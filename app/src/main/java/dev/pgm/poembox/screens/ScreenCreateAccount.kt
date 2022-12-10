package dev.pgm.poembox.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.pgm.poembox.MainActivity
import dev.pgm.poembox.roomUtils.User


@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun CreateAccount(navController: NavController) {
    val user = User(null, null)//Create user with null values

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val userInputName = remember { mutableStateOf(TextFieldValue()) }
        val userInputMail = remember { mutableStateOf(TextFieldValue()) }

        Text(
            text = "Sing up",
            style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Serif)
        )

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Username") },
            value = userInputName.value,
            onValueChange = { userInputName.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Email") },
            value = userInputMail.value,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { userInputMail.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                 /*   user.userName = userInputName.value.text
                    user.userMail = userInputMail.value.text*/
                    val activity = MainActivity()
                    activity.user.userName = user.userName.toString()
                    activity.user.userMail = user.userMail.toString()
                    activity.saveUser(user)
                    navController.navigate(ScreensRouteList.RouteScreenTabs.route) {
                        popUpTo(0)
                    }
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


@RequiresApi(Build.VERSION_CODES.M)
@Preview(showBackground = true)
@Composable
fun PrevScreenCreateAccount() {
    CreateAccount(rememberNavController())
}