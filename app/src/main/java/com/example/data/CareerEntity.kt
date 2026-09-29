package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Player
import com.example.model.PlayerPosition
import com.example.model.PlayerRelationships
import com.example.model.PlayerSkills

@Entity(tableName = "career")
data class CareerEntity(
  @PrimaryKey val id: Int = 1,
  val name: String,
  val nickname: String,
  val age: Int,
  val nationality: String,
  val preferredFoot: String,
  val position: String,
  val number: Int,

  // Energy & Stamina System
  val energy: Int,
  val maxEnergy: Int = 100,

  // Career Progression
  val careerLevel: Int = 1,
  val careerXp: Int = 0,

  val money: Long,
  val wagePerMatch: Long,
  val winBonus: Long,
  val goalBonus: Long,
  val contractYears: Int,
  val currentClubId: String,

  // Attributes
  val technique: Int = 35,
  val power: Int,
  val stamina: Int = 40,
  val curl: Int,
  val accuracy: Int,
  val vision: Int,
  val pace: Int,
  val freeKick: Int,

  // Sub-level XP
  val techniqueXp: Int = 0,
  val powerXp: Int = 0,
  val staminaXp: Int = 0,
  val curlXp: Int = 0,
  val accuracyXp: Int = 0,
  val visionXp: Int = 0,
  val paceXp: Int = 0,
  val freeKickXp: Int = 0,

  // Social
  val relCoach: Int,
  val relTeammates: Int,
  val relFans: Int,
  val relSponsor: Int,
  val relPartner: Int,
  val happiness: Int = 85,
  val isTrialCompleted: Boolean = true,
  val scoutScore: Int = 75,
  val lifestylePrestige: Int,
  val equippedBootsId: String,
  val ownedItemsIds: String,

  // Career Statistics
  val matchesPlayed: Int,
  val goalsScored: Int,
  val assistsGiven: Int,
  val mvpAwards: Int,
  val trophiesWon: Int,
  val seasonMatches: Int,
  val seasonGoals: Int,
  val seasonAssists: Int
) {
  fun toPlayer(): Player {
    val pos = try {
      PlayerPosition.valueOf(position)
    } catch (_: Exception) {
      PlayerPosition.ATACANTE
    }
    return Player(
      name = name,
      nickname = nickname,
      age = age,
      nationality = nationality,
      preferredFoot = preferredFoot,
      position = pos,
      number = number,
      energy = energy,
      maxEnergy = if (maxEnergy > 0) maxEnergy else 100,
      careerLevel = if (careerLevel > 0) careerLevel else 1,
      careerXp = careerXp,
      money = money,
      wagePerMatch = wagePerMatch,
      winBonus = winBonus,
      goalBonus = goalBonus,
      contractYears = contractYears,
      currentClubId = currentClubId,
      skills = PlayerSkills(
        technique = technique,
        power = power,
        stamina = stamina,
        curl = curl,
        accuracy = accuracy,
        vision = vision,
        pace = pace,
        freeKick = freeKick,
        techniqueXp = techniqueXp,
        powerXp = powerXp,
        staminaXp = staminaXp,
        curlXp = curlXp,
        accuracyXp = accuracyXp,
        visionXp = visionXp,
        paceXp = paceXp,
        freeKickXp = freeKickXp
      ),
      relationships = PlayerRelationships(
        coach = relCoach,
        teammates = relTeammates,
        fans = relFans,
        sponsor = relSponsor,
        partner = relPartner
      ),
      happiness = happiness,
      isTrialCompleted = isTrialCompleted,
      scoutScore = scoutScore,
      lifestylePrestige = lifestylePrestige,
      equippedBootsId = equippedBootsId,
      matchesPlayed = matchesPlayed,
      goalsScored = goalsScored,
      assistsGiven = assistsGiven,
      mvpAwards = mvpAwards,
      trophiesWon = trophiesWon,
      seasonMatches = seasonMatches,
      seasonGoals = seasonGoals,
      seasonAssists = seasonAssists
    )
  }

  companion object {
    fun fromPlayer(player: Player, ownedItems: Set<String>): CareerEntity {
      return CareerEntity(
        id = 1,
        name = player.name,
        nickname = player.nickname,
        age = player.age,
        nationality = player.nationality,
        preferredFoot = player.preferredFoot,
        position = player.position.name,
        number = player.number,
        energy = player.energy,
        maxEnergy = player.maxEnergy,
        careerLevel = player.careerLevel,
        careerXp = player.careerXp,
        money = player.money,
        wagePerMatch = player.wagePerMatch,
        winBonus = player.winBonus,
        goalBonus = player.goalBonus,
        contractYears = player.contractYears,
        currentClubId = player.currentClubId,
        technique = player.skills.technique,
        power = player.skills.power,
        stamina = player.skills.stamina,
        curl = player.skills.curl,
        accuracy = player.skills.accuracy,
        vision = player.skills.vision,
        pace = player.skills.pace,
        freeKick = player.skills.freeKick,
        techniqueXp = player.skills.techniqueXp,
        powerXp = player.skills.powerXp,
        staminaXp = player.skills.staminaXp,
        curlXp = player.skills.curlXp,
        accuracyXp = player.skills.accuracyXp,
        visionXp = player.skills.visionXp,
        paceXp = player.skills.paceXp,
        freeKickXp = player.skills.freeKickXp,
        relCoach = player.relationships.coach,
        relTeammates = player.relationships.teammates,
        relFans = player.relationships.fans,
        relSponsor = player.relationships.sponsor,
        relPartner = player.relationships.partner,
        happiness = player.happiness,
        isTrialCompleted = player.isTrialCompleted,
        scoutScore = player.scoutScore,
        lifestylePrestige = player.lifestylePrestige,
        equippedBootsId = player.equippedBootsId,
        ownedItemsIds = ownedItems.joinToString(","),
        matchesPlayed = player.matchesPlayed,
        goalsScored = player.goalsScored,
        assistsGiven = player.assistsGiven,
        mvpAwards = player.mvpAwards,
        trophiesWon = player.trophiesWon,
        seasonMatches = player.seasonMatches,
        seasonGoals = player.seasonGoals,
        seasonAssists = player.seasonAssists
      )
    }
  }
}

@Entity(tableName = "clubs")
data class ClubEntity(
  @PrimaryKey val id: String,
  val name: String,
  val shortName: String,
  val primaryColorHex: Long,
  val secondaryColorHex: Long,
  val reputation: Int,
  val stadiumName: String,
  val tier: Int,
  val points: Int,
  val played: Int,
  val won: Int,
  val drawn: Int,
  val lost: Int,
  val goalsFor: Int,
  val goalsAgainst: Int
)
