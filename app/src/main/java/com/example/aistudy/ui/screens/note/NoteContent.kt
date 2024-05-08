package com.example.aistudy.ui.screens.note

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.aistudy.R
import com.example.aistudy.components.CustomText
import com.example.aistudy.components.note.CategoryDropDown
import com.example.aistudy.components.note.TranscriptItem
import com.example.aistudy.data.models.Note
import com.example.aistudy.data.notecontent.ContentItem
import com.example.aistudy.ui.theme.ChineseSilver
import com.example.aistudy.ui.theme.fontFamily
import com.example.aistudy.ui.viewmodels.SharedViewModel
import com.example.aistudy.utils.GlobalVariable
import com.example.aistudy.utils.dateToString
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun NoteContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    categoryId: Int,
    onCategoryChange: (Int) -> Unit,
    reminderDateTime: Date?,
    onReminderDateTimeChange: (Date) -> Unit,
    sharedViewModel: SharedViewModel,
    selectedNote: Note?,
    navigateToSpeech2TextScreen: (Int) -> Unit
) {
    val categories by sharedViewModel.categories.collectAsState()

    val contentItems by sharedViewModel.selectedNoteContentItems.collectAsState()

    // Find the selected category object based on the selectedCategoryId
    val selectedCategory = categories.find { it.id == sharedViewModel.categoryIDforNoteContent }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .verticalScroll(scrollState)
    ) {
        CustomText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            text = stringResource(id = R.string.title),
            color = MaterialTheme.colors.secondary,
            fontSize = 20.sp,
            fontWeight = FontWeight.W600
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = { text ->
                if (text.length <= 35) onTitleChange(text)
            },
            placeholder = {
                CustomText(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    text = stringResource(id = R.string.enter_title),
                    color = ChineseSilver,
                    fontWeight = FontWeight.W300,
                    fontSize = 18.sp
                )
            },
            textStyle = TextStyle(
                color = MaterialTheme.colors.secondary,
                fontFamily = fontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 18.sp
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = MaterialTheme.colors.secondary,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent
            )
        )

        Divider(
            modifier = Modifier.height(25.dp), color = MaterialTheme.colors.primary
        )

        CustomText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            text = stringResource(id = R.string.category),
            color = MaterialTheme.colors.secondary,
            fontSize = 20.sp,
            fontWeight = FontWeight.W600
        )

        Divider(
            modifier = Modifier.height(1.dp), color = MaterialTheme.colors.primary
        )

        CategoryDropDown(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategoryChange = { category ->
                // Update the selected category in the ViewModel
                sharedViewModel.categoryId.value = category.id
            },
            onAddCategory = { categoryName ->
                // Delegate adding a new category to the ViewModel
                sharedViewModel.addCategory(categoryName)
            },
            onDeleteCategory = {
                categoryId ->
                // Delegate deleting a category to the ViewModel
                sharedViewModel.deleteCategory(categoryId)
            }
        )

        Divider(
            modifier = Modifier.height(25.dp), color = MaterialTheme.colors.primary
        )

        if (GlobalVariable.hasNotificationPermission) {
            CustomText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                text = stringResource(id = R.string.reminder_title),
                color = MaterialTheme.colors.secondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.W600
            )

            Divider(
                modifier = Modifier.height(16.dp), color = MaterialTheme.colors.primary
            )

            DateAndTimerPicker(
                reminderDateTime = reminderDateTime,
                onReminderDateTimeChange = onReminderDateTimeChange
            )

            Divider(
                modifier = Modifier.height(40.dp), color = MaterialTheme.colors.primary
            )
        }

        CustomText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 5.dp),
            text = stringResource(id = R.string.description),
            color = MaterialTheme.colors.secondary,
            fontSize = 20.sp,
            fontWeight = FontWeight.W600
        )

            contentItems.forEach { item ->
                when (item) {
                    is ContentItem.TextContent -> {
                        Text(
                            text = item.text,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            style = TextStyle(
                                color = MaterialTheme.colors.secondary,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.W400,
                                fontSize = 18.sp
                            )
                        )
                    }
                    is ContentItem.TranscriptContent -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TranscriptItem(
                                transcriptId = item.id,
                                SharedViewModel = sharedViewModel,
                                navigateToSpeech2TextScreen = navigateToSpeech2TextScreen,
                                onDelete = {
                                    // Implementation of the delete action using the sharedViewModel
                                    sharedViewModel.removeTranscript(item.id, selectedNote)
                                }

                            )

                        }
                    }
                    is ContentItem.PhotoContent -> {
                        var offsetX by remember { mutableStateOf(0f) }
                        var offsetY by remember { mutableStateOf(0f) }
                        var containerWidth by remember { mutableStateOf(0) }
                        val screenWidthDp = LocalConfiguration.current.screenWidthDp

                        // allows for image to be dragged around (not completed yet pls ignore)
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                                .draggable(
                                    orientation = Orientation.Horizontal,
                                    state = rememberDraggableState { delta ->
                                        offsetX += delta
                                        // Limit X-offset to within screen bounds
                                        offsetX = max(0f, min(offsetX, (screenWidthDp - containerWidth).toFloat()))                                },
                                    onDragStopped = {
                                        // Update the Y offset when dragging stops
                                        offsetY += it
                                    }
                                )
                                .onGloballyPositioned { layoutCoordinates ->
                                    // Get the width of the container when it is positioned
                                    containerWidth = layoutCoordinates.size.width
                                }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Using Coil for image loading
                                Image(
                                    painter = rememberAsyncImagePainter(model = item.uri),
                                    contentDescription = "Photo",
                                    modifier = Modifier.size(128.dp)
                                )
                                // for deleting image
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    modifier = Modifier
                                        .clickable {
                                            sharedViewModel.removePhoto(item.uri, selectedNote)
                                        }
                                        .padding(8.dp)
                                        .size(24.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }


        TextField(

            modifier = Modifier.fillMaxWidth(),
            value = sharedViewModel.textInput,
            onValueChange = { text ->
                sharedViewModel.textInput = text
                onDescriptionChange(text)

            },
            placeholder = {
                CustomText(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    text = stringResource(id = R.string.enter_description),
                    color = ChineseSilver,
                    fontWeight = FontWeight.W300,
                    fontSize = 18.sp
                )
            },
            textStyle = TextStyle(
                color = MaterialTheme.colors.secondary,
                fontFamily = fontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 18.sp
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = MaterialTheme.colors.secondary,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent
            )
        )
    }
}

@Composable
fun DateAndTimerPicker(
    reminderDateTime: Date?,
    onReminderDateTimeChange: (Date) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val mContext = LocalContext.current
    var selectedYear: Int = 0
    var selectedMonth: Int = 0
    var selectedDay: Int = 0
    var selectedHour: Int = 0
    var selectedMin: Int = 0

    // Instance to display today date in both date and time picker
    val today = Calendar.getInstance()

    // Creating a TimePicker dialog
    val mTimePickerDialog = TimePickerDialog(
        mContext, { _, hour: Int, minute: Int ->
            selectedHour = hour
            selectedMin = minute
            val calendar = Calendar.getInstance()
            calendar.set(
                selectedYear, selectedMonth, selectedDay, selectedHour, selectedMin
            )
            onReminderDateTimeChange(calendar.time)
        }, today[Calendar.HOUR_OF_DAY], today[Calendar.MINUTE], false
    )

    // Creating a DatePicker dialog
    val mDatePickerDialog = DatePickerDialog(
        mContext, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            selectedYear = year
            selectedMonth = month
            selectedDay = dayOfMonth
            mTimePickerDialog.show()
        }, today[Calendar.YEAR], today[Calendar.MONTH], today[Calendar.DAY_OF_MONTH]
    )

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp)
        .clickable(
            interactionSource = interactionSource, indication = null
        ) {
            mDatePickerDialog.show()
        }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_calendar),
            contentDescription = stringResource(id = R.string.calendar_icon),
            tint = MaterialTheme.colors.secondary
        )
        CustomText(
            modifier = Modifier
                .alpha(ContentAlpha.medium)
                .padding(start = 12.dp),
            text = if (dateToString(reminderDateTime).isNullOrEmpty()) stringResource(id = R.string.pick_date_time)
            else dateToString(reminderDateTime)!!,
            color = ChineseSilver,
            fontWeight = FontWeight.W300,
            fontSize = 18.sp
        )
    }
}

//@Composable
//@Preview
//fun NoteContentPreview() {
//    NoteContent(
//        title = "Title",
//        onTitleChange = {},
//        description = "Description",
//        onDescriptionChange = {},
//        priority = Priority.LOW,
//        onPriorityChange = {},
//        reminderDateTime = Date(),
//        onReminderDateTimeChange = {}
//                categoryId = 1
//    )
//}