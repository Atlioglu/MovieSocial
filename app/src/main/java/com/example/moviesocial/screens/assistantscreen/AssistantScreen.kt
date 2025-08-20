package com.example.moviesocial.screens.assistantscreen


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moviesocial.model.ChatModel
import com.example.moviesocial.model.FunctionCall
import com.example.moviesocial.screens.navigation.Screen
import com.example.moviesocial.ui.theme.Purple80
import org.koin.androidx.compose.koinViewModel


@Composable
fun AssistantScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val viewModel = koinViewModel<AssistantViewModel>()

    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        MessageList(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            messageList = viewModel.messageList,
            functionCalls = viewModel.functionCalls,
            onFunctionCall = { action, movieName ->
                when (action) {
                    "navigate_to_favorites" -> {
                        navController.navigate(Screen.Fav.rout)
                    }
                    "search_movie" -> {
                        val text = movieName
                        val duration = Toast.LENGTH_SHORT
                        Toast.makeText(context, text, duration).show()

                        movieName?.let {
                            navController.navigate(Screen.Search.rout + "$movieName")
                        }
                    }

                }
            }
        )
        MessageInput(
            onMessageSend = {
                viewModel.sendMessage(it)
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}



@Composable
fun MessageInput(onMessageSend : (String)-> Unit,modifier: Modifier = Modifier,
) {
    var message by remember {
        mutableStateOf("")
    }

    Row (
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = modifier.weight(1f),
            value = message,
            onValueChange = {
                message = it
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if(message.isNotEmpty()){
                            onMessageSend(message)
                            message = ""
                        }
                    }
                )
                {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send"
                    )
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (message.isNotBlank()) {
                        onMessageSend(message)
                        message = ""
                    }
                }
            ),
            singleLine = false
        )
    }
}



@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messageList: List<ChatModel>,
    functionCalls: Map<Int, List<FunctionCall>>,
    onFunctionCall: (String, String?) -> Unit = { _, _ -> }
) {
    if (messageList.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                imageVector = Icons.Default.Face,
                contentDescription = "Icon",
                tint = Purple80,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Ask me anything!",
                fontSize = 22.sp,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                ),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            reverseLayout = true
        ) {
            itemsIndexed(messageList.reversed()) { index, item ->
                val originalIndex = messageList.size - 1 - index
                MessageRow(
                    chatModel = item,
                    messageIndex = originalIndex,
                    functionCalls = functionCalls,
                    onFunctionCall = onFunctionCall
                )
            }
        }
    }
}

