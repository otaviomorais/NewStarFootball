package com.example.model

enum class TrainingIntensity(
  val label: String,
  val energyCost: Int,
  val baseXp: Int,
  val description: String
) {
  LIGHT(
    label = "Treino Leve",
    energyCost = 8,
    baseXp = 30,
    description = "Foco em recuperação ativa e toques rápidos. Baixo desgaste físico."
  ),
  BALANCED(
    label = "Treino Padrão",
    energyCost = 15,
    baseXp = 50,
    description = "Carga tática e técnica equilibrada com o restante do elenco."
  ),
  INTENSIVE(
    label = "Treino Intensivo",
    energyCost = 25,
    baseXp = 90,
    description = "Exigência física máxima, com chance de bônus de desenvolvimento acelerado."
  )
}

data class TrainingDrill(
  val id: String,
  val skillType: SkillType,
  val title: String,
  val description: String,
  val recommendedIntensity: TrainingIntensity = TrainingIntensity.BALANCED
)

data class TrainingResult(
  val player: Player,
  val skillType: SkillType,
  val xpGained: Int,
  val didLevelUp: Boolean,
  val newLevel: Int,
  val energySpent: Int,
  val maxEnergyExpanded: Boolean,
  val newMaxEnergy: Int,
  val message: String
)

data class SkillEvolutionReport(
  val xpGainedMap: Map<String, Int>,
  val leveledUpSkills: List<Pair<String, Int>>, // Skill name to new value
  val careerXpGained: Int,
  val careerLevelUp: Boolean,
  val newCareerLevel: Int,
  val maxEnergyIncreased: Boolean,
  val newMaxEnergy: Int,
  val matchEnergyCost: Int = 25
)

object CareerProgression {

  const val XP_PER_LEVEL = 100
  const val CAREER_LEVEL_XP_STEP = 250

  /**
   * Adds XP to a specific skill and handles level-up overflow.
   */
  fun addXpToSkill(
    currentVal: Int,
    currentXp: Int,
    gainedXp: Int,
    maxVal: Int = 99
  ): Pair<Int, Int> {
    if (currentVal >= maxVal) return Pair(maxVal, 0)

    val totalXp = currentXp + gainedXp
    val pointsToAdd = totalXp / XP_PER_LEVEL
    val remainingXp = totalXp % XP_PER_LEVEL

    val newVal = (currentVal + pointsToAdd).coerceAtMost(maxVal)
    val finalXp = if (newVal >= maxVal) 0 else remainingXp
    return Pair(newVal, finalXp)
  }

  /**
   * Evolves player attributes after completing a match.
   * Maps actual performance (goals, assists, chances, match rating) to skill XP:
   * - Técnica: boosted by successful chances, passes, dribbles and high match rating.
   * - Força: boosted by goals, shot power, and physical duels.
   * - Energia/Stamina: boosted by 90 minutes of active endurance.
   * Higher stamina skill reduces post-match energy depletion.
   */
  fun calculateMatchEvolution(
    player: Player,
    goals: Int,
    assists: Int,
    rating: Float,
    successChances: Int,
    totalChances: Int
  ): Pair<Player, SkillEvolutionReport> {
    val xpMap = mutableMapOf<String, Int>()
    val levelUps = mutableListOf<Pair<String, Int>>()

    // 1. Calculate XP gains based on performance metrics
    val goalPowerXp = goals * 40
    val goalAccuracyXp = goals * 45
    val assistVisionXp = assists * 45
    val assistTechniqueXp = assists * 35
    val chanceTechniqueXp = successChances * 30
    val matchStaminaXp = 30 + (rating * 2.5f).toInt()
    val matchPaceXp = 20 + (rating * 1.5f).toInt()
    val ratingBonusXp = if (rating >= 7.5f) 25 else 10

    xpMap[SkillType.TECHNIQUE.id] = assistTechniqueXp + chanceTechniqueXp + ratingBonusXp
    xpMap[SkillType.POWER.id] = goalPowerXp + (if (goals > 0) 15 else 5)
    xpMap[SkillType.STAMINA.id] = matchStaminaXp
    xpMap[SkillType.ACCURACY.id] = goalAccuracyXp + ratingBonusXp
    xpMap[SkillType.VISION.id] = assistVisionXp + (if (rating >= 7.0f) 20 else 5)
    xpMap[SkillType.PACE.id] = matchPaceXp
    xpMap[SkillType.CURL.id] = if (goals > 0 || successChances > 1) 25 else 8
    xpMap[SkillType.FREE_KICK.id] = if (goals > 0) 20 else 5

    val curSkills = player.skills

    // 2. Apply evolution to each skill
    val (newTech, newTechXp) = addXpToSkill(curSkills.technique, curSkills.techniqueXp, xpMap[SkillType.TECHNIQUE.id] ?: 0)
    if (newTech > curSkills.technique) levelUps.add(SkillType.TECHNIQUE.label to newTech)

    val (newPower, newPowerXp) = addXpToSkill(curSkills.power, curSkills.powerXp, xpMap[SkillType.POWER.id] ?: 0)
    if (newPower > curSkills.power) levelUps.add(SkillType.POWER.label to newPower)

    val (newStamina, newStaminaXp) = addXpToSkill(curSkills.stamina, curSkills.staminaXp, xpMap[SkillType.STAMINA.id] ?: 0)
    if (newStamina > curSkills.stamina) levelUps.add(SkillType.STAMINA.label to newStamina)

    val (newAcc, newAccXp) = addXpToSkill(curSkills.accuracy, curSkills.accuracyXp, xpMap[SkillType.ACCURACY.id] ?: 0)
    if (newAcc > curSkills.accuracy) levelUps.add(SkillType.ACCURACY.label to newAcc)

    val (newVision, newVisionXp) = addXpToSkill(curSkills.vision, curSkills.visionXp, xpMap[SkillType.VISION.id] ?: 0)
    if (newVision > curSkills.vision) levelUps.add(SkillType.VISION.label to newVision)

    val (newPace, newPaceXp) = addXpToSkill(curSkills.pace, curSkills.paceXp, xpMap[SkillType.PACE.id] ?: 0)
    if (newPace > curSkills.pace) levelUps.add(SkillType.PACE.label to newPace)

    val (newCurl, newCurlXp) = addXpToSkill(curSkills.curl, curSkills.curlXp, xpMap[SkillType.CURL.id] ?: 0)
    if (newCurl > curSkills.curl) levelUps.add(SkillType.CURL.label to newCurl)

    val (newFk, newFkXp) = addXpToSkill(curSkills.freeKick, curSkills.freeKickXp, xpMap[SkillType.FREE_KICK.id] ?: 0)
    if (newFk > curSkills.freeKick) levelUps.add(SkillType.FREE_KICK.label to newFk)

    val updatedSkills = curSkills.copy(
      technique = newTech, techniqueXp = newTechXp,
      power = newPower, powerXp = newPowerXp,
      stamina = newStamina, staminaXp = newStaminaXp,
      accuracy = newAcc, accuracyXp = newAccXp,
      vision = newVision, visionXp = newVisionXp,
      pace = newPace, paceXp = newPaceXp,
      curl = newCurl, curlXp = newCurlXp,
      freeKick = newFk, freeKickXp = newFkXp
    )

    // 3. Career Level Calculation
    val careerXpGained = (rating * 20f).toInt() + (goals * 45) + (assists * 30)
    val totalCareerXp = player.careerXp + careerXpGained
    val nextCareerLevel = (totalCareerXp / CAREER_LEVEL_XP_STEP) + 1
    val didLevelUp = nextCareerLevel > player.careerLevel

    // Max energy capacity expansion on stamina level-up
    var updatedMaxEnergy = player.maxEnergy
    var maxEnergyIncreased = false
    if (newStamina > curSkills.stamina && (newStamina % 2 == 0) && updatedMaxEnergy < 130) {
      updatedMaxEnergy += 1
      maxEnergyIncreased = true
    }

    // Dynamic stamina fatigue reduction: higher stamina reduces match energy drain
    val fatigueDiscount = (newStamina * 0.18f).toInt()
    val matchEnergyCost = ((28 + (rating * 1.0f).toInt()) - fatigueDiscount).coerceIn(15, 38)

    val evolvedPlayer = player.copy(
      skills = updatedSkills,
      careerLevel = nextCareerLevel,
      careerXp = totalCareerXp,
      maxEnergy = updatedMaxEnergy
    )

    val report = SkillEvolutionReport(
      xpGainedMap = xpMap,
      leveledUpSkills = levelUps,
      careerXpGained = careerXpGained,
      careerLevelUp = didLevelUp,
      newCareerLevel = nextCareerLevel,
      maxEnergyIncreased = maxEnergyIncreased,
      newMaxEnergy = updatedMaxEnergy,
      matchEnergyCost = matchEnergyCost
    )

    return Pair(evolvedPlayer, report)
  }

  /**
   * Applies training drill with specific skill type and intensity.
   */
  fun applyTraining(
    player: Player,
    skillType: SkillType,
    intensity: TrainingIntensity = TrainingIntensity.BALANCED
  ): TrainingResult {
    val cur = player.skills
    val currentAttr = cur.getAttribute(skillType)

    val xpToAdd = intensity.baseXp
    val energyCost = intensity.energyCost

    val (valAfter, xpAfter) = addXpToSkill(currentAttr.value, currentAttr.xp, xpToAdd)
    val didLevelUp = valAfter > currentAttr.value

    var updatedMaxEnergy = player.maxEnergy
    var maxEnergyExpanded = false

    if (skillType == SkillType.STAMINA && didLevelUp && updatedMaxEnergy < 130) {
      updatedMaxEnergy += 1
      maxEnergyExpanded = true
    }

    val updatedSkills = cur.withSkill(skillType, valAfter, xpAfter)
    val remainingEnergy = (player.energy - energyCost).coerceAtLeast(0)

    val updatedPlayer = player.copy(
      skills = updatedSkills,
      energy = remainingEnergy,
      maxEnergy = updatedMaxEnergy
    )

    val message = when {
      didLevelUp && maxEnergyExpanded ->
        "${skillType.label} subiu para $valAfter! Capacidade física expandida: $updatedMaxEnergy HP máx.!"
      didLevelUp ->
        "${skillType.label} subiu para $valAfter! (+1 ponto de habilidade)"
      else ->
        "${skillType.label}: +$xpToAdd XP ($xpAfter/100 XP)"
    }

    return TrainingResult(
      player = updatedPlayer,
      skillType = skillType,
      xpGained = xpToAdd,
      didLevelUp = didLevelUp,
      newLevel = valAfter,
      energySpent = energyCost,
      maxEnergyExpanded = maxEnergyExpanded,
      newMaxEnergy = updatedMaxEnergy,
      message = message
    )
  }

  /**
   * Legacy string-key training drill overload for seamless backward compatibility.
   */
  fun applyTrainingDrill(
    player: Player,
    skillKey: String,
    intensity: TrainingIntensity = TrainingIntensity.BALANCED
  ): Pair<Player, String> {
    val skillType = SkillType.fromId(skillKey)
    val result = applyTraining(player, skillType, intensity)
    return Pair(result.player, result.message)
  }

  /**
   * Rest Day / Day Off mechanic (New Star Soccer style):
   * Recovers player energy (+25 HP) without spending money, at the cost of forgoing a training session.
   */
  fun applyRestDay(player: Player): Pair<Player, String> {
    val energyRecovered = 25
    val newEnergy = (player.energy + energyRecovered).coerceAtMost(player.maxEnergy)
    val actualGained = newEnergy - player.energy

    val updatedPlayer = player.copy(energy = newEnergy)
    val msg = if (actualGained > 0) {
      "Dia de descanso bem aproveitado! Recuperou +$actualGained HP de energia ($newEnergy/${player.maxEnergy})."
    } else {
      "Você já está com a energia no máximo (${player.maxEnergy} HP)!"
    }
    return Pair(updatedPlayer, msg)
  }
}
