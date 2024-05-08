package com.example.aistudy.components.note

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aistudy.R
import com.example.aistudy.components.CustomText
import com.example.aistudy.ui.theme.BlackOlive
import com.example.aistudy.data.models.Category

/**
 * This composable function represents a dropdown menu for selecting a category when adding or editing a note.
 *
 * It allows the following:
 * - Displays a list of existing categories
 * - Provides an option to select "None" to indicate no category assignment
 * - Enables deleting existing categories
 * - Offers the ability to add a new category in a dialog box
 */
@Composable
fun CategoryDropDown(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategoryChange: (Category) -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (Int) -> Unit
) {

    var expanded by remember { mutableStateOf(false) } // State for managing dropdown expansion
    var showAddCategoryDialog by remember { mutableStateOf(false) } // State to control the visibility of the dialog
    var selectedCategoryName by remember { mutableStateOf(selectedCategory?.name ?: "Select Category") } // State for managing selected category's name

    // Use LaunchedEffect to watch for changes in selectedCategory
    LaunchedEffect(selectedCategory) {
        // Update selectedCategoryName when selectedCategory changes
        selectedCategoryName = selectedCategory?.name ?: "Select Category"
    }

    // Animation state for rotating the dropdown arrow
    val angle: Float by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    // Main row for the dropdown menu
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }, // Expands the dropdown menu on click
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Text showing the selected category
        CustomText(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 16.dp),
            text = "${selectedCategoryName ?: "Select Category"}",
            color = MaterialTheme.colors.secondary,
            fontWeight = FontWeight.W400,
            fontSize = 18.sp
        )

        // Dropdown arrow icon, rotates based on expansion state
        IconButton(
            modifier = Modifier
                .alpha(ContentAlpha.medium)
                .rotate(degrees = angle),
            onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = stringResource(id = R.string.down_arrow),
                tint = MaterialTheme.colors.secondary
            )
        }

        // The actual dropdown menu
        DropdownMenu(
            modifier = Modifier
                .background(BlackOlive)
                .fillMaxWidth(fraction = 0.92f),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {

            // Option for no category selection
            DropdownMenuItem(onClick = {
                selectedCategoryName = Category.NO_CATEGORY.name // Placeholder text for no selection
                onCategoryChange(Category.NO_CATEGORY) // Pass null to indicate no category selected
                expanded = false
            }) {
                Text("None", modifier = Modifier.fillMaxWidth())
            }

            // List of categories for selection
            categories.forEach{ category ->
                DropdownMenuItem(onClick = {
                    selectedCategoryName = category.name
                    onCategoryChange(category)
                    expanded = false
                }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomText(
                            text = category.name,
                            color = MaterialTheme.colors.secondary,
                            fontWeight = FontWeight.W400,
                            fontSize = 18.sp
                        )
                        IconButton(
                            onClick = { onDeleteCategory(category.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Category",
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                }
            }
            Divider()
            // Option to add a new category
            DropdownMenuItem(onClick = {
                expanded = false
                showAddCategoryDialog = true // Show the add category dialog
            }) {
                CustomText(
                    "Add new category",
                    color = MaterialTheme.colors.secondary,
                    fontWeight = FontWeight.W400,
                    fontSize = 18.sp
                )
            }
        }
    }

    // Dialog for adding a new category
    if (showAddCategoryDialog) {
        var newCategoryName by remember { mutableStateOf("") }

        AlertDialog(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = Color.White,
            onDismissRequest = { showAddCategoryDialog = false },
            text = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Text field for entering new category name
                        TextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            label = { Text("Category Name") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f) // Use weight to make TextField flexible in size
                                .padding(end = 8.dp) // Add padding to separate TextField and Button
                        )

                        // Button for adding the new category
                        IconButton(
                            onClick = {
                                showAddCategoryDialog = false
                                onAddCategory(newCategoryName)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add",
                                tint = Color.Green
                            )
                        }
                    }
                }
            },
            buttons = { }
        )

    }
}
