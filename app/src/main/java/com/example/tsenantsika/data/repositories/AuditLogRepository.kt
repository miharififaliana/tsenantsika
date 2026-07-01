package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.database.BoutiqueDatabase
import com.example.tsenantsika.data.entities.ActionAudit
import com.example.tsenantsika.data.entities.AuditLog
import kotlinx.coroutines.flow.Flow

class AuditLogRepository(database: BoutiqueDatabase) : BaseRepository(database) {

    private val dao = database.auditLogDao()

    suspend fun insert(log: AuditLog): Long = safeCall { dao.insert(log) }

    fun getAllLogs(): Flow<List<AuditLog>> = dao.getAllLogs()

    fun getLogsByAction(action: ActionAudit): Flow<List<AuditLog>> = dao.getLogsByAction(action)
}