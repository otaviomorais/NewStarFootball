package com.example.model

enum class ShopCategory(val title: String) {
  BOOTS("Chuteiras"),
  ENERGY("Energia & Saúde"),
  LIFESTYLE("Estilo de Vida"),
  STAFF("Staff & Carreira")
}

data class ShopItem(
  val id: String,
  val name: String,
  val category: ShopCategory,
  val price: Long,
  val description: String,
  val iconName: String,
  val powerBonus: Int = 0,
  val curlBonus: Int = 0,
  val accuracyBonus: Int = 0,
  val visionBonus: Int = 0,
  val paceBonus: Int = 0,
  val energyRestored: Int = 0,
  val coachBonus: Int = 0,
  val teammatesBonus: Int = 0,
  val fansBonus: Int = 0,
  val sponsorBonus: Int = 0,
  val partnerBonus: Int = 0,
  val isOwned: Boolean = false,
  val isConsumable: Boolean = false
)

object DefaultShopItems {
  fun getAllItems(): List<ShopItem> = listOf(
    // Energy / Drinks
    ShopItem(
      id = "energy_isocool",
      name = "Isotônico Refrescante",
      category = ShopCategory.ENERGY,
      price = 100,
      description = "Recupera 30% da sua energia instantaneamente para a próxima partida.",
      iconName = "LocalDrink",
      energyRestored = 30,
      isConsumable = true
    ),
    ShopItem(
      id = "energy_turbo_shot",
      name = "Bebida Energética Turbo",
      category = ShopCategory.ENERGY,
      price = 220,
      description = "Recupera 65% da sua barra de energia com taurina e eletrólitos.",
      iconName = "Bolt",
      energyRestored = 65,
      isConsumable = true
    ),
    ShopItem(
      id = "energy_spa_vip",
      name = "Sessão Fisioterápica VIP",
      category = ShopCategory.ENERGY,
      price = 450,
      description = "Banheira de gelo, massagem e 100% de energia e disposição restaurada!",
      iconName = "Spa",
      energyRestored = 100,
      isConsumable = true
    ),

    // Boots
    ShopItem(
      id = "boots_basic",
      name = "Chuteira Clássica Preta",
      category = ShopCategory.BOOTS,
      price = 0,
      description = "Chuteira inicial simples de couro sintético.",
      iconName = "SportsSoccer",
      isOwned = true
    ),
    ShopItem(
      id = "boots_speed",
      name = "Chuteira Flash Velocitá",
      category = ShopCategory.BOOTS,
      price = 750,
      description = "Travas aerodinâmicas para arrancadas explosivas (+6 Velocidade, +4 Força).",
      iconName = "Speed",
      paceBonus = 6,
      powerBonus = 4
    ),
    ShopItem(
      id = "boots_curve",
      name = "Chuteira Predator Curve",
      category = ShopCategory.BOOTS,
      price = 1400,
      description = "Borracha texturizada para colocar efeito mortal na bola (+8 Curva, +6 Precisão).",
      iconName = "TrendingUp",
      curlBonus = 8,
      accuracyBonus = 6
    ),
    ShopItem(
      id = "boots_sniper",
      name = "Chuteira Laser Sniper",
      category = ShopCategory.BOOTS,
      price = 2500,
      description = "Mira infalível no ângulo do goleiro (+10 Precisão, +8 Força, +6 Visão).",
      iconName = "Adjust",
      accuracyBonus = 10,
      powerBonus = 8,
      visionBonus = 6
    ),
    ShopItem(
      id = "boots_gold",
      name = "Chuteira de Ouro 24k",
      category = ShopCategory.BOOTS,
      price = 6000,
      description = "A chuteira dos maiores artilheiros do mundo (+12 em Todas Habilidades)! Reluz no gramado.",
      iconName = "Star",
      powerBonus = 12,
      curlBonus = 12,
      accuracyBonus = 12,
      visionBonus = 12,
      paceBonus = 12
    ),

    // Lifestyle
    ShopItem(
      id = "life_phone",
      name = "Smartphone Gamer Pro",
      category = ShopCategory.LIFESTYLE,
      price = 900,
      description = "Poste fotos dos treinos e ganhe seguidores (+8 Torcida, +8 Namorada).",
      iconName = "PhoneAndroid",
      fansBonus = 8,
      partnerBonus = 8
    ),
    ShopItem(
      id = "life_watch",
      name = "Relógio Suíço Ouro",
      category = ShopCategory.LIFESTYLE,
      price = 2800,
      description = "Elegância pura que impressiona patrocinadores (+15 Patrocinador, +10 Namorada).",
      iconName = "Watch",
      sponsorBonus = 15,
      partnerBonus = 10
    ),
    ShopItem(
      id = "life_car_hot",
      name = "Carro Esportivo Turbo",
      category = ShopCategory.LIFESTYLE,
      price = 15000,
      description = "Chegue aos treinos com estilo e motor V6 (+18 Namorada, +15 Torcida).",
      iconName = "DirectionsCar",
      partnerBonus = 18,
      fansBonus = 15
    ),
    ShopItem(
      id = "life_mansion",
      name = "Cobertura Frente ao Mar",
      category = ShopCategory.LIFESTYLE,
      price = 65000,
      description = "Vista panorâmica, piscina infinita e prestígio máximo (+25 Todos Relacionamentos).",
      iconName = "House",
      coachBonus = 10,
      teammatesBonus = 20,
      fansBonus = 25,
      sponsorBonus = 25,
      partnerBonus = 25
    ),

    // Staff
    ShopItem(
      id = "staff_trainer",
      name = "Preparador Físico Pessoal",
      category = ShopCategory.STAFF,
      price = 3500,
      description = "Treinos 2x mais eficientes e melhora constante da capacidade física (+5 Força, +5 Pace).",
      iconName = "FitnessCenter",
      powerBonus = 5,
      paceBonus = 5,
      coachBonus = 10
    ),
    ShopItem(
      id = "staff_agent",
      name = "Super Agente Internacional",
      category = ShopCategory.STAFF,
      price = 8000,
      description = "Negocia salários mais altos, bônus milionários e contratos estelares (+25 Patrocinador).",
      iconName = "BusinessCenter",
      sponsorBonus = 25
    )
  )
}
