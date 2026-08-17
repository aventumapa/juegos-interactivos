package com.aventumapa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aventumapa.app.ui.AventuMapaRoot
import com.aventumapa.app.ui.theme.AventuMapaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AventuMapaTheme {
                AventuMapaRoot()
            }
        }
    }
}

