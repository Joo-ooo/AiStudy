package com.example.aistudy.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aistudy.utils.Constants.CAT_TABLE

/**
 * This data class represents a single category and is used for Room database persistence.
 * A new row is added when a new category is added in any note.
 */
@Entity(tableName = CAT_TABLE)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
) {
    companion object {
        val NO_CATEGORY = Category(id = -1, name = "None")
    }
}
