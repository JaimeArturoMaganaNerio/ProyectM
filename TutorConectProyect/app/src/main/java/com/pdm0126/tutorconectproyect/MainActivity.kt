package com.pdm0126.tutorconectproyect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pdm0126.tutorconectproyect.ui.theme.TutorConectProyectTheme
import com.tutorconnect.core.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TutorConectProyectTheme {
                AppNavigation()
                }
            }
        }
    }

