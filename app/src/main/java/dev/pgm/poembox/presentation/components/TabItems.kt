package dev.pgm.poembox.presentation.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import dev.pgm.poembox.R
import dev.pgm.poembox.presentation.content.EditScreen
import dev.pgm.poembox.presentation.content.ManagerScreen
import dev.pgm.poembox.presentation.content.MonitoringScreen

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem(var icon: ImageVector, @StringRes var title: Int, var screen: ComposableFun) {
    object Editor : TabItem(Icons.Filled.Edit, R.string.tab_editor, { EditScreen() })
    object Monitor : TabItem(Icons.Filled.Analytics, R.string.tab_monitor, { MonitoringScreen() })
    object Manager : TabItem(Icons.Filled.List, R.string.tab_manager, { ManagerScreen() })
}
