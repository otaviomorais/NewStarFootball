package com.example.model

enum class ScoutDrillType(
  val id: String,
  val title: String,
  val subtitle: String,
  val instruction: String,
  val energyCost: Int
) {
  SHOOTING(
    id = "shooting",
    title = "Teste 1: Finalização & Pontaria",
    subtitle = "Avaliação de Chute",
    instruction = "Acerte os alvos nas gavetas do gol com força e precisão.",
    energyCost = 10
  ),
  PASSING(
    id = "passing",
    title = "Teste 2: Visão de Jogo & Passe",
    subtitle = "Avaliação de Distribuição",
    instruction = "Encontre os companheiros em movimento através da linha de marcação.",
    energyCost = 8
  ),
  AGILITY(
    id = "agility",
    title = "Teste 3: Circuito Físico & Drible",
    subtitle = "Avaliação de Fôlego & Coordenação",
    instruction = "Circuito de arrancada, drible curto nos cones e velocidade de reação.",
    energyCost = 12
  )
}

data class ClubContractOffer(
  val club: Club,
  val wagePerMatch: Long,
  val winBonus: Long,
  val goalBonus: Long,
  val contractYears: Int,
  val squadRole: String,
  val scoutNote: String
)

object ScoutTrialEngine {

  /**
   * Evaluates trial drill performance based on player skills, accuracy inputs, and timing.
   */
  fun calculateDrillScore(
    drillType: ScoutDrillType,
    player: Player,
    performanceQuality: Float // 0.0f to 1.0f
  ): Int {
    val baseSkill = when (drillType) {
      ScoutDrillType.SHOOTING -> (player.skills.power * 0.5f + player.skills.accuracy * 0.5f)
      ScoutDrillType.PASSING -> (player.skills.vision * 0.6f + player.skills.technique * 0.4f)
      ScoutDrillType.AGILITY -> (player.skills.pace * 0.5f + player.skills.stamina * 0.5f)
    }

    val score = (baseSkill * 0.4f + performanceQuality * 60f).toInt().coerceIn(20, 100)
    return score
  }

  /**
   * Generates realistic contract proposals from Brazilian clubs based on the scout trial score.
   */
  fun generateTrialOffers(
    player: Player,
    availableClubs: List<Club>,
    totalScore: Int
  ): List<ClubContractOffer> {
    val offers = mutableListOf<ClubContractOffer>()
    val shuffledClubs = availableClubs.shuffled()

    when {
      totalScore >= 75 -> {
        // High score: offers from tier 1 elite clubs and strong tier 2 clubs with top wages
        val tier1Clubs = shuffledClubs.filter { it.tier == 1 }
        val tier2Clubs = shuffledClubs.filter { it.tier == 2 }

        val mainClub = tier1Clubs.firstOrNull() ?: shuffledClubs.first()
        val secondClub = tier1Clubs.getOrNull(1) ?: tier2Clubs.firstOrNull() ?: shuffledClubs.getOrElse(1) { mainClub }
        val thirdClub = tier2Clubs.firstOrNull { it.id != mainClub.id && it.id != secondClub.id } ?: shuffledClubs.last()

        offers.add(
          ClubContractOffer(
            club = mainClub,
            wagePerMatch = 980L,
            winBonus = 450L,
            goalBonus = 280L,
            contractYears = 3,
            squadRole = "Joia do Elenco Profissional",
            scoutNote = "Os olheiros do ${mainClub.shortName} ficaram maravilhados com sua frieza e precisão nos testes. Proposta formal para integrar o elenco principal imediatamente!"
          )
        )

        if (secondClub.id != mainClub.id) {
          offers.add(
            ClubContractOffer(
              club = secondClub,
              wagePerMatch = 750L,
              winBonus = 350L,
              goalBonus = 200L,
              contractYears = 3,
              squadRole = "Titular em Potencial",
              scoutNote = "Grande destaque técnico! Promessa de camisa titular e plano especial de desenvolvimento físico."
            )
          )
        }

        if (thirdClub.id != mainClub.id && thirdClub.id != secondClub.id) {
          offers.add(
            ClubContractOffer(
              club = thirdClub,
              wagePerMatch = 550L,
              winBonus = 260L,
              goalBonus = 150L,
              contractYears = 2,
              squadRole = "Camisa 10 do Projeto",
              scoutNote = "O ${thirdClub.name} quer construir a equipe ao redor do seu talento com bônus generosos por vitória."
            )
          )
        }
      }
      totalScore >= 50 -> {
        // Moderate score: solid standard clubs (tier 2)
        val tier2Clubs = shuffledClubs.filter { it.tier == 2 }
        val candidates = if (tier2Clubs.size >= 2) tier2Clubs.take(3) else shuffledClubs.take(3)

        for ((idx, club) in candidates.withIndex()) {
          val role = if (idx == 0) "Disputando Vaga de Titular" else "Reserva Imediato"
          offers.add(
            ClubContractOffer(
              club = club,
              wagePerMatch = (380L + idx * 70L),
              winBonus = 180L,
              goalBonus = 100L,
              contractYears = 2,
              squadRole = role,
              scoutNote = "Desempenho aprovado nos fundamentos essenciais. O ${club.shortName} oferece contrato de 2 anos com perspectiva de titularidade."
            )
          )
        }
      }
      else -> {
        // Modest score: hard route starting in fighting tier 3 clubs
        val tier3Clubs = shuffledClubs.filter { it.tier == 3 }
        val candidates = if (tier3Clubs.isNotEmpty()) tier3Clubs.take(2) else shuffledClubs.takeLast(2)

        for ((idx, club) in candidates.withIndex()) {
          val wage = if (idx == 0) 220L else 190L
          val role = if (idx == 0) "Aposta da Base (Modo Difícil)" else "Contrato de Experiência"
          offers.add(
            ClubContractOffer(
              club = club,
              wagePerMatch = wage,
              winBonus = 90L,
              goalBonus = 50L,
              contractYears = 1,
              squadRole = role,
              scoutNote = "Começo pelo jeito difícil! O olheiro do ${club.name} viu garra e força de vontade, oferecendo contrato de 1 ano para provar seu valor na raça."
            )
          )
        }
      }
    }

    return offers
  }
}
