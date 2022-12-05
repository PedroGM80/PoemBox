package dev.pgm.poembox.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.pgm.poembox.MainActivity
import dev.pgm.poembox.roomUtils.User
import dev.pgm.poembox.ui.theme.Purple700
import kotlinx.coroutines.delay

@Composable
fun GoScreenTab(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(0)
        navController.navigate(ScreensRouteList.RouteScreenTabs.route) {
            popUpTo(0)
        }
    }
}


@Composable
fun UserLogin(navController: NavController) {
    val loadedUserData = MainActivity.USER_DATA
    val dataSplit = loadedUserData.split("#")
    val userLoaded = dataSplit[0]
    val mailLoaded = dataSplit[1]

    Log.i(":::Data", userLoaded)
    Log.i(":::DataB", mailLoaded)
    val user = User(userLoaded, mailLoaded)
    var next: Boolean by remember { mutableStateOf(false) }
    if (next) {
        GoScreenTab(navController)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ClickableText(
            text = AnnotatedString("Sign up here"),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = {
                navController.navigate(ScreensRouteList.RouteScreenTabs.route) {
                    popUpTo(0)
                }
            },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Purple700
            )
        )
    }
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val username = remember { mutableStateOf(TextFieldValue()) }
        val eMail = remember { mutableStateOf(TextFieldValue()) }

        Text(text = "Login", style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Serif))

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = user.userName.toString()) },
            value = username.value,
            onValueChange = { username.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = user.userName.toString()) },
            value = eMail.value,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { eMail.value = it })

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
        /*ClickableText(
            text = AnnotatedString("Forgot password?"),
            onClick = { },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default
            )
        )*/
    }
    //  }


}

/*
@RequiresApi(Build.VERSION_CODES.M)
@Preview(showBackground = true)
@Composable
fun PrevScreenUserLogin() {
    UserLogin(rememberNavController())
}*/
