package com.example.model

enum class LifestyleActivity(
  val id: String,
  val title: String,
  val description: String,
  val energyCost: Int,
  val moneyCost: Long,
  val happinessBonus: Int,
  val coachImpact: Int,
  val teammatesImpact: Int,
  val fansImpact: Int,
  val sponsorImpact: Int,
  val partnerImpact: Int,
  val iconName: String
) {
  REST_DAY(
    id = "rest_day",
    title = "Dia de Folga & Recuperação",
    description = "Fique em casa, durma bem e descanse a musculatura. Restaura energia e alivia estresse.",
    energyCost = 0,
    moneyCost = 0,
    happinessBonus = 12,
    coachImpact = 2,
    teammatesImpact = 0,
    fansImpact = 0,
    sponsorImpact = 0,
    partnerImpact = 6,
    iconName = "Bedtime"
  ),
  FAMILY_DINNER(
    id = "family_dinner",
    title = "Jantar Romântico / Família",
    description = "Momento íntimo para valorizar quem esteve ao seu lado desde a base.",
    energyCost = 8,
    moneyCost = 180,
    happinessBonus = 18,
    coachImpact = 0,
    teammatesImpact = 0,
    fansImpact = 0,
    sponsorImpact = 0,
    partnerImpact = 16,
    iconName = "Favorite"
  ),
  SQUAD_BARBECUE(
    id = "squad_barbecue",
    title = "Churrasco com o Elenco",
    description = "Resenha descontraída com companheiros de vestiário para elevar o entrosamento.",
    energyCost = 12,
    moneyCost = 350,
    happinessBonus = 14,
    coachImpact = -2,
    teammatesImpact = 18,
    fansImpact = 4,
    sponsorImpact = 0,
    partnerImpact = -3,
    iconName = "Groups"
  ),
  VIP_PARTY(
    id = "vip_party",
    title = "Camarote & Noitada VIP",
    description = "Vida de celebridade em baladas badaladas. Eleva moral e fama, mas irrita a comissão técnica.",
    energyCost = 28,
    moneyCost = 900,
    happinessBonus = 22,
    coachImpact = -15,
    teammatesImpact = 6,
    fansImpact = 12,
    sponsorImpact = -5,
    partnerImpact = -12,
    iconName = "Nightlife"
  ),
  CHARITY_VISIT(
    id = "charity_visit",
    title = "Ação Social na Comunidade",
    description = "Visite escolinhas de futebol carentes e doe cestas básicas. Admirado pela torcida e marcas.",
    energyCost = 10,
    moneyCost = 300,
    happinessBonus = 15,
    coachImpact = 5,
    teammatesImpact = 4,
    fansImpact = 22,
    sponsorImpact = 15,
    partnerImpact = 8,
    iconName = "VolunteerActivism"
  ),
  GAMING_RELAX(
    id = "gaming_relax",
    title = "Jogatina & Streaming",
    description = "Jogue videogame e relaxe a mente sem gastar fortunas.",
    energyCost = 5,
    moneyCost = 0,
    happinessBonus = 10,
    coachImpact = 0,
    teammatesImpact = 5,
    fansImpact = 6,
    sponsorImpact = 2,
    partnerImpact = -2,
    iconName = "SportsEsports"
  )
}

object LifestyleManager {

  /**
   * Applies lifestyle activity to balance relationships, money, energy, and happiness.
   */
  fun executeActivity(player: Player, activity: LifestyleActivity): Pair<Player, String> {
    if (player.energy < activity.energyCost) {
      return Pair(player, "Muito exausto! Você precisa de ${activity.energyCost} HP de energia.")
    }
    if (player.money < activity.moneyCost) {
      return Pair(player, "Saldo bancário insuficiente! Custo: R$ ${activity.moneyCost}.")
    }

    val updatedMoney = player.money - activity.moneyCost
    val updatedEnergy = if (activity == LifestyleActivity.REST_DAY) {
      (player.energy + 25).coerceAtMost(player.maxEnergy)
    } else {
      (player.energy - activity.energyCost).coerceAtLeast(0)
    }

    val updatedHappiness = (player.happiness + activity.happinessBonus).coerceIn(0, 100)

    val curRels = player.relationships
    val updatedRels = curRels.copy(
      coach = (curRels.coach + activity.coachImpact).coerceIn(0, 100),
      teammates = (curRels.teammates + activity.teammatesImpact).coerceIn(0, 100),
      fans = (curRels.fans + activity.fansImpact).coerceIn(0, 100),
      sponsor = (curRels.sponsor + activity.sponsorImpact).coerceIn(0, 100),
      partner = (curRels.partner + activity.partnerImpact).coerceIn(0, 100)
    )

    val updatedPlayer = player.copy(
      money = updatedMoney,
      energy = updatedEnergy,
      happiness = updatedHappiness,
      relationships = updatedRels
    )

    val feedbackMsg = when (activity) {
      LifestyleActivity.REST_DAY -> "Dia de descanso excelente! +25 HP e +${activity.happinessBonus}% de Felicidade."
      LifestyleActivity.FAMILY_DINNER -> "Noite especial com a parceira! Relacionamento subiu para ${updatedRels.partner}%."
      LifestyleActivity.SQUAD_BARBECUE -> "Ótima confraternização! O clima com o elenco está em alta (${updatedRels.teammates}%)."
      LifestyleActivity.VIP_PARTY -> "Noite agitada! Fãs adoraram, mas o treinador não aprovou o desgaste físico."
      LifestyleActivity.CHARITY_VISIT -> "Ação exemplar! A torcida e patrocinadores aplaudiram seu gesto comunitário."
      LifestyleActivity.GAMING_RELAX -> "Momento relaxante! Mental renovado para os próximos jogos."
    }

    return Pair(updatedPlayer, feedbackMsg)
  }
}
