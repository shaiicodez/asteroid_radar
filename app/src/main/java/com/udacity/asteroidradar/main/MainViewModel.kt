package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch



class MainViewModel(application: Application) : AndroidViewModel(application) {
    enum class PeriodDays {ONE, SEVEN}
    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)


    init{
        refreshData()
    }

    fun refreshData(){
        viewModelScope.launch {
            asteroidsRepository.apply {
                refreshAsteroids()
                refreshPictureOfTheDay()
            }
        }
    }


    val daysIncluded = MutableLiveData(PeriodDays.SEVEN)

    val asteroidsList =
        Transformations.map(daysIncluded) { days ->
            days?.let {
                when (days) {
                    PeriodDays.ONE   -> asteroidsRepository.asteroidsListToday
                    PeriodDays.SEVEN -> asteroidsRepository.asteroidsListUpToEndDate
                }
            }
        }

    val pictureOfDay = asteroidsRepository.pictureOfDay

    val networkStatus =  asteroidsRepository.status
    val txStatus =  asteroidsRepository.txStatus


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