package com.viapezeshk.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drugs")
data class Drug(
    @PrimaryKey val id: String,
    val name: String,
    val genericName: String,
    val category: String,
    val usage: String,
    val sideEffects: String,
    val warnings: String
)