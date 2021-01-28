package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsListRepository
import kotlinx.coroutines.launch



class MainViewModel(application: Application) : AndroidViewModel(application) {

    enum class TimeZone {ONE, SEVEN} //today or a week period filteration
    val period = MutableLiveData(TimeZone.SEVEN)
    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsListRepository(database)
    val pictureOfDay = asteroidsRepository.pictureOfDay
    val networkStatus =  asteroidsRepository.status
    val txStatus =  asteroidsRepository.txtStatus


    init{
        viewModelScope.launch {
            asteroidsRepository.apply {
                refreshAsteroids()
                refreshPictureOfTheDay()
            }
        }
    }

    val asteroidsList =
        Transformations.map(period) { period ->
            period?.let {
                when (period) {
                    TimeZone.ONE   -> asteroidsRepository.asteroidListToday
                    TimeZone.SEVEN -> asteroidsRepository.asteroidsListWeek
                }
            }
        }


    // Navigation Controls
    // navigate variables
    private val _navigateToAsteroidDetail = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetail
        get() = _navigateToAsteroidDetail

    // when item is clicked, pass/save its value
    fun onAsteroidItemClicked(asteroid: Asteroid) {
        _navigateToAsteroidDetail.value = asteroid
    }

    // when navigation is done, value = null
    fun onSleepDataQualityNavigated() {
        _navigateToAsteroidDetail.value = null
    }
}