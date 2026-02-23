package com.example.compose.ui.main.component

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.compose.ui.theme.ComposeTheme

/**
 *
 */
class ComponentSampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeTheme {
                Surface(
                    modifier = Modifier
                        .systemBarsPadding(),
                    color = Color.White
                ) {
                    FullscreenScreen()
                }
            }
        }
    }

    @Composable
    private fun FullscreenScreen(){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
        }
    }

}



