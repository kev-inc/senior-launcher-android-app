package com.example.seniorlauncher

import android.app.Activity
import android.os.Bundle
import android.view.Surface
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity


class SettingsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SettingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val activity = LocalContext.current as? Activity // Get the current Activity

    var darkMode by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }
    var username by remember { mutableStateOf("") }

    val languages = listOf("English", "Spanish", "French")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp), ) {

                // Dark Mode Switch
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(text = "Enable Dark Mode", modifier = Modifier.weight(1f))
                    Switch(checked = darkMode, onCheckedChange = { darkMode = it })
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Language Selection Dropdown
                ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}) {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        label = { Text("Select Language") },
                        readOnly = true
                    )
                    DropdownMenu(expanded = false, onDismissRequest = {}) {
                        languages.forEach { language ->
                            DropdownMenuItem(text = { Text(language) }, onClick = { selectedLanguage = language })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Username Input
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }


}