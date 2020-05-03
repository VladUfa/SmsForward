package com.pierreduchemin.smsforward.data.source.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pierreduchemin.smsforward.data.ForwardModel

/**
 * Data Access Object for the tasks table.
 */
@Dao
interface ForwardModelDao {

    @Query("SELECT * FROM ForwardModel WHERE id = 1")
    fun observeForwardModel(): LiveData<ForwardModel?>

    @Query("SELECT * FROM ForwardModel WHERE id = 1")
    fun getForwardModel(): ForwardModel?

    @Query("SELECT COUNT(*) FROM ForwardModel")
    fun countForwardModel(): LiveData<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForwardModel(forwardModel: ForwardModel): Long

    @Update
    fun updateForwardModel(forwardModel: ForwardModel)

    @Query("DELETE FROM ForwardModel WHERE id = :id")
    fun deleteForwardModelById(id: Long): Int
}