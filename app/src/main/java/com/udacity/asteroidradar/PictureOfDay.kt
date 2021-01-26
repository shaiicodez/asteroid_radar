package com.udacity.asteroidradar

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class PictureOfDay(@Json(name = "media_type") val mediaType: String,
                        val title: String,
                        val url: String)

/**
 * ROOM LEVEL PictureOfDay data class/Room Entity - table
 */
@Entity(tableName = "picture_of_day")
data class DatabasePictureOfDay(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "media_type")    val mediaType: String,
    val title: String,
    val url: String)


/**
 * RETROFIT LEVEL PictureOfDay data class
 */
data class NetworkPictureOfDay(
    @Json(name = "media_type")  val mediaType: String,
    val title: String,
    val url: String)

/**
 * Network Object
 */
fun NetworkPictureOfDay.asDatabaseModel(): DatabasePictureOfDay{
    return  DatabasePictureOfDay(
        id = 0L,                        // OnConflictStrategy.REPLACE -> always overwrite as the ID is the same
        mediaType = this.mediaType,
        title = this.title,
        url = this.url
    )
}

/**
 * Domain Object
 */
fun DatabasePictureOfDay.asDomainModel(): PictureOfDay{
    return  PictureOfDay(
        mediaType = this.mediaType,
        title = this.title,
        url = this.url
    )
}