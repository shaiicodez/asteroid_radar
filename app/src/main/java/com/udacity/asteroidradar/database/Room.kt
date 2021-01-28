package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.*
import java.util.*


// for offline cache

// STEP 1:
@Dao
interface AsteroidDao{
    @Query("select * from DatabaseAsteroid where closeApproachDate between :fromDate and :toDate order by closeApproachDate asc")
    fun getAsteroids(fromDate: Date, toDate: Date): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAsteroids(vararg asteroids: DatabaseAsteroid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfDay(pictureOfDay: DatabasePictureOfDay)

    @Query("select * from picture_of_day limit 1")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay>
}

// STEP 2:
@Database(entities = [DatabaseAsteroid::class, DatabasePictureOfDay::class], version = 1)
@TypeConverters(Converters::class)
abstract class AsteroidsDatabase: RoomDatabase(){
    abstract val asteroidDao: AsteroidDao
}


// STEP 3:
@Volatile
private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase{
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids").build()
        }
    }
    return INSTANCE
}

