package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CareerRepository
import com.example.gameplay.MatchEngine
import com.example.gameplay.ShotOutcome
import com.example.model.CareerProgression
import com.example.model.Club
import com.example.model.ClubContractOffer
import com.example.model.CommentaryEvent
import com.example.model.DefaultClubs
import com.example.model.DefaultShopItems
import com.example.model.LifestyleActivity
import com.example.model.LifestyleManager
import com.example.model.MatchChance
import com.example.model.MatchResult
import com.example.model.Player
import com.example.model.PlayerPosition
import com.example.model.PlayerRelationships
import com.example.model.PlayerSkills
import com.example.model.ShopItem
import com.example.model.SkillType
import com.example.model.TacticalActionType
import com.example.model.TrainingIntensity
import com.example.util.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class MatchPhase {
  NOT_IN_MATCH,
  PRE_MATCH,
  SIMULATING_TICKER,
  INTERACTIVE_CHANCE,
  GOAL_CELEBRATION,
  POST_MATCH
}

data class LiveMatchState(
  val opponentClub: Club,
  val currentMinute: Int = 1,
  val homeGoals: Int = 0,
  val awayGoals: Int = 0,
  val playerGoals: Int = 0,
  val playerAssists: Int = 0,
  val currentEnergy: Int = 100,
  val selectedAction: TacticalActionType = TacticalActionType.POWER_SHOT,
  val commentary: List<CommentaryEvent> = emptyList(),
  val chances: List<MatchChance> = emptyList(),
  val currentChanceIndex: Int = 0,
  val phase: MatchPhase = MatchPhase.PRE_MATCH,
  val lastOutcomeMessage: String? = null,
  val postMatchResult: MatchResult? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: CareerRepository
  val soundManager = SoundManager(application)

  private val _player = MutableStateFlow<Player?>(null)
  val player: StateFlow<Player?> = _player.asStateFlow()

  private val _clubs = MutableStateFlow<List<Club>>(DefaultClubs.getInitialClubs())
  val clubs: StateFlow<List<Club>> = _clubs.asStateFlow()

  private val _ownedItems = MutableStateFlow<Set<String>>(setOf("boots_basic"))
  val ownedItems: StateFlow<Set<String>> = _ownedItems.asStateFlow()

  private val _liveMatch = MutableStateFlow<LiveMatchState?>(null)
  val liveMatch: StateFlow<LiveMatchState?> = _liveMatch.asStateFlow()

  private var matchTickerJob: Job? = null

  init {
    val db = AppDatabase.getDatabase(application)
    repository = CareerRepository(db.careerDao())

    viewModelScope.launch {
      repository.initializeClubsIfEmpty()

      launch {
        repository.careerPlayerFlow.collect { savedPlayer ->
          if (savedPlayer != null) {
            _player.value = savedPlayer
          } else {
            // Initial default player
            val initial = Player(
              name = "Gabriel",
              nickname = "Craque",
              age = 17,
              nationality = "Brasil",
              preferredFoot = "Destro",
              position = PlayerPosition.ATACANTE,
              number = 10,
              currentClubId = "vila_real"
            )
            _player.value = initial
            repository.savePlayerAndItems(initial, setOf("boots_basic"))
          }
        }
      }

      launch {
        repository.clubsFlow.collect { clubList ->
          if (clubList.isNotEmpty()) {
            _clubs.value = clubList
          }
        }
      }

      launch {
        repository.ownedItemsFlow.collect { items ->
          if (items.isNotEmpty()) {
            _ownedItems.value = items + "boots_basic"
          }
        }
      }
    }
  }

  fun getPlayerClub(): Club {
    val clubId = _player.value?.currentClubId ?: "vila_real"
    return _clubs.value.find { it.id == clubId } ?: DefaultClubs.getInitialClubs().first()
  }

  fun getNextOpponent(): Club {
    val myClub = getPlayerClub()
    val available = _clubs.value.filter { it.id != myClub.id }
    return available.randomOrNull() ?: DefaultClubs.getInitialClubs()[1]
  }

  // --- MATCH MANAGEMENT ---

  fun startMatch(opponent: Club) {
    val curPlayer = _player.value ?: return
    soundManager.playWhistle()

    val chances = MatchEngine.generateChances(curPlayer, opponent)
    val simulatedEvents = MatchEngine.generateSimulatedEvents(getPlayerClub(), opponent, chances.map { it.minute })
    val availableActions = TacticalActionType.getAvailableActions(curPlayer.energy)
    val defaultAction = availableActions.firstOrNull() ?: TacticalActionType.DESPERATE_SHOT

    _liveMatch.value = LiveMatchState(
      opponentClub = opponent,
      currentMinute = 1,
      homeGoals = 0,
      awayGoals = 0,
      playerGoals = 0,
      playerAssists = 0,
      currentEnergy = curPlayer.energy,
      selectedAction = defaultAction,
      commentary = simulatedEvents.filter { it.minute == 1 },
      chances = chances,
      currentChanceIndex = 0,
      phase = MatchPhase.SIMULATING_TICKER
    )

    runMatchSimulationLoop(simulatedEvents)
  }

  private fun runMatchSimulationLoop(allSimulatedEvents: List<CommentaryEvent>) {
    matchTickerJob?.cancel()
    matchTickerJob = viewModelScope.launch {
      var min = _liveMatch.value?.currentMinute ?: 1
      val state = _liveMatch.value ?: return@launch
      val chances = state.chances

      while (min <= 90) {
        delay(320)
        min += 2

        val curState = _liveMatch.value ?: break
        // Check if there is a chance scheduled for this minute
        val chanceIdx = curState.currentChanceIndex
        if (chanceIdx < chances.size && min >= chances[chanceIdx].minute) {
          // Pause simulation for user interactive chance!
          soundManager.playWhistle()
          _liveMatch.value = curState.copy(
            currentMinute = chances[chanceIdx].minute,
            phase = MatchPhase.INTERACTIVE_CHANCE
          )
          return@launch
        }

        // Add any simulated commentary events up to this minute
        val newEvents = allSimulatedEvents.filter { it.minute in (min - 2)..min }
        var updatedAwayGoals = curState.awayGoals
        for (ev in newEvents) {
          if (ev.isGoal) updatedAwayGoals++
        }

        _liveMatch.value = curState.copy(
          currentMinute = min.coerceAtMost(90),
          awayGoals = updatedAwayGoals,
          commentary = (curState.commentary + newEvents).distinctBy { it.minute to it.text }
        )
      }

      // Reached 90 min! Match ends!
      finishMatch()
    }
  }

  fun onShotOutcome(outcome: ShotOutcome) {
    val curState = _liveMatch.value ?: return
    val curPlayer = _player.value ?: return
    val chance = curState.chances.getOrNull(curState.currentChanceIndex) ?: return

    var newHomeGoals = curState.homeGoals
    var newPlayerGoals = curState.playerGoals
    var newPlayerAssists = curState.playerAssists
    var outcomeMessage = ""
    var isGoal = false

    when (outcome) {
      ShotOutcome.GOAL -> {
        soundManager.playGoal()
        newHomeGoals++
        newPlayerGoals++
        outcomeMessage = "GOOOOOOOOOOL! Finalização espetacular no fundo das redes!"
        isGoal = true
      }
      ShotOutcome.PASS_COMPLETE -> {
        soundManager.playSuccess()
        newPlayerAssists++
        newHomeGoals++
        outcomeMessage = "PASSE PERFEITO! O atacante domina e estufa o barbante! Assistência sua!"
        isGoal = true
      }
      ShotOutcome.SAVED -> {
        soundManager.playClick()
        outcomeMessage = "DEFESAAAAÇA! O goleiro voa na ponta dos dedos e evita o gol!"
      }
      ShotOutcome.POST -> {
        soundManager.playCrossbar()
        outcomeMessage = "NA TRAVE! Uma bomba estrondosa faz o travessão tremer!"
      }
      ShotOutcome.WIDE -> {
        soundManager.playClick()
        outcomeMessage = "PRA FORA! O chute tirou tinta da trave e foi para a linha de fundo."
      }
      ShotOutcome.INTERCEPTED -> {
        soundManager.playClick()
        outcomeMessage = "INTERCEPTADO! A zaga adversária cortou o passe no último instante."
      }
      ShotOutcome.TACKLE_WON -> {
        soundManager.playSuccess()
        outcomeMessage = "DESARME LIMPO! Você rouba a bola de forma perfeita e arma o contragolpe!"
      }
      ShotOutcome.TACKLE_LOST -> {
        soundManager.playClick()
        outcomeMessage = "DRIBLADO! O atacante adversário conseguiu escapar da marcação."
      }
    }

    val event = CommentaryEvent(
      minute = chance.minute,
      text = "${chance.minute}' - $outcomeMessage",
      isGoal = isGoal,
      isPlayerInvolved = true,
      isImportant = true
    )

    val action = curState.selectedAction
    val remainingEnergy = (curState.currentEnergy - action.energyCost).coerceAtLeast(0)
    val nextAvailableActions = TacticalActionType.getAvailableActions(remainingEnergy)
    val nextSelectedAction = nextAvailableActions.firstOrNull() ?: TacticalActionType.DESPERATE_SHOT

    val nextIdx = curState.currentChanceIndex + 1

    if (isGoal) {
      _liveMatch.value = curState.copy(
        homeGoals = newHomeGoals,
        playerGoals = newPlayerGoals,
        playerAssists = newPlayerAssists,
        currentEnergy = remainingEnergy,
        selectedAction = nextSelectedAction,
        lastOutcomeMessage = outcomeMessage,
        phase = MatchPhase.GOAL_CELEBRATION,
        commentary = curState.commentary + event,
        currentChanceIndex = nextIdx
      )
    } else {
      _liveMatch.value = curState.copy(
        lastOutcomeMessage = outcomeMessage,
        currentEnergy = remainingEnergy,
        selectedAction = nextSelectedAction,
        commentary = curState.commentary + event,
        currentChanceIndex = nextIdx,
        phase = MatchPhase.SIMULATING_TICKER
      )
      resumeMatchTicker()
    }
  }

  fun selectTacticalAction(action: TacticalActionType) {
    val curState = _liveMatch.value ?: return
    if (curState.currentEnergy >= action.energyCost || action == TacticalActionType.DESPERATE_SHOT) {
      soundManager.playClick()
      _liveMatch.value = curState.copy(selectedAction = action)
    }
  }

  fun resumeMatchAfterGoal() {
    val curState = _liveMatch.value ?: return
    _liveMatch.value = curState.copy(phase = MatchPhase.SIMULATING_TICKER)
    resumeMatchTicker()
  }

  private fun resumeMatchTicker() {
    val allSimulated = MatchEngine.generateSimulatedEvents(
      getPlayerClub(),
      _liveMatch.value?.opponentClub ?: return,
      _liveMatch.value?.chances?.map { it.minute } ?: emptyList()
    )
    runMatchSimulationLoop(allSimulated)
  }

  private fun finishMatch() {
    matchTickerJob?.cancel()
    soundManager.playWhistle()

    val curState = _liveMatch.value ?: return
    val curPlayer = _player.value ?: return

    val rawResult = MatchEngine.calculatePostMatch(
      player = curPlayer,
      opponent = curState.opponentClub,
      playerGoals = curState.playerGoals,
      playerAssists = curState.playerAssists,
      opponentGoalsSimulated = curState.awayGoals,
      chancesTotal = curState.chances.size,
      chancesSuccess = curState.playerGoals + curState.playerAssists
    )

    // Calculate skill attributes and career progression from match performance
    val (evolvedPlayer, evolutionReport) = CareerProgression.calculateMatchEvolution(
      player = curPlayer,
      goals = rawResult.playerGoals,
      assists = rawResult.playerAssists,
      rating = rawResult.playerRating,
      successChances = rawResult.playerGoals + rawResult.playerAssists,
      totalChances = curState.chances.size
    )

    val result = rawResult.copy(evolutionReport = evolutionReport)

    // Update Player Career Stats, Money & Rest
    // Energy reflects all tactical decisions and actions taken during the match!
    val finalEnergy = curState.currentEnergy.coerceIn(5, evolvedPlayer.maxEnergy)

    val updatedPlayer = evolvedPlayer.copy(
      money = evolvedPlayer.money + result.wageEarned + result.bonusEarned,
      energy = finalEnergy,
      matchesPlayed = evolvedPlayer.matchesPlayed + 1,
      goalsScored = evolvedPlayer.goalsScored + result.playerGoals,
      assistsGiven = evolvedPlayer.assistsGiven + result.playerAssists,
      mvpAwards = evolvedPlayer.mvpAwards + (if (result.playerRating >= 8.5f) 1 else 0),
      seasonMatches = evolvedPlayer.seasonMatches + 1,
      seasonGoals = evolvedPlayer.seasonGoals + result.playerGoals,
      seasonAssists = evolvedPlayer.seasonAssists + result.playerAssists,
      relationships = evolvedPlayer.relationships.copy(
        coach = (evolvedPlayer.relationships.coach + result.coachDelta).coerceIn(0, 100),
        teammates = (evolvedPlayer.relationships.teammates + result.teammatesDelta).coerceIn(0, 100),
        fans = (evolvedPlayer.relationships.fans + result.fansDelta).coerceIn(0, 100)
      )
    )

    // Update League Standings
    val updatedClubs = _clubs.value.map { club ->
      if (club.id == updatedPlayer.currentClubId) {
        val won = result.userGoals > result.opponentGoals
        val drawn = result.userGoals == result.opponentGoals
        club.copy(
          played = club.played + 1,
          won = club.won + (if (won) 1 else 0),
          drawn = club.drawn + (if (drawn) 1 else 0),
          lost = club.lost + (if (!won && !drawn) 1 else 0),
          goalsFor = club.goalsFor + result.userGoals,
          goalsAgainst = club.goalsAgainst + result.opponentGoals,
          points = club.points + (if (won) 3 else if (drawn) 1 else 0)
        )
      } else if (club.id == curState.opponentClub.id) {
        val oppWon = result.opponentGoals > result.userGoals
        val oppDrawn = result.opponentGoals == result.userGoals
        club.copy(
          played = club.played + 1,
          won = club.won + (if (oppWon) 1 else 0),
          drawn = club.drawn + (if (oppDrawn) 1 else 0),
          lost = club.lost + (if (!oppWon && !oppDrawn) 1 else 0),
          goalsFor = club.goalsFor + result.opponentGoals,
          goalsAgainst = club.goalsAgainst + result.userGoals,
          points = club.points + (if (oppWon) 3 else if (oppDrawn) 1 else 0)
        )
      } else {
        club
      }
    }

    _player.value = updatedPlayer
    _clubs.value = updatedClubs
    _liveMatch.value = curState.copy(
      phase = MatchPhase.POST_MATCH,
      postMatchResult = result
    )

    viewModelScope.launch {
      repository.savePlayerAndItems(updatedPlayer, _ownedItems.value)
      repository.saveClubs(updatedClubs)
    }
  }

  fun exitPostMatch() {
    _liveMatch.value = null
  }

  // --- TRAINING & SKILLS ---

  fun trainSkill(
    skillType: SkillType,
    intensity: TrainingIntensity = TrainingIntensity.BALANCED,
    onSuccess: (String) -> Unit,
    onFail: (String) -> Unit
  ) {
    val p = _player.value ?: return
    if (p.energy < intensity.energyCost) {
      onFail("Energia insuficiente! Precisa de ${intensity.energyCost} HP (você tem ${p.energy} HP). Descanse um dia ou tome um energético.")
      return
    }

    soundManager.playKick()
    val result = CareerProgression.applyTraining(p, skillType, intensity)

    _player.value = result.player
    viewModelScope.launch {
      repository.savePlayerAndItems(result.player, _ownedItems.value)
    }
    onSuccess(result.message)
  }

  fun trainSkill(skillTypeKey: String, onSuccess: (String) -> Unit, onFail: (String) -> Unit) {
    val skillType = SkillType.fromId(skillTypeKey)
    trainSkill(skillType, TrainingIntensity.BALANCED, onSuccess, onFail)
  }

  fun restPlayer(onSuccess: (String) -> Unit) {
    val p = _player.value ?: return
    soundManager.playSuccess()
    val (restedPlayer, msg) = CareerProgression.applyRestDay(p)
    _player.value = restedPlayer
    viewModelScope.launch {
      repository.savePlayerAndItems(restedPlayer, _ownedItems.value)
    }
    onSuccess(msg)
  }

  // --- SHOP & LIFESTYLE ---

  fun buyShopItem(item: ShopItem, onSuccess: () -> Unit, onFail: (String) -> Unit) {
    val p = _player.value ?: return
    if (p.money < item.price) {
      onFail("Saldo insuficiente! R$ ${item.price} necessários.")
      return
    }

    soundManager.playSuccess()
    val updatedMoney = p.money - item.price
    var updatedEnergy = p.energy
    var updatedSkills = p.skills
    var updatedRels = p.relationships
    var equippedBoots = p.equippedBootsId
    val newOwned = _ownedItems.value.toMutableSet()

    if (item.isConsumable) {
      updatedEnergy = (updatedEnergy + item.energyRestored).coerceAtMost(p.maxEnergy)
    } else {
      newOwned.add(item.id)
      if (item.category == com.example.model.ShopCategory.BOOTS) {
        equippedBoots = item.id
      }
      updatedSkills = updatedSkills.copy(
        power = (updatedSkills.power + item.powerBonus).coerceAtMost(99),
        curl = (updatedSkills.curl + item.curlBonus).coerceAtMost(99),
        accuracy = (updatedSkills.accuracy + item.accuracyBonus).coerceAtMost(99),
        vision = (updatedSkills.vision + item.visionBonus).coerceAtMost(99),
        pace = (updatedSkills.pace + item.paceBonus).coerceAtMost(99)
      )
      updatedRels = updatedRels.copy(
        coach = (updatedRels.coach + item.coachBonus).coerceIn(0, 100),
        teammates = (updatedRels.teammates + item.teammatesBonus).coerceIn(0, 100),
        fans = (updatedRels.fans + item.fansBonus).coerceIn(0, 100),
        sponsor = (updatedRels.sponsor + item.sponsorBonus).coerceIn(0, 100),
        partner = (updatedRels.partner + item.partnerBonus).coerceIn(0, 100)
      )
    }

    val updatedPlayer = p.copy(
      money = updatedMoney,
      energy = updatedEnergy,
      skills = updatedSkills,
      relationships = updatedRels,
      equippedBootsId = equippedBoots
    )

    _player.value = updatedPlayer
    _ownedItems.value = newOwned

    viewModelScope.launch {
      repository.savePlayerAndItems(updatedPlayer, newOwned)
    }
    onSuccess()
  }

  // --- RELATIONSHIP SOCIAL INTERACTIONS ---

  fun socialInteract(category: String, onSuccess: (String) -> Unit, onFail: (String) -> Unit) {
    val p = _player.value ?: return
    if (p.energy < 10) {
      onFail("Muito cansado para atividades sociais! Recupere energia.")
      return
    }

    soundManager.playClick()
    var updatedRels = p.relationships
    var message = ""

    when (category) {
      "coach" -> {
        updatedRels = updatedRels.copy(coach = (updatedRels.coach + 8).coerceAtMost(100))
        message = "Conversa tática com o treinador! Ele aprovou seu compromisso tático."
      }
      "teammates" -> {
        updatedRels = updatedRels.copy(teammates = (updatedRels.teammates + 10).coerceAtMost(100))
        message = "Churrasco com os companheiros de elenco! O entrosamento aumentou."
      }
      "fans" -> {
        updatedRels = updatedRels.copy(fans = (updatedRels.fans + 12).coerceAtMost(100))
        message = "Sessão de autógrafos com a torcida! Os torcedores foram ao delírio."
      }
      "sponsor" -> {
        val sponsorBonus = 350L
        _player.value = p.copy(money = p.money + sponsorBonus)
        updatedRels = updatedRels.copy(sponsor = (updatedRels.sponsor + 8).coerceAtMost(100))
        message = "Gravação de comercial esportivo! Bônus de R$ 350 depositado."
      }
      "partner" -> {
        updatedRels = updatedRels.copy(partner = (updatedRels.partner + 12).coerceAtMost(100))
        message = "Jantar romântico! Sua parceira está radiante com seu sucesso."
      }
    }

    val updatedPlayer = (_player.value ?: p).copy(
      relationships = updatedRels,
      energy = p.energy - 10
    )
    _player.value = updatedPlayer
    viewModelScope.launch {
      repository.savePlayerAndItems(updatedPlayer, _ownedItems.value)
    }
    onSuccess(message)
  }

  // --- CAREER RESTART / CREATION ---

  fun resetOrNewCareer(
    name: String,
    nickname: String,
    pos: PlayerPosition,
    foot: String,
    age: Int = 17,
    clubId: String = "vila_real",
    startWithTrial: Boolean = false
  ) {
    soundManager.playSuccess()
    val initial = Player(
      name = name.ifBlank { "Gabriel" },
      nickname = nickname.ifBlank { "Fenômeno" },
      age = age,
      position = pos,
      preferredFoot = foot,
      currentClubId = clubId,
      isTrialCompleted = !startWithTrial,
      wagePerMatch = if (startWithTrial) 0L else 350L,
      money = if (startWithTrial) 300L else 1200L,
      happiness = 85
    )
    _player.value = initial
    _ownedItems.value = setOf("boots_basic")
    viewModelScope.launch {
      repository.resetCareer()
      repository.savePlayerAndItems(initial, setOf("boots_basic"))
    }
  }

  fun resetOrNewCareer(name: String, nickname: String, pos: PlayerPosition, foot: String, clubId: String) {
    resetOrNewCareer(name, nickname, pos, foot, 17, clubId, false)
  }

  fun signContractOffer(offer: ClubContractOffer) {
    val p = _player.value ?: return
    soundManager.playSuccess()

    val updatedPlayer = p.copy(
      currentClubId = offer.club.id,
      wagePerMatch = offer.wagePerMatch,
      winBonus = offer.winBonus,
      goalBonus = offer.goalBonus,
      contractYears = offer.contractYears,
      isTrialCompleted = true,
      money = p.money + 500L,
      happiness = 95
    )

    _player.value = updatedPlayer
    viewModelScope.launch {
      repository.savePlayerAndItems(updatedPlayer, _ownedItems.value)
    }
  }

  // --- LIFESTYLE MANAGEMENT ---

  fun executeLifestyleActivity(activity: LifestyleActivity, onSuccess: (String) -> Unit, onFail: (String) -> Unit) {
    val p = _player.value ?: return
    val (updatedPlayer, msg) = LifestyleManager.executeActivity(p, activity)
    if (updatedPlayer == p && (p.energy < activity.energyCost || p.money < activity.moneyCost)) {
      onFail(msg)
      return
    }

    soundManager.playSuccess()
    _player.value = updatedPlayer
    viewModelScope.launch {
      repository.savePlayerAndItems(updatedPlayer, _ownedItems.value)
    }
    onSuccess(msg)
  }
}
