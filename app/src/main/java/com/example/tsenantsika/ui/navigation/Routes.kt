package com.example.tsenantsika.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val CREATE_PATRONNE = "create_patronne"
    const val LOGIN = "login"
    const val PATRONNE_HOME = "patronne_home"
    const val EMPLOYE_HOME = "employe_home"
    const val SETTINGS = "settings"
    const val EMPLOYE_MANAGEMENT = "employe_management"
    const val JOURNEE_HISTORIQUE = "journee_historique"
    const val VENTE_LIST = "vente_list"
    const val VENTE_CREATE = "vente_create"
    const val VENTE_EDIT = "vente_edit/{venteId}"
    const val AVANCES = "avances"
    const val DEPENSES = "depenses"
    const val ADMIN_SETTINGS = "admin_settings"
    const val AUDIT = "audit"

    fun venteEdit(id: Long) = "vente_edit/$id"
}
