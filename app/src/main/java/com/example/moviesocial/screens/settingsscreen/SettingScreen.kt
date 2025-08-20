package com.example.moviesocial.screens.settingsscreen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moviesocial.screens.navigation.Screen
import com.example.moviesocial.ui.theme.Purple80
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingScreen(navController: NavController) {
    val viewModel = koinViewModel<SettingsViewModel>()

    val settingsState by viewModel.settingsState
    val dropdownExpanded by viewModel.dropdownExpanded

    // Load settings
    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                modifier = Modifier.size(60.dp),
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Purple80,
            )
            Spacer(modifier = Modifier.height(20.dp))

            DropDownFun(
                selectIndex = settingsState.selectedUIIndex,
                onIndexChange = viewModel::updateUISelection,
                dropControl = dropdownExpanded,
                onDropControlChange = viewModel::setDropdownExpanded,
                uiList = settingsState.uiOptions,
                uiDescription = "You can change your UI:"
            )

            Spacer(modifier = Modifier.height(20.dp))

            AnimationSwitch(
                animationDescription = "You can turn off animations:",
                isAnimation = settingsState.isAnimationEnabled,
                viewModel::updateAnimationSetting
            )

            Spacer(modifier = Modifier.height(20.dp))

            SaveButton(viewModel::saveSettings)

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                navController.navigate(Screen.QrScreen.rout)
            }) {
                Text("Go to qr code")
            }
        }
    }
}

@Composable
fun DropDownFun(
    selectIndex: Int,
    onIndexChange: (Int) -> Unit,
    dropControl: Boolean,
    onDropControlChange: (Boolean) -> Unit,
    uiList: List<String>,
    uiDescription: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(uiDescription)
        OutlinedCard(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentWidth()
                    .height(50.dp)
                    .padding(5.dp)
                    .clickable {
                        onDropControlChange(true)
                    }
            ) {
                Text(text = uiList[selectIndex])
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = dropControl,
                onDismissRequest = { onDropControlChange(false) }
            ) {
                uiList.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            onIndexChange(index)
                            onDropControlChange(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimationSwitch(
    animationDescription: String,
    isAnimation: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(animationDescription)
        Switch(
            modifier = Modifier.padding(start = 20.dp),
            checked = isAnimation,
            onCheckedChange = onCheckedChange,
            thumbContent = if (isAnimation) {
                {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            } else {
                null
            }
        )
    }
}

@Composable
fun SaveButton(onSave: () -> Unit) {
    Button(onClick = onSave) {
        Text("Save")
    }
}