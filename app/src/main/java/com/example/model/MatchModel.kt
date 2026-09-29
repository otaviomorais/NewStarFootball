package com.example.model

enum class MatchChanceType(val title: String) {
  OPEN_PLAY_SHOT("Finalização a Gol"),
  PASS_TO_TEAMMATE("Passe Decisivo"),
  FREE_KICK("Cobrança de Falta"),
  PENALTY("Pênalti Máximo"),
  TACKLE_INTERCEPTION("Desarme & Roubada de Bola")
}

enum class TacticalActionType(
  val id: String,
  val label: String,
  val shortLabel: String,
  val energyCost: Int,
  val description: String,
  val powerMultiplier: Float,
  val accuracyBonus: Int,
  val curlBonus: Int
) {
  POWER_SHOT(
    id = "power_shot",
    label = "Chute Forte",
    shortLabel = "CHUTE",
    energyCost = 12,
    description = "Bomba de pé cheio em direção à meta. Alta velocidade e potência.",
    powerMultiplier = 1.25f,
    accuracyBonus = 0,
    curlBonus = -5
  ),
  PASS(
    id = "pass",
    label = "Passe Decisivo",
    shortLabel = "PASSE",
    energyCost = 6,
    description = "Passe rasteiro ou cruzamento para o companheiro desmarcado.",
    powerMultiplier = 0.85f,
    accuracyBonus = 15,
    curlBonus = 0
  ),
  PLACED_SHOT(
    id = "placed_shot",
    label = "Chute Colocado",
    shortLabel = "COLOCADO",
    energyCost = 10,
    description = "Finalização sutil de chapa com curva acentuada contornando o goleiro.",
    powerMultiplier = 1.0f,
    accuracyBonus = 10,
    curlBonus = 25
  ),
  DRIBBLE(
    id = "dribble",
    label = "Drible & Avanço",
    shortLabel = "DRIBLE",
    energyCost = 8,
    description = "Corta o zagueiro mais próximo para abrir ângulo limpo de tiro.",
    powerMultiplier = 1.05f,
    accuracyBonus = 8,
    curlBonus = 5
  ),
  DESPERATE_SHOT(
    id = "desperate",
    label = "Chute no Limite",
    shortLabel = "EXAUSTÃO",
    energyCost = 0,
    description = "Tentativa em exaustão física com a energia no fim. Menor força e precisão.",
    powerMultiplier = 0.65f,
    accuracyBonus = -20,
    curlBonus = -10
  );

  companion object {
    fun getAvailableActions(currentEnergy: Int): List<TacticalActionType> {
      val actions = mutableListOf<TacticalActionType>()
      if (currentEnergy >= POWER_SHOT.energyCost) actions.add(POWER_SHOT)
      if (currentEnergy >= PASS.energyCost) actions.add(PASS)
      if (currentEnergy >= PLACED_SHOT.energyCost) actions.add(PLACED_SHOT)
      if (currentEnergy >= DRIBBLE.energyCost) actions.add(DRIBBLE)
      if (actions.isEmpty()) actions.add(DESPERATE_SHOT)
      return actions
    }
  }
}

data class PitchPlayer(
  val id: String,
  val x: Float, // 0.0 to 1.0 (width fraction)
  val y: Float, // 0.0 to 1.0 (height fraction: 0 is top / goal, 1 is player side)
  val isUser: Boolean = false,
  val isTeammate: Boolean = false,
  val isGoalkeeper: Boolean = false,
  val number: Int = 10,
  val name: String = ""
)

data class MatchChance(
  val id: String,
  val minute: Int,
  val type: MatchChanceType,
  val promptMessage: String,
  val userPosition: Pair<Float, Float>,
  val ballPosition: Pair<Float, Float>,
  val teammates: List<PitchPlayer>,
  val defenders: List<PitchPlayer>,
  val goalkeeper: PitchPlayer,
  val goalBounds: Pair<Float, Float> = Pair(0.32f, 0.68f) // X bounds of the goal
)

data class CommentaryEvent(
  val minute: Int,
  val text: String,
  val isGoal: Boolean = false,
  val isPlayerInvolved: Boolean = false,
  val isImportant: Boolean = false
)

data class MatchResult(
  val opponentClub: Club,
  val userGoals: Int,
  val opponentGoals: Int,
  val playerGoals: Int,
  val playerAssists: Int,
  val playerRating: Float, // 1.0 to 10.0
  val wageEarned: Long,
  val bonusEarned: Long,
  val energyCost: Int,
  val headline: String,
  val subHeadline: String,
  val coachDelta: Int,
  val teammatesDelta: Int,
  val fansDelta: Int,
  val evolutionReport: SkillEvolutionReport? = null
)
