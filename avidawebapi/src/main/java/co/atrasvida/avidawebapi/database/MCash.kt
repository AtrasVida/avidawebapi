package co.atrasvida.avidawebapi.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Table for store tableName
 *
 * @property id primary key of local table
 */
@Entity(tableName = "m_cash")
data class MCash(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var token: String,
    var data_val: String?


)
