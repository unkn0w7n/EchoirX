package app.echoirx.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
    val type: String,
    val timestamp: Long = System.currentTimeMillis()
)