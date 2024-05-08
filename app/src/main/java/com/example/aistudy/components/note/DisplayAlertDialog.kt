package com.example.aistudy.components.note

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.aistudy.R
import com.example.aistudy.components.CustomText
import com.example.aistudy.ui.theme.MediumSeaGreen
import com.example.aistudy.ui.theme.Red

/**
 * This composable creates a reusable custom alert dialog with the following features:
 *
 * *  Rounded container shape
 * *  Customizable title and message text
 * *  Two buttons with customizable labels and actions
 * *  Designed for flexibility to fit various use cases within the application
 */
@Composable
fun DisplayAlertDialog(
    title: String,
    message: String,
    openDialog: Boolean,
    button1Text: String,
    button2Text: String,
    onButton1Pressed: () -> Unit,
    onButton2Pressed: () -> Unit
) {
    // Show dialog only if openDialog is true
    if (openDialog) {
        Dialog(onDismissRequest = { }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colors.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier.padding(vertical = 32.dp, horizontal = 32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Displays the dialog title
                        CustomText(
                            text = title,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.secondary,
                            fontWeight = FontWeight.W700,
                            fontSize = 22.sp
                        )
                        Divider(
                            modifier = Modifier.height(24.dp),
                            color = MaterialTheme.colors.primary
                        )
                        // Displays the dialog message
                        CustomText(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = message,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.secondary,
                            fontWeight = FontWeight.W400,
                            fontSize = 18.sp
                        )
                        Divider(
                            modifier = Modifier.height(24.dp),
                            color = MaterialTheme.colors.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // First button with custom styling and action
                            Button(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Red),
                                onClick = {
                                    onButton1Pressed()
                                }) {
                                CustomText(
                                    text = button1Text,
                                    color = MaterialTheme.colors.secondary,
                                    fontWeight = FontWeight.W400,
                                    fontSize = 18.sp
                                )
                            }
                            Divider(
                                modifier = Modifier.width(32.dp),
                                color = MaterialTheme.colors.primary
                            )
                            // Second button with custom styling and action
                            Button(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = MediumSeaGreen),
                                onClick = {
                                    onButton2Pressed()
                                }) {
                                CustomText(
                                    text = button2Text,
                                    color = MaterialTheme.colors.secondary,
                                    fontWeight = FontWeight.W400,
                                    fontSize = 18.sp
                                )
                            }
                        }

                        Divider(
                            modifier = Modifier.height(16.dp),
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun DisplayAlertDialogPreview() {
    DisplayAlertDialog(
        title = stringResource(id = R.string.delete_note_alert_title),
        message = stringResource(id = R.string.delete_note_alert_message),
        openDialog = true,
        button1Text = "No",
        button2Text = "Yes",
        onButton1Pressed = { },
        onButton2Pressed = {

        })
}