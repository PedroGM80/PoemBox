package dev.pgm.poembox.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.pgm.poembox.presentation.MainActivity
import dev.pgm.poembox.presentation.theme.ColorPoemField
import dev.pgm.poembox.repository.User
import kotlinx.coroutines.delay


/**
 * Go screen tab
 *
 * @param navController
 */
@Composable
fun GoScreenTab(navController: NavController) {

    LaunchedEffect(key1 = true) {
        delay(0)
        navController.navigate(ScreensRouteList.RouteScreenTabs.route) {
            popUpTo(0)
        }
    }
}

/**
 * User login
 *
 * @param navController
 * @param userData
 */
@Composable
fun UserLogin(navController: NavController, userData: String) {
    val dataSplit = userData.split("#")
    val userLoaded = dataSplit[1]
    val mailLoaded = dataSplit[0]
    Log.i(":::Data", userLoaded)
    Log.i(":::DataB", mailLoaded)
    val user = User(userLoaded, mailLoaded)
    var next: Boolean by remember { mutableStateOf(false) }
    if (next) {
        GoScreenTab(navController)
    }
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val username = remember { mutableStateOf(TextFieldValue(text = user.userName.toString())) }
        val eMail =
            remember { mutableStateOf(TextFieldValue(text = user.userMail.toString())) }

        Text(text = "Login", style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Serif))

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            colors = ColorPoemField,
            label = { Text(text = "User") },
            value = username.value,
            onValueChange = { })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            colors = ColorPoemField,
            label = { Text(text = "Email") },
            value = eMail.value,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { })

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                    val activity = MainActivity()
                    activity.user.userName = user.userName.toString()
                    activity.user.userMail = user.userMail.toString()
                    next = true
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Login")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
