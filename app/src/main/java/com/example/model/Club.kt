package com.example.model

data class Club(
  val id: String,
  val name: String,
  val shortName: String,
  val primaryColorHex: Long,
  val secondaryColorHex: Long,
  val reputation: Int, // 1 to 5 stars
  val stadiumName: String,
  val tier: Int = 1,
  var points: Int = 0,
  var played: Int = 0,
  var won: Int = 0,
  var drawn: Int = 0,
  var lost: Int = 0,
  var goalsFor: Int = 0,
  var goalsAgainst: Int = 0
) {
  val goalDifference: Int
    get() = goalsFor - goalsAgainst
}

object DefaultClubs {
  fun getInitialClubs(): List<Club> = listOf(
    Club(
      id = "vila_real",
      name = "Vila Real FC",
      shortName = "VIL",
      primaryColorHex = 0xFF1976D2, // Blue
      secondaryColorHex = 0xFFFFFFFF,
      reputation = 2,
      stadiumName = "Arena da Vila",
      tier = 2,
      points = 12, played = 6, won = 3, drawn = 3, lost = 0, goalsFor = 8, goalsAgainst = 3
    ),
    Club(
      id = "estrela_dourada",
      name = "Estrela Dourada",
      shortName = "EST",
      primaryColorHex = 0xFFFFB300, // Gold
      secondaryColorHex = 0xFF1B5E20,
      reputation = 3,
      stadiumName = "Estádio das Estrelas",
      tier = 1,
      points = 14, played = 6, won = 4, drawn = 2, lost = 0, goalsFor = 11, goalsAgainst = 4
    ),
    Club(
      id = "rubro_sul",
      name = "Rubro-Sul AC",
      shortName = "RBS",
      primaryColorHex = 0xFFC62828, // Red
      secondaryColorHex = 0xFF212121,
      reputation = 3,
      stadiumName = "Caldeirão Vermelho",
      tier = 1,
      points = 13, played = 6, won = 4, drawn = 1, lost = 1, goalsFor = 10, goalsAgainst = 5
    ),
    Club(
      id = "alviverde_fc",
      name = "Alviverde FC",
      shortName = "ALV",
      primaryColorHex = 0xFF2E7D32, // Green
      secondaryColorHex = 0xFFFFFFFF,
      reputation = 4,
      stadiumName = "Arena Parque",
      tier = 1,
      points = 11, played = 6, won = 3, drawn = 2, lost = 1, goalsFor = 9, goalsAgainst = 6
    ),
    Club(
      id = "atletico_litoral",
      name = "Atlético Litoral",
      shortName = "LIT",
      primaryColorHex = 0xFF00838F, // Teal
      secondaryColorHex = 0xFFFFFFFF,
      reputation = 2,
      stadiumName = "Estádio da Praia",
      tier = 2,
      points = 9, played = 6, won = 2, drawn = 3, lost = 1, goalsFor = 7, goalsAgainst = 5
    ),
    Club(
      id = "gremio_central",
      name = "Grêmio Central",
      shortName = "GRE",
      primaryColorHex = 0xFF0288D1, // Sky Blue
      secondaryColorHex = 0xFF212121,
      reputation = 3,
      stadiumName = "Coliseu Central",
      tier = 2,
      points = 8, played = 6, won = 2, drawn = 2, lost = 2, goalsFor = 6, goalsAgainst = 6
    ),
    Club(
      id = "santos_paulista",
      name = "Paulista Santos",
      shortName = "PAU",
      primaryColorHex = 0xFF212121, // Black/White
      secondaryColorHex = 0xFFFFFFFF,
      reputation = 4,
      stadiumName = "Vila Belmiro",
      tier = 1,
      points = 8, played = 6, won = 2, drawn = 2, lost = 2, goalsFor = 8, goalsAgainst = 8
    ),
    Club(
      id = "aurinegro_sp",
      name = "Aurinegro Esporte",
      shortName = "AUR",
      primaryColorHex = 0xFFFFD600, // Yellow
      secondaryColorHex = 0xFF212121,
      reputation = 2,
      stadiumName = "Estádio da Serra",
      tier = 3,
      points = 6, played = 6, won = 1, drawn = 3, lost = 2, goalsFor = 5, goalsAgainst = 7
    ),
    Club(
      id = "rio_claro",
      name = "Rio Claro FC",
      shortName = "RCL",
      primaryColorHex = 0xFF6A1B9A, // Purple
      secondaryColorHex = 0xFFFFFFFF,
      reputation = 2,
      stadiumName = "Municipal de Rio Claro",
      tier = 3,
      points = 4, played = 6, won = 1, drawn = 1, lost = 4, goalsFor = 4, goalsAgainst = 11
    ),
    Club(
      id = "uniao_nacional",
      name = "União Nacional",
      shortName = "UNI",
      primaryColorHex = 0xFF4E342E, // Brown
      secondaryColorHex = 0xFFFFB300,
      reputation = 1,
      stadiumName = "Estádio da Várzea",
      tier = 3,
      points = 2, played = 6, won = 0, drawn = 2, lost = 4, goalsFor = 3, goalsAgainst = 14
    )
  )
}
