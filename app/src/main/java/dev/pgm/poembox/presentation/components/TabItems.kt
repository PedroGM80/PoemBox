package dev.pgm.poembox.presentation.components

import androidx.compose.runtime.Composable
import dev.pgm.poembox.presentation.components.TabItem.Editor.userData
import dev.pgm.poembox.presentation.content.EditScreen
import dev.pgm.poembox.presentation.content.ManagerScreen
import dev.pgm.poembox.presentation.content.MonitoringScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

typealias ComposableFun = @Composable () -> Unit

/**
 * Tab item
 *
 * @constructor Create empty Tab item
 * @property icon
 * @property title
 * @property screen
 */
sealed class TabItem(var icon: ImageVector, var title: String, var screen: ComposableFun) {
    var userData: String = ""

    /**
     * Set user data
     *
     * @param data
     */
    @JvmName("setUserData1")
    fun setUserData(data: String) {
        userData = data
    }

    object Editor : TabItem(Icons.Filled.Edit, "Edit", { EditScreen(userData) })
    object Monitor : TabItem(Icons.Filled.Analytics, "Analyze", { MonitoringScreen() })
    object Manager : TabItem(Icons.Filled.List, "Manager", { ManagerScreen() })
}