package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsListRepository

class RefreshAsteroidWork(context : Context, params : WorkerParameters) :
    CoroutineWorker(context, params){

    companion object{
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {

        val database = getDatabase(applicationContext)
        val repository = AsteroidsListRepository(database)

        return try {
            repository.refreshAsteroids()
            Result.success()

        } catch (e : Exception){
            Result.retry()
        }
    }//end doWork

}// end RefreshAsteroidWork