package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.NasaApiStatus
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.getToday
import com.udacity.asteroidradar.database.getWeek
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

// fetch the data from the negtwrok and store them on local disk
class AsteroidsListRepository(private val database : AsteroidsDatabase) {

    //status
    var status = MutableLiveData<NasaApiStatus>(NasaApiStatus.LOADING)
    val txtStatus = MutableLiveData<String>()

    //receive picture of the day
    val pictureOfDay : LiveData<PictureOfDay> =
        Transformations.map(database.asteroidDao.getPictureOfDay()){
            it?.asDomainModel()
        }

    //receive only today list
    val asteroidListToday: LiveData<List<Asteroid>> =
        Transformations.map(database.getToday()){ todayList ->
            todayList?.asDomainModel()
        }

    //receive list up to 7 days (week)
    val asteroidsListWeek : LiveData<List<Asteroid>> =
        Transformations.map(database.getWeek()){ weekList ->
            weekList?.asDomainModel()
        }

    //fetxh data and refresh
    suspend fun refreshAsteroids(){
        //on main thread load the data
        withContext(Dispatchers.Main){
            try {
                status.value = NasaApiStatus.LOADING

                //get the data from the network api
                val stringResponse = NasaApi.retrofitService.getAsteroidList(
                    getPeriodTimeZone(0, Constants.API_QUERY_DATE_FORMAT),
                    getPeriodTimeZone(Constants.DEFAULT_END_DATE_DAYS, Constants.API_QUERY_DATE_FORMAT),
                    Constants.API_KEY)

                //convert json string to object
                val jsonObject = JSONObject(stringResponse)

                // object to array format
                val arrayListNetworkAsteroids = parseAsteroidsJsonResult(jsonObject)

                //convert it to container shape
                val asteroidList = NetworkAsteroidContainer(arrayListNetworkAsteroids)

                //push the list to db
                withContext(Dispatchers.IO){
                    database.asteroidDao.insertAllAsteroids(*asteroidList.asDatabaseModel())
                }

                status.value = NasaApiStatus.DONE
            } catch (e: Exception){
                status.value = NasaApiStatus.ERROR
            }
        }

    }// end refreshAsteroids

    //fetch pod and save it
    suspend fun refreshPictureOfTheDay(){
        withContext(Dispatchers.IO){
            try {
                //get the pod from server
                val pod = NasaApi.retrofitService.getPictureOfDay(Constants.API_KEY)

                //push to db only image media
                if (pod.mediaType == "image")
                    database.asteroidDao.insertPictureOfDay(pod.asDatabaseModel())

                status.value = NasaApiStatus.DONE
            } catch (e : java.lang.Exception){
                // network error
            }
        }
    }

}//end AsteroidsListRepository