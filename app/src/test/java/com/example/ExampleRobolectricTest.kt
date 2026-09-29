package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.gameplay.MatchEngine
import com.example.model.Club
import com.example.model.DefaultClubs
import com.example.model.Player
import com.example.model.PlayerPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Estrela do Futebol", appName)
  }

  @Test
  fun `verify player creation and overall calculation`() {
    val player = Player(
      name = "Pelézinho",
      position = PlayerPosition.ATACANTE
    )
    assertTrue(player.skills.overall > 0)
    assertEquals("Pelézinho", player.name)
    assertEquals(100, player.energy)
  }

  @Test
  fun `verify match engine chance generation`() {
    val player = Player()
    val opponent = DefaultClubs.getInitialClubs().last()
    val chances = MatchEngine.generateChances(player, opponent)
    assertTrue(chances.isNotEmpty())
    assertNotNull(chances.first().type)
  }

  @Test
  fun `verify physics engine magnus effect and trajectory`() {
    val physics = com.example.gameplay.physics.SoccerPhysicsEngine()
    physics.launchShot(
      startX = 0.5f,
      startY = 0.7f,
      targetX = 0.5f,
      targetY = 0.14f,
      impactOffset = androidx.compose.ui.geometry.Offset(-0.8f, 0.4f),
      powerInput = 0.9f,
      playerPowerSkill = 60,
      playerCurlSkill = 70,
      playerAccuracySkill = 65
    )
    assertTrue(physics.isBallInPlay)
    assertTrue(physics.shotSpeedKmH > 70f)
    assertTrue(physics.particles.isNotEmpty())

    // Update multiple frames to check movement and curve
    for (i in 0 until 10) {
      physics.update(0.016f)
    }
    assertTrue(physics.ballY < 0.7f)
    // Lateral curve caused by Magnus effect
    assertTrue(physics.ballX != 0.5f)
  }

  @Test
  fun `verify career skill attributes and overall`() {
    val player = Player(
      skills = com.example.model.PlayerSkills(
        technique = 50,
        power = 60,
        stamina = 55
      )
    )
    val techAttr = player.techniqueAttribute
    assertEquals(com.example.model.SkillType.TECHNIQUE, techAttr.type)
    assertEquals(50, techAttr.value)
    assertEquals(60, player.powerAttribute.value)
    assertEquals(55, player.staminaAttribute.value)
    assertTrue(player.staminaFatigueReduction > 0f)
  }

  @Test
  fun `verify training evolution for technique and power`() {
    val initialPlayer = Player(
      energy = 80,
      skills = com.example.model.PlayerSkills(
        technique = 40,
        techniqueXp = 70, // 70 XP + 50 XP training will trigger level up to 41!
        power = 35,
        powerXp = 20
      )
    )

    // Train Technique with Balanced intensity (50 XP, -15 HP)
    val resultTech = com.example.model.CareerProgression.applyTraining(
      player = initialPlayer,
      skillType = com.example.model.SkillType.TECHNIQUE,
      intensity = com.example.model.TrainingIntensity.BALANCED
    )

    assertTrue(resultTech.didLevelUp)
    assertEquals(41, resultTech.newLevel)
    assertEquals(20, resultTech.player.skills.techniqueXp) // 120 % 100 = 20 XP
    assertEquals(65, resultTech.player.energy) // 80 - 15 = 65

    // Train Power with Light intensity (30 XP, -8 HP)
    val resultPower = com.example.model.CareerProgression.applyTraining(
      player = resultTech.player,
      skillType = com.example.model.SkillType.POWER,
      intensity = com.example.model.TrainingIntensity.LIGHT
    )

    assertEquals(35, resultPower.newLevel)
    assertEquals(50, resultPower.player.skills.powerXp) // 20 + 30 = 50
    assertEquals(57, resultPower.player.energy) // 65 - 8 = 57
  }

  @Test
  fun `verify stamina training expands max energy capacity`() {
    val initialPlayer = Player(
      energy = 100,
      maxEnergy = 100,
      skills = com.example.model.PlayerSkills(
        stamina = 49,
        staminaXp = 80 // +50 XP will level up to 50
      )
    )

    val resultStamina = com.example.model.CareerProgression.applyTraining(
      player = initialPlayer,
      skillType = com.example.model.SkillType.STAMINA,
      intensity = com.example.model.TrainingIntensity.BALANCED
    )

    assertTrue(resultStamina.didLevelUp)
    assertEquals(50, resultStamina.newLevel)
    assertTrue(resultStamina.maxEnergyExpanded)
    assertEquals(101, resultStamina.newMaxEnergy)
  }

  @Test
  fun `verify rest day recovers energy`() {
    val tiredPlayer = Player(energy = 40, maxEnergy = 100)
    val (restedPlayer, msg) = com.example.model.CareerProgression.applyRestDay(tiredPlayer)

    assertEquals(65, restedPlayer.energy)
    assertTrue(msg.contains("+25 HP"))
  }

  @Test
  fun `verify match evolution updates skills and stamina reduces fatigue`() {
    val playerLowStamina = Player(
      skills = com.example.model.PlayerSkills(
        technique = 30,
        power = 30,
        stamina = 20,
        accuracy = 30,
        vision = 30
      )
    )

    val (evolvedPlayer, report) = com.example.model.CareerProgression.calculateMatchEvolution(
      player = playerLowStamina,
      goals = 2,
      assists = 1,
      rating = 8.5f,
      successChances = 3,
      totalChances = 3
    )

    // Goals should give substantial Power and Accuracy XP
    assertTrue(report.xpGainedMap[com.example.model.SkillType.POWER.id] ?: 0 >= 80)
    assertTrue(report.xpGainedMap[com.example.model.SkillType.ACCURACY.id] ?: 0 >= 90)

    // Assists & successful chances should boost Technique & Vision
    assertTrue(report.xpGainedMap[com.example.model.SkillType.TECHNIQUE.id] ?: 0 >= 90)
    assertTrue(report.xpGainedMap[com.example.model.SkillType.VISION.id] ?: 0 >= 60)

    // Match duration should award stamina XP
    assertTrue(report.xpGainedMap[com.example.model.SkillType.STAMINA.id] ?: 0 >= 30)

    // Career level XP gained
    assertTrue(report.careerXpGained > 100)
    assertTrue(evolvedPlayer.careerXp > 0)
  }

  @Test
  fun `verify tactical action selection based on energy`() {
    // When energy is abundant (50 HP), all main actions should be available
    val actionsAt50Hp = com.example.model.TacticalActionType.getAvailableActions(50)
    assertTrue(actionsAt50Hp.contains(com.example.model.TacticalActionType.POWER_SHOT))
    assertTrue(actionsAt50Hp.contains(com.example.model.TacticalActionType.PASS))
    assertTrue(actionsAt50Hp.contains(com.example.model.TacticalActionType.PLACED_SHOT))
    assertTrue(actionsAt50Hp.contains(com.example.model.TacticalActionType.DRIBBLE))

    // When energy is tight (7 HP), Power shot (12 HP), Placed shot (10 HP), and Dribble (8 HP) are excluded, but Pass (6 HP) is available
    val actionsAt7Hp = com.example.model.TacticalActionType.getAvailableActions(7)
    assertEquals(1, actionsAt7Hp.size)
    assertEquals(com.example.model.TacticalActionType.PASS, actionsAt7Hp.first())

    // When player is completely exhausted (0 HP), only Desperate Shot is available
    val actionsAt0Hp = com.example.model.TacticalActionType.getAvailableActions(0)
    assertEquals(1, actionsAt0Hp.size)
    assertEquals(com.example.model.TacticalActionType.DESPERATE_SHOT, actionsAt0Hp.first())
    assertEquals(0, com.example.model.TacticalActionType.DESPERATE_SHOT.energyCost)
  }

  @Test
  fun `verify tactical action modifiers affect stats`() {
    val powerShot = com.example.model.TacticalActionType.POWER_SHOT
    assertTrue(powerShot.powerMultiplier > 1.0f)
    assertEquals(12, powerShot.energyCost)

    val passAction = com.example.model.TacticalActionType.PASS
    assertTrue(passAction.accuracyBonus > 0)
    assertEquals(6, passAction.energyCost)

    val placedShot = com.example.model.TacticalActionType.PLACED_SHOT
    assertTrue(placedShot.curlBonus > 0)
    assertEquals(10, placedShot.energyCost)
  }

  @Test
  fun `verify athlete age and starting career configuration`() {
    val youngPlayer = Player(
      name = "Endrick",
      nickname = "Joia",
      age = 16,
      nationality = "Brasil",
      position = com.example.model.PlayerPosition.ATACANTE,
      currentClubId = "santos_paulista"
    )

    assertEquals(16, youngPlayer.age)
    assertEquals("Endrick", youngPlayer.name)
    assertEquals("Joia", youngPlayer.nickname)
    assertTrue(youngPlayer.hasClub)
  }

  @Test
  fun `verify starting in random club assigns valid club directly`() {
    val clubs = com.example.model.DefaultClubs.getInitialClubs()
    val randomClub = clubs.random()

    val playerWithRandomClub = Player(
      name = "Lucas",
      age = 17,
      currentClubId = randomClub.id,
      isTrialCompleted = true
    )

    assertTrue(playerWithRandomClub.isTrialCompleted)
    assertTrue(playerWithRandomClub.hasClub)
    assertTrue(clubs.any { it.id == playerWithRandomClub.currentClubId })
  }

  @Test
  fun `verify hard mode scout trials evaluate drills and generate offers`() {
    val rookie = Player(
      name = "Vitor",
      age = 17,
      currentClubId = "",
      isTrialCompleted = false,
      skills = com.example.model.PlayerSkills(
        technique = 45,
        power = 40,
        stamina = 42,
        accuracy = 44,
        vision = 38,
        pace = 46
      )
    )

    assertFalse(rookie.isTrialCompleted)
    assertFalse(rookie.hasClub)

    // Test 1: Shooting drill with high quality timing
    val shootingScore = com.example.model.ScoutTrialEngine.calculateDrillScore(
      drillType = com.example.model.ScoutDrillType.SHOOTING,
      player = rookie,
      performanceQuality = 0.95f
    )
    assertTrue(shootingScore >= 70)

    // Test 2: Passing drill
    val passingScore = com.example.model.ScoutTrialEngine.calculateDrillScore(
      drillType = com.example.model.ScoutDrillType.PASSING,
      player = rookie,
      performanceQuality = 0.90f
    )
    assertTrue(passingScore >= 65)

    // Test 3: Agility drill
    val agilityScore = com.example.model.ScoutTrialEngine.calculateDrillScore(
      drillType = com.example.model.ScoutDrillType.AGILITY,
      player = rookie,
      performanceQuality = 0.92f
    )
    assertTrue(agilityScore >= 68)

    val overallScore = ((shootingScore * 0.4f) + (passingScore * 0.35f) + (agilityScore * 0.25f)).toInt()
    assertTrue(overallScore >= 70)

    val clubs = com.example.model.DefaultClubs.getInitialClubs()
    val highOffers = com.example.model.ScoutTrialEngine.generateTrialOffers(rookie, clubs, 85)

    assertTrue(highOffers.isNotEmpty())
    // Best offer should have high wage and top role
    val topOffer = highOffers.first()
    assertTrue(topOffer.wagePerMatch >= 750L)
    assertTrue(topOffer.squadRole.contains("Joia") || topOffer.squadRole.contains("Titular"))

    // Modest performance yields access division offers (starting the hard way)
    val lowOffers = com.example.model.ScoutTrialEngine.generateTrialOffers(rookie, clubs, 40)
    assertTrue(lowOffers.isNotEmpty())
    val humbleOffer = lowOffers.first()
    assertTrue(humbleOffer.wagePerMatch <= 300L)
    assertTrue(humbleOffer.squadRole.contains("Aposta") || humbleOffer.squadRole.contains("Experiência") || humbleOffer.squadRole.contains("Acesso"))

    // Signing contract integrates the player into the club
    val signedPlayer = rookie.copy(
      currentClubId = topOffer.club.id,
      wagePerMatch = topOffer.wagePerMatch,
      winBonus = topOffer.winBonus,
      goalBonus = topOffer.goalBonus,
      contractYears = topOffer.contractYears,
      isTrialCompleted = true
    )

    assertTrue(signedPlayer.isTrialCompleted)
    assertTrue(signedPlayer.hasClub)
    assertEquals(topOffer.club.id, signedPlayer.currentClubId)
    assertEquals(topOffer.wagePerMatch, signedPlayer.wagePerMatch)
  }

  @Test
  fun `verify nss lob shot elevation over defenders`() {
    val physics = com.example.gameplay.physics.SoccerPhysicsEngine()
    // Striking bottom of the ball (y = 0.8f) in NSS elevates the ball
    physics.launchShot(
      startX = 0.5f,
      startY = 0.7f,
      targetX = 0.5f,
      targetY = 0.2f,
      impactOffset = androidx.compose.ui.geometry.Offset(0f, 0.8f),
      powerInput = 0.85f,
      playerPower = 60,
      playerCurl = 50,
      playerAccuracy = 70
    )

    assertTrue(physics.vz > 0.015f)
    // Run 15 physics frames
    for (i in 0 until 15) {
      physics.update(0.016f)
    }
    // Altitude Z should be well above ground (> 0.12f clears ground defenders!)
    assertTrue(physics.ballZ > 0.12f)
  }

  @Test
  fun `verify nss curve shot sidespin bends trajectory`() {
    val physics = com.example.gameplay.physics.SoccerPhysicsEngine()
    // Striking left side of the ball (x = -0.9f) in NSS causes curve to the right
    physics.launchShot(
      startX = 0.5f,
      startY = 0.7f,
      targetX = 0.5f,
      targetY = 0.15f,
      impactOffset = androidx.compose.ui.geometry.Offset(-0.9f, 0.2f),
      powerInput = 0.8f,
      playerPower = 55,
      playerCurl = 85, // High technique/curl
      playerAccuracy = 70
    )

    assertTrue(physics.spinZ > 0f)
    for (i in 0 until 20) {
      physics.update(0.016f)
    }
    // Trajectory curved to the right (ballX > 0.5f)
    assertTrue(physics.ballX > 0.51f)
  }
}


