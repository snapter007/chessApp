package com.chessforge.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.chessforge.app.ui.navigation.ChessForgeNavGraph
import com.chessforge.app.ui.theme.Background
import com.chessforge.app.ui.theme.ChessForgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChessForgeTheme {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background),
                ) {
                    ChessForgeNavGraph()
                }
            }
        }
    }
}
