package com.example.model

enum class PlayerPosition(val label: String, val shortLabel: String) {
  ATACANTE("Centroavante", "ATA"),
  MEIA("Meia Armador", "MEI"),
  PONTA("Ponta Veloz", "PON")
}

enum class SkillCategory(val label: String) {
  TECHNICAL("Técnico"),
  PHYSICAL("Físico"),
  TACTICAL("Tático")
}

enum class SkillType(
  val id: String,
  val label: String,
  val shortLabel: String,
  val description: String,
  val category: SkillCategory
) {
  TECHNIQUE(
    id = "technique",
    label = "Técnica & Drible",
    shortLabel = "TÉC",
    description = "Controle de bola de primeira, domínio sob pressão e drible curto sobre a marcação.",
    category = SkillCategory.TECHNICAL
  ),
  POWER(
    id = "power",
    label = "Força & Potência",
    shortLabel = "FOR",
    description = "Potência do chute, finalizações de longa distância e divididas corporais.",
    category = SkillCategory.PHYSICAL
  ),
  STAMINA(
    id = "stamina",
    label = "Energia & Resistência",
    shortLabel = "ENE",
    description = "Fôlego aeróbico, menor cansaço por partida e expansão da barra máxima de energia.",
    category = SkillCategory.PHYSICAL
  ),
  ACCURACY(
    id = "accuracy",
    label = "Precisão & Mira",
    shortLabel = "PRE",
    description = "Pontaria em chutes nas gavetas e bochechas da rede, reduzindo chances de chute para fora.",
    category = SkillCategory.TECHNICAL
  ),
  VISION(
    id = "vision",
    label = "Visão de Jogo",
    shortLabel = "VIS",
    description = "Percepção espacial, passes enfiados em profundidade e triangulações rápidas.",
    category = SkillCategory.TACTICAL
  ),
  PACE(
    id = "pace",
    label = "Velocidade & Arranque",
    shortLabel = "VEL",
    description = "Aceleração explosiva nos primeiros passos e velocidade máxima nos contragolpes.",
    category = SkillCategory.PHYSICAL
  ),
  CURL(
    id = "curl",
    label = "Curva & Efeito",
    shortLabel = "CUR",
    description = "Efeito aerodinâmico Magnus, chutes de chapa colocados e finalizações de trivela.",
    category = SkillCategory.TECHNICAL
  ),
  FREE_KICK(
    id = "freeKick",
    label = "Bolas Paradas",
    shortLabel = "FAL",
    description = "Precisão em cobranças de falta por cima da barreira e penalidades máximas.",
    category = SkillCategory.TECHNICAL
  );

  companion object {
    fun fromId(id: String): SkillType = entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: TECHNIQUE
  }
}

data class SkillAttribute(
  val type: SkillType,
  val value: Int,
  val xp: Int,
  val maxXp: Int = 100
) {
  val progressFraction: Float get() = (xp.toFloat() / maxXp).coerceIn(0f, 1f)
  val isMaxed: Boolean get() = value >= 99
}

data class PlayerSkills(
  // Core Technical, Physical & Energy Attributes (10 - 99)
  val technique: Int = 35,     // Técnica & Drible
  val power: Int = 32,         // Força de Chute & Físico
  val stamina: Int = 40,       // Fôlego & Resistência (Energia)
  val curl: Int = 28,          // Curva & Efeito
  val accuracy: Int = 36,      // Precisão de Finalização
  val vision: Int = 32,        // Visão de Jogo & Passe
  val pace: Int = 42,          // Velocidade & Arranque
  val freeKick: Int = 22,      // Bolas Paradas

  // Sub-level XP Progress towards the next point (0 - 99 XP)
  val techniqueXp: Int = 0,
  val powerXp: Int = 0,
  val staminaXp: Int = 0,
  val curlXp: Int = 0,
  val accuracyXp: Int = 0,
  val visionXp: Int = 0,
  val paceXp: Int = 0,
  val freeKickXp: Int = 0
) {
  val overall: Int
    get() = (
      (technique * 1.3 + power * 1.2 + stamina * 1.0 + accuracy * 1.4 +
       vision * 1.1 + pace * 1.1 + curl * 0.9 + freeKick * 0.8) / 8.8
    ).toInt()

  val starRating: Float
    get() = (overall.coerceIn(40, 99) - 30) / 14f

  fun getAttribute(type: SkillType): SkillAttribute = when (type) {
    SkillType.TECHNIQUE -> SkillAttribute(type, technique, techniqueXp)
    SkillType.POWER -> SkillAttribute(type, power, powerXp)
    SkillType.STAMINA -> SkillAttribute(type, stamina, staminaXp)
    SkillType.ACCURACY -> SkillAttribute(type, accuracy, accuracyXp)
    SkillType.VISION -> SkillAttribute(type, vision, visionXp)
    SkillType.PACE -> SkillAttribute(type, pace, paceXp)
    SkillType.CURL -> SkillAttribute(type, curl, curlXp)
    SkillType.FREE_KICK -> SkillAttribute(type, freeKick, freeKickXp)
  }

  fun getAllAttributes(): List<SkillAttribute> = SkillType.entries.map { getAttribute(it) }

  fun withSkill(type: SkillType, newVal: Int, newXp: Int): PlayerSkills = when (type) {
    SkillType.TECHNIQUE -> copy(technique = newVal, techniqueXp = newXp)
    SkillType.POWER -> copy(power = newVal, powerXp = newXp)
    SkillType.STAMINA -> copy(stamina = newVal, staminaXp = newXp)
    SkillType.ACCURACY -> copy(accuracy = newVal, accuracyXp = newXp)
    SkillType.VISION -> copy(vision = newVal, visionXp = newXp)
    SkillType.PACE -> copy(pace = newVal, paceXp = newXp)
    SkillType.CURL -> copy(curl = newVal, curlXp = newXp)
    SkillType.FREE_KICK -> copy(freeKick = newVal, freeKickXp = newXp)
  }
}

data class PlayerRelationships(
  val coach: Int = 60,      // Treinador (0 - 100)
  val teammates: Int = 55,  // Companheiros (0 - 100)
  val fans: Int = 65,       // Torcida (0 - 100)
  val sponsor: Int = 40,    // Patrocinador (0 - 100)
  val partner: Int = 50     // Família / Namorada (0 - 100)
)

data class Player(
  val name: String = "Craque",
  val nickname: String = "Fenômeno",
  val age: Int = 17,
  val nationality: String = "Brasil",
  val preferredFoot: String = "Destro",
  val position: PlayerPosition = PlayerPosition.ATACANTE,
  val number: Int = 10,

  // Energy & Stamina System
  val energy: Int = 100,           // Energia atual (0 to maxEnergy)
  val maxEnergy: Int = 100,        // Capacidade máxima de energia (expansível via treinos aeróbicos)

  // Career Progression & Level
  val careerLevel: Int = 1,
  val careerXp: Int = 0,

  // Financial & Contract
  val money: Long = 1200,          // R$
  val wagePerMatch: Long = 350,    // R$
  val winBonus: Long = 200,        // R$
  val goalBonus: Long = 100,       // R$
  val contractYears: Int = 2,
  val currentClubId: String = "vila_real",

  // Skills & Social & Lifestyle
  val skills: PlayerSkills = PlayerSkills(),
  val relationships: PlayerRelationships = PlayerRelationships(),
  val happiness: Int = 85,         // Felicidade / Moral geral do atleta (0 a 100%)
  val isTrialCompleted: Boolean = true, // Se já passou pela peneira de olheiro
  val scoutScore: Int = 75,        // Nota na avaliação do olheiro (0 a 100)
  val lifestylePrestige: Int = 1,
  val equippedBootsId: String = "boots_basic",

  // Career Totals
  val matchesPlayed: Int = 0,
  val goalsScored: Int = 0,
  val assistsGiven: Int = 0,
  val mvpAwards: Int = 0,
  val trophiesWon: Int = 0,

  // Current Season Totals
  val seasonMatches: Int = 0,
  val seasonGoals: Int = 0,
  val seasonAssists: Int = 0
) {
  val isExhausted: Boolean
    get() = energy < (maxEnergy * 0.25f)

  val energyPercentage: Float
    get() = if (maxEnergy > 0) (energy.toFloat() / maxEnergy).coerceIn(0f, 1f) else 1f

  val careerTitle: String
    get() = when {
      careerLevel >= 15 -> "Lenda Mundial do Futebol"
      careerLevel >= 12 -> "Super Estrela Internacional"
      careerLevel >= 9  -> "Craque da Primeira Divisão"
      careerLevel >= 6  -> "Ídolo da Torcida"
      careerLevel >= 4  -> "Titular Absoluto"
      careerLevel >= 2  -> "Joia Promissora do Elenco"
      else              -> "Promessa das Categorias de Base"
    }

  val coachConfidenceText: String
    get() = when {
      relationships.coach >= 80 -> "Titular Indiscutível"
      relationships.coach >= 50 -> "Titular Confiável"
      relationships.coach >= 30 -> "Disputando Vaga"
      else -> "Em risco de perder a vaga"
    }

  val techniqueAttribute: SkillAttribute get() = skills.getAttribute(SkillType.TECHNIQUE)
  val powerAttribute: SkillAttribute get() = skills.getAttribute(SkillType.POWER)
  val staminaAttribute: SkillAttribute get() = skills.getAttribute(SkillType.STAMINA)
  val accuracyAttribute: SkillAttribute get() = skills.getAttribute(SkillType.ACCURACY)
  val visionAttribute: SkillAttribute get() = skills.getAttribute(SkillType.VISION)
  val paceAttribute: SkillAttribute get() = skills.getAttribute(SkillType.PACE)
  val curlAttribute: SkillAttribute get() = skills.getAttribute(SkillType.CURL)
  val freeKickAttribute: SkillAttribute get() = skills.getAttribute(SkillType.FREE_KICK)

  // Stamina / Energy benefit: higher stamina reduces match energy depletion
  val staminaFatigueReduction: Float
    get() = (skills.stamina / 100f * 0.45f).coerceIn(0.05f, 0.45f)

  val careerXpForNextLevel: Int
    get() = careerLevel * 250

  val careerLevelProgress: Float
    get() = ((careerXp % 250).toFloat() / 250f).coerceIn(0f, 1f)

  val happinessDescription: String
    get() = when {
      happiness >= 85 -> "Radiante / Alta Confiança"
      happiness >= 65 -> "Satisfeito & Focado"
      happiness >= 45 -> "Sob Pressão"
      happiness >= 25 -> "Desanimado"
      else -> "Em Crise / Esgotado"
    }

  val happinessFraction: Float
    get() = (happiness / 100f).coerceIn(0f, 1f)

  val hasClub: Boolean
    get() = currentClubId.isNotBlank() && isTrialCompleted
}
