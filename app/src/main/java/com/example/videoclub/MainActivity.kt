package com.example.videoclub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.videoclub.home.HomeScreen
import com.example.videoclub.home.HomeUiState
import com.example.videoclub.ui.theme.VideoClubTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VideoClubTheme {
                HomeScreen()
            }
        }
    }
}

