package com.brightcove.player.samples.compose.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.brightcove.player.samples.compose.activity.ui.BrightcoveExoPlayerVideo
import com.brightcove.player.samples.compose.activity.ui.theme.AndroidplayersamplesTheme

class MainActivity : BrightcoveComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidplayersamplesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BrightcoveExoPlayerVideo(modifier = Modifier.fillMaxSize(),
                        initializeLifecycleUtil = ::initializeLifecycleUtil )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidplayersamplesTheme {
        BrightcoveExoPlayerVideo(modifier = Modifier.fillMaxSize(), {})
    }
}