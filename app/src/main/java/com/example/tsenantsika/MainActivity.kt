package com.example.tsenantsika

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tsenantsika.ui.common.rememberBoutiqueSnackbarHostState
import com.example.tsenantsika.ui.common.BoutiqueSnackbarHost
import com.example.tsenantsika.ui.navigation.BoutiqueNavGraph
import com.example.tsenantsika.ui.theme.TsenantsikaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TsenantsikaTheme {
                val snackbarState = rememberBoutiqueSnackbarHostState()
                androidx.compose.material3.Scaffold(snackbarHost = { BoutiqueSnackbarHost(snackbarState) }) {
                    BoutiqueNavGraph()
                }
            }
        }
    }
}
