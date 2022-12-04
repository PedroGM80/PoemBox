package dev.pgm.poembox.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.pgm.poembox.MainActivity
import dev.pgm.poembox.MainActivity.Companion.USER_DATA
import dev.pgm.poembox.roomUtils.User
import dev.pgm.poembox.ui.theme.Purple700


@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun CreateAccount(navController: NavController) {
    var next: Boolean by remember { mutableStateOf(false) }
    if (next) {
        GoScreenTab(navController)
    }

    val user = User(null, null)//Create user with null values
    if (user.existUserRegister()) {

        val userData = USER_DATA
        val userDataSplit = userData.split("#")

        user.userName = userDataSplit[0]
        user.mail = userDataSplit[1]

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

            Text(text = "Sing up", style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Serif))

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Username") },
                value = username.value,
                onValueChange = { username.value = it })

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Email") },
                value = eMail.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { eMail.value = it })

            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = {
                        user.userName = username.value.text
                        user.mail = eMail.value.text
                        MainActivity().saveUser(user)
                        next = true
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Create your account")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }


}

@RequiresApi(Build.VERSION_CODES.M)
@Preview(showBackground = true)
@Composable
fun PrevScreenCreateAccount() {
    CreateAccount(rememberNavController())
}