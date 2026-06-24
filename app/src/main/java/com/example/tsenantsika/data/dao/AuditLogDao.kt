package com.example.tsenantsika.data.dao

import androidx.room.*
import com.example.tsenantsika.data.entities.ActionAudit
import com.example.tsenantsika.data.entities.AuditLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {

    @Insert
    suspend fun insert(log: AuditLog): Long

    @Query("SELECT * FROM audit_logs ORDER BY dateHeure DESC")
    fun getAllLogs(): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs WHERE `action` = :action ORDER BY dateHeure DESC")
    fun getLogsByAction(action: ActionAudit): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs WHERE idEntiteConcernee = :entityId ORDER BY dateHeure DESC")
    fun getLogsByEntity(entityId: String): Flow<List<AuditLog>>
}