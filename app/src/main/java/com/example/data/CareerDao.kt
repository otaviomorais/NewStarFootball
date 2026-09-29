package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CareerDao {
  @Query("SELECT * FROM career WHERE id = 1 LIMIT 1")
  fun getCareerFlow(): Flow<CareerEntity?>

  @Query("SELECT * FROM career WHERE id = 1 LIMIT 1")
  suspend fun getCareerOnce(): CareerEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveCareer(career: CareerEntity)

  @Query("DELETE FROM career")
  suspend fun resetCareer()

  @Query("SELECT * FROM clubs ORDER BY points DESC, (goalsFor - goalsAgainst) DESC, goalsFor DESC")
  fun getClubsFlow(): Flow<List<ClubEntity>>

  @Query("SELECT * FROM clubs ORDER BY points DESC, (goalsFor - goalsAgainst) DESC, goalsFor DESC")
  suspend fun getClubsOnce(): List<ClubEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertClubs(clubs: List<ClubEntity>)

  @Query("DELETE FROM clubs")
  suspend fun clearClubs()
}
