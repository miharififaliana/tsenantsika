package com.example.tsenantsika.network.models

import kotlinx.serialization.Serializable

@Serializable
enum class WsEventType {
    VENTE_CREEE, VENTE_MODIFIEE, VENTE_SUPPRIMEE,
    AVANCE_AJOUTEE, DEPENSE_AJOUTEE,
    JOURNEE_OUVERTE, JOURNEE_CLOTUREE,
    PARAMETRE_COMMISSION_MODIFIE, CATEGORIE_MODIFIEE,
    PING
}

@Serializable
data class WsEvent(
    val type: WsEventType,
    val payload: String = ""
)
