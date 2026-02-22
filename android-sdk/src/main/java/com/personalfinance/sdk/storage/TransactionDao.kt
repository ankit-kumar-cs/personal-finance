package com.personalfinance.sdk.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE sync_status = 'PENDING' ORDER BY timestamp ASC")
    suspend fun pending(): List<TransactionEntity>

    @Query("UPDATE transactions SET sync_status = 'SYNCED' WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)

    @Query("SELECT COUNT(*) FROM transactions WHERE sync_status = 'PENDING'")
    suspend fun pendingCount(): Int

    @Query("UPDATE transactions SET category = :newCategory WHERE id = :id")
    suspend fun updateCategory(id: String, newCategory: String)
}
