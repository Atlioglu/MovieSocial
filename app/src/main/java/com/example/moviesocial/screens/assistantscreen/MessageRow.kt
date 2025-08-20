package com.example.moviesocial.screens.assistantscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviesocial.model.ChatModel
import com.example.moviesocial.model.FunctionCall
import com.example.moviesocial.ui.theme.ColorModelMessage
import com.example.moviesocial.ui.theme.ColorUserMessage

@Composable
fun MessageRow(
    chatModel: ChatModel,
    messageIndex: Int,
    functionCalls: Map<Int, List<FunctionCall>>,
    onFunctionCall: (String, String?) -> Unit = { _, _ -> }
) {
    val isModel = chatModel.role == "model"

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if (isModel) 8.dp else 70.dp,
                        end = if (isModel) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(48f))
                    .background(if (isModel) ColorModelMessage else ColorUserMessage)
                    .padding(16.dp)
            ) {
                Column {
                    SelectionContainer {
                        Text(
                            text = chatModel.message,
                            fontWeight = FontWeight.W500,
                            color = Color.White
                        )
                    }

                    // Add clickable buttons for each function call
                    functionCalls[messageIndex]?.let { calls ->
                        if (calls.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))

                            calls.forEach { functionCall ->
                                Button(
                                    onClick = {
                                        onFunctionCall(functionCall.action, functionCall.movieName)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when (functionCall.type) {
                                            "movie_search" -> Color.White.copy(alpha = 0.3f)
                                            else -> Color.White.copy(alpha = 0.2f)
                                        }
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (functionCall.type == "movie_search") {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Search",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = functionCall.label,
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}