package com.example.data

import com.example.model.Club
import com.example.model.DefaultClubs
import com.example.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CareerRepository(private val dao: CareerDao) {

  val careerPlayerFlow: Flow<Player?> = dao.getCareerFlow().map { entity ->
    entity?.toPlayer()
  }

  val ownedItemsFlow: Flow<Set<String>> = dao.getCareerFlow().map { entity ->
    entity?.ownedItemsIds?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
  }

  val clubsFlow: Flow<List<Club>> = dao.getClubsFlow().map { list ->
    if (list.isEmpty()) {
      DefaultClubs.getInitialClubs()
    } else {
      list.map { entity ->
        Club(
          id = entity.id,
          name = entity.name,
          shortName = entity.shortName,
          primaryColorHex = entity.primaryColorHex,
          secondaryColorHex = entity.secondaryColorHex,
          reputation = entity.reputation,
          stadiumName = entity.stadiumName,
          tier = entity.tier,
          points = entity.points,
          played = entity.played,
          won = entity.won,
          drawn = entity.drawn,
          lost = entity.lost,
          goalsFor = entity.goalsFor,
          goalsAgainst = entity.goalsAgainst
        )
      }
    }
  }

  suspend fun savePlayerAndItems(player: Player, ownedItems: Set<String>) {
    dao.saveCareer(CareerEntity.fromPlayer(player, ownedItems))
  }

  suspend fun saveClubs(clubs: List<Club>) {
    val entities = clubs.map { club ->
      ClubEntity(
        id = club.id,
        name = club.name,
        shortName = club.shortName,
        primaryColorHex = club.primaryColorHex,
        secondaryColorHex = club.secondaryColorHex,
        reputation = club.reputation,
        stadiumName = club.stadiumName,
        tier = club.tier,
        points = club.points,
        played = club.played,
        won = club.won,
        drawn = club.drawn,
        lost = club.lost,
        goalsFor = club.goalsFor,
        goalsAgainst = club.goalsAgainst
      )
    }
    dao.insertClubs(entities)
  }

  suspend fun initializeClubsIfEmpty() {
    val existing = dao.getClubsOnce()
    if (existing.isEmpty()) {
      saveClubs(DefaultClubs.getInitialClubs())
    }
  }

  suspend fun resetCareer() {
    dao.resetCareer()
    dao.clearClubs()
    saveClubs(DefaultClubs.getInitialClubs())
  }
}
