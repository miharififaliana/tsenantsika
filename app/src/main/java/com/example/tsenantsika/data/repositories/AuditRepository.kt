package com.example.tsenantsika.data.repositories

import com.example.tsenantsika.data.entities.ActionAudit
import com.example.tsenantsika.data.entities.AuditLog
import java.time.Instant

class AuditRepository(private val auditLogRepository: AuditLogRepository) {

    suspend fun log(
        action: ActionAudit,
        utilisateurId: Long,
        details: String,
        idEntiteConcernee: Long? = null
    ) {
        auditLogRepository.insert(
            AuditLog(
                action = action,
                utilisateurId = utilisateurId,
                dateHeure = Instant.now(),
                details = details,
                idEntiteConcernee = idEntiteConcernee?.toString()
            )
        )
    }
}
