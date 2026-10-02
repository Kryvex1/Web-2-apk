package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WebProjectDao {
    @Query("SELECT * FROM web_projects ORDER BY lastBuildTime DESC, id DESC")
    fun getAllProjectsFlow(): Flow<List<WebProjectEntity>>

    @Query("SELECT * FROM web_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): WebProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: WebProjectEntity): Long

    @Update
    suspend fun updateProject(project: WebProjectEntity)

    @Delete
    suspend fun deleteProject(project: WebProjectEntity)

    @Query("DELETE FROM web_projects WHERE id = :id")
    suspend fun deleteById(id: Long)
}
