package com.example.gameplay

import com.example.model.Club
import com.example.model.CommentaryEvent
import com.example.model.MatchChance
import com.example.model.MatchChanceType
import com.example.model.MatchResult
import com.example.model.PitchPlayer
import com.example.model.Player
import kotlin.random.Random

object MatchEngine {

  fun generateChances(player: Player, opponent: Club): List<MatchChance> {
    val count = when {
      player.relationships.coach > 75 -> 4
      player.relationships.coach > 45 -> 3
      else -> 2
    }

    val minutes = mutableListOf<Int>()
    while (minutes.size < count) {
      val min = Random.nextInt(12, 88)
      if (!minutes.any { kotlin.math.abs(it - min) < 15 }) {
        minutes.add(min)
      }
    }
    minutes.sort()

    return minutes.mapIndexed { index, minute ->
      when (index) {
        0 -> createPassingChance(minute, opponent)
        1 -> createOpenPlayShotChance(minute, opponent)
        2 -> if (Random.nextBoolean()) createFreeKickChance(minute, opponent) else createOpenPlayShotChance(minute, opponent)
        else -> if (Random.nextInt(100) < 35) createPenaltyChance(minute, opponent) else createTackleChance(minute, opponent)
      }
    }
  }

  private fun createOpenPlayShotChance(minute: Int, opponent: Club): MatchChance {
    val ballX = 0.5f + (Random.nextFloat() - 0.5f) * 0.25f
    val ballY = 0.65f + Random.nextFloat() * 0.15f
    return MatchChance(
      id = "shot_$minute",
      minute = minute,
      type = MatchChanceType.OPEN_PLAY_SHOT,
      promptMessage = "Finalização na área! Mire no ângulo e escolha o ponto de contato na bola!",
      userPosition = Pair(ballX, ballY + 0.04f),
      ballPosition = Pair(ballX, ballY),
      teammates = listOf(
        PitchPlayer("t1", 0.25f, 0.45f, isTeammate = true, number = 7, name = "Ponta"),
        PitchPlayer("t2", 0.78f, 0.48f, isTeammate = true, number = 11, name = "Lateral")
      ),
      defenders = listOf(
        PitchPlayer("d1", 0.42f, 0.38f, number = 3, name = "Zagueiro"),
        PitchPlayer("d2", 0.58f, 0.40f, number = 4, name = "Zagueiro")
      ),
      goalkeeper = PitchPlayer("gk", 0.50f, 0.14f, isGoalkeeper = true, number = 1, name = "Goleiro")
    )
  }

  private fun createPassingChance(minute: Int, opponent: Club): MatchChance {
    val ballX = 0.48f
    val ballY = 0.75f
    return MatchChance(
      id = "pass_$minute",
      minute = minute,
      type = MatchChanceType.PASS_TO_TEAMMATE,
      promptMessage = "Contra-ataque rápido! Encontre o atacante livre entre os defensores!",
      userPosition = Pair(ballX, ballY + 0.03f),
      ballPosition = Pair(ballX, ballY),
      teammates = listOf(
        PitchPlayer("t1", 0.32f, 0.38f, isTeammate = true, number = 9, name = "Artilheiro"),
        PitchPlayer("t2", 0.72f, 0.44f, isTeammate = true, number = 7, name = "Ponta")
      ),
      defenders = listOf(
        PitchPlayer("d1", 0.48f, 0.52f, number = 5, name = "Volante"),
        PitchPlayer("d2", 0.30f, 0.28f, number = 2, name = "Lateral")
      ),
      goalkeeper = PitchPlayer("gk", 0.50f, 0.14f, isGoalkeeper = true, number = 1, name = "Goleiro")
    )
  }

  private fun createFreeKickChance(minute: Int, opponent: Club): MatchChance {
    val ballX = 0.52f + (Random.nextFloat() - 0.5f) * 0.18f
    val ballY = 0.62f
    return MatchChance(
      id = "fk_$minute",
      minute = minute,
      type = MatchChanceType.FREE_KICK,
      promptMessage = "Falta perigosa na entrada da área! Use curva para contornar a barreira!",
      userPosition = Pair(ballX - 0.03f, ballY + 0.05f),
      ballPosition = Pair(ballX, ballY),
      teammates = listOf(
        PitchPlayer("t1", 0.25f, 0.35f, isTeammate = true, number = 8, name = "Meia")
      ),
      defenders = listOf(
        PitchPlayer("w1", 0.44f, 0.38f, number = 4, name = "Barreira"),
        PitchPlayer("w2", 0.49f, 0.38f, number = 5, name = "Barreira"),
        PitchPlayer("w3", 0.54f, 0.38f, number = 3, name = "Barreira")
      ),
      goalkeeper = PitchPlayer("gk", 0.58f, 0.14f, isGoalkeeper = true, number = 1, name = "Goleiro")
    )
  }

  private fun createPenaltyChance(minute: Int, opponent: Club): MatchChance {
    val ballX = 0.50f
    val ballY = 0.55f
    return MatchChance(
      id = "pen_$minute",
      minute = minute,
      type = MatchChanceType.PENALTY,
      promptMessage = "PÊNALTI DECISIVO! Chute com precisão e desloque o goleiro!",
      userPosition = Pair(ballX, ballY + 0.06f),
      ballPosition = Pair(ballX, ballY),
      teammates = emptyList(),
      defenders = emptyList(),
      goalkeeper = PitchPlayer("gk", 0.50f, 0.14f, isGoalkeeper = true, number = 1, name = "Goleiro")
    )
  }

  private fun createTackleChance(minute: Int, opponent: Club): MatchChance {
    val ballX = 0.52f
    val ballY = 0.68f
    return MatchChance(
      id = "tackle_$minute",
      minute = minute,
      type = MatchChanceType.TACKLE_INTERCEPTION,
      promptMessage = "O rival avança em velocidade! Intercepte a jogada no tempo certo!",
      userPosition = Pair(ballX - 0.08f, ballY + 0.02f),
      ballPosition = Pair(ballX, ballY),
      teammates = listOf(
        PitchPlayer("t1", 0.35f, 0.80f, isTeammate = true, number = 6, name = "Zagueiro")
      ),
      defenders = listOf(
        PitchPlayer("d1", ballX, ballY - 0.02f, number = 10, name = "Atacante Rival")
      ),
      goalkeeper = PitchPlayer("gk", 0.50f, 0.14f, isGoalkeeper = true, number = 1, name = "Goleiro")
    )
  }

  fun generateSimulatedEvents(homeClub: Club, awayClub: Club, playerMinuteChances: List<Int>): List<CommentaryEvent> {
    val events = mutableListOf<CommentaryEvent>()
    events.add(CommentaryEvent(1, "Apita o árbitro! Começa o duelo entre ${homeClub.name} e ${awayClub.name}!", isImportant = true))

    val candidateMinutes = (5..89).filter { m ->
      !playerMinuteChances.any { kotlin.math.abs(it - m) < 5 }
    }.shuffled().take(6)

    for (m in candidateMinutes.sorted()) {
      when (Random.nextInt(5)) {
        0 -> events.add(CommentaryEvent(m, "${awayClub.name} pressiona na marcação alta e ganha escanteio perigoso."))
        1 -> events.add(CommentaryEvent(m, "Linda troca de passes do nosso meio-campo empolga a torcida no estádio!"))
        2 -> events.add(CommentaryEvent(m, "O adversário chuta forte de fora da área mas nosso goleiro espalma com segurança!"))
        3 -> {
          val isOpponentGoal = Random.nextInt(100) < 30
          if (isOpponentGoal) {
            events.add(CommentaryEvent(m, "Gol do ${awayClub.name}! Desatenção na zaga e o rival abre o placar!", isGoal = true, isImportant = true))
          } else {
            events.add(CommentaryEvent(m, "Cartão amarelo aplicado após falta dura no círculo central."))
          }
        }
        else -> events.add(CommentaryEvent(m, "O técnico gesticula na linha lateral pedindo mais velocidade nas pontas."))
      }
    }

    events.add(CommentaryEvent(45, "Fim do primeiro tempo! Equipes conversam nos vestiários.", isImportant = true))
    events.add(CommentaryEvent(90, "Fim de papo! Apita o árbitro encerrando uma partida eletrizante!", isImportant = true))
    events.sortBy { it.minute }
    return events
  }

  fun calculatePostMatch(
    player: Player,
    opponent: Club,
    playerGoals: Int,
    playerAssists: Int,
    opponentGoalsSimulated: Int,
    chancesTotal: Int,
    chancesSuccess: Int
  ): MatchResult {
    val totalTeamGoals = playerGoals + (if (Random.nextInt(100) < 40) 1 else 0)
    val totalOpponentGoals = opponentGoalsSimulated

    val successRate = if (chancesTotal > 0) chancesSuccess.toFloat() / chancesTotal else 0.5f
    val baseRating = 5.0f + (playerGoals * 2.0f) + (playerAssists * 1.2f) + (successRate * 2.0f)
    val finalRating = kotlin.math.min(10.0f, kotlin.math.max(3.0f, baseRating))

    val won = totalTeamGoals > totalOpponentGoals
    val drawn = totalTeamGoals == totalOpponentGoals

    val earnedWage = player.wagePerMatch
    val earnedBonus = (if (won) player.winBonus else if (drawn) player.winBonus / 2 else 0) + (playerGoals * player.goalBonus)
    val staminaReduction = (player.skills.stamina * 0.18f).toInt()
    val energyCost = ((28 + (finalRating * 1.0f).toInt()) - staminaReduction).coerceIn(15, 38)

    val coachDelta = if (playerGoals > 0 || won) 8 else if (finalRating < 5.5f) -6 else 2
    val teammatesDelta = if (playerAssists > 0 || successRate > 0.6f) 7 else if (successRate < 0.3f) -5 else 2
    val fansDelta = if (playerGoals >= 2) 15 else if (playerGoals == 1 || won) 8 else -4

    val headline = when {
      playerGoals >= 3 -> "SHOW HISTÓRICO! Hat-trick espetacular de ${player.name}!"
      playerGoals == 2 -> "DECISIVO! ${player.name} comanda a vitória com dois golaços!"
      playerGoals == 1 && won -> "HERÓI DO JOGO! Gol solitário de ${player.name} garante três pontos!"
      playerGoals == 1 -> "BRILHOU! ${player.name} balança as redes em partida disputada!"
      won -> "TRABALHO COLETIVO! Equipe vence com grande dedicação tática!"
      else -> "DIA DIFÍCIL: Equipe sai de campo derrotada e promete reação."
    }

    val subHeadline = "Com nota %.1f, o camisa %d mostrou muita garra contra o %s.".format(finalRating, player.number, opponent.name)

    return MatchResult(
      opponentClub = opponent,
      userGoals = totalTeamGoals,
      opponentGoals = totalOpponentGoals,
      playerGoals = playerGoals,
      playerAssists = playerAssists,
      playerRating = finalRating,
      wageEarned = earnedWage,
      bonusEarned = earnedBonus,
      energyCost = energyCost,
      headline = headline,
      subHeadline = subHeadline,
      coachDelta = coachDelta,
      teammatesDelta = teammatesDelta,
      fansDelta = fansDelta
    )
  }
}
