package dev.pgm.poembox.presentation.components

import androidx.compose.runtime.Composable
import dev.pgm.poembox.R
import dev.pgm.poembox.presentation.components.TabItem.Editor.userData
import dev.pgm.poembox.presentation.content.EditScreen
import dev.pgm.poembox.presentation.content.ManagerScreen
import dev.pgm.poembox.presentation.content.MonitoringScreen

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem(var icon: Int, var title: String, var screen: ComposableFun) {
    var userData: String = ""

    @JvmName("setUserData1")
    fun setUserData(data: String) {
        userData = data
    }

    object Editor : TabItem(R.drawable.ic_edit, "Edit", { EditScreen(userData) })
    object Monitor : TabItem(R.drawable.ic_monitor, "Analyze", { MonitoringScreen() })
    object Manager : TabItem(R.drawable.ic_manager, "Manager", { ManagerScreen() })
}