package com.viapezeshk.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diseases")
data class Disease(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val category: String,
    val content: String,
    val isBookmarked: Boolean = false // فیلد جدید
)