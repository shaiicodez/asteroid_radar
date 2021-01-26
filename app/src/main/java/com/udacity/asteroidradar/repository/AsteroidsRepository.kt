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
import com.udacity.asteroidradar.database.getUpToEndDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Repository for fwetching the datat from the network and storing them on local disk
 * **/
class AsteroidsRepository(private val database: AsteroidsDatabase) {


//    val asteroidsListSaved: LiveData<List<Asteroid>> =
//        Transformations.map(database.asteroidDao.getAsteroids()){
//            it?.asDomainModel()
//        }

    val asteroidsListUpToEndDate: LiveData<List<Asteroid>> =
        Transformations.map(database.getUpToEndDate()){
            it?.asDomainModel()
        }

    val asteroidsListToday: LiveData<List<Asteroid>> =
        Transformations.map(database.getToday()){
            it?.asDomainModel()
        }

    var status = MutableLiveData<NasaApiStatus>(NasaApiStatus.LOADING)

    val txStatus = MutableLiveData<String>()

    private val fromDate = getDaysFromNowStr(0, Constants.API_QUERY_DATE_FORMAT)
    private val toDate = getDaysFromNowStr(Constants.DEFAULT_END_DATE_DAYS, Constants.API_QUERY_DATE_FORMAT)

    /**
     * Fetch Data based on period - DEFAULT_END_DATE_DAYS
     */
    suspend fun refreshAsteroids(){
        withContext(Dispatchers.Main){
            try {
                status.value = NasaApiStatus.LOADING

                // Get String json response via retrofit with scalars converter
                val stringResponse = NasaApi.retrofitService.getAsteroidList(fromDate, toDate, Constants.API_KEY)

                // Transform the string response to json object
                val jsonObject = JSONObject(stringResponse)

                // Get an ArrayList<NetworkAsteroids> from the JSONObject
                val arrayListOfNetworkAsteroids = parseAsteroidsJsonResult(jsonObject)

                // set at the asteroidsList dataclass the ArrayList<NetworkAsteroids> received
                val asteroidsList = NetworkAsteroidContainer(arrayListOfNetworkAsteroids)

                // push the fetched results to the database
                withContext(Dispatchers.IO) {
                    database.asteroidDao.insertAllAsteroids(*asteroidsList.asDatabaseModel())
                }
                status.value = NasaApiStatus.DONE
            }
            catch (e: Exception){
                // no internet
                status.value = NasaApiStatus.ERROR
            }
        }
    }


    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(database.asteroidDao.getPictureOfDay()){
            it?.asDomainModel()
        }

    suspend fun refreshPictureOfTheDay(){
        withContext(Dispatchers.IO){
            try {
                status.value = NasaApiStatus.LOADING
                // Get the picture of the day via moshi converter
                val networkPictureOfDay = NasaApi.retrofitService.getPictureOfDay(Constants.API_KEY)

                // push the fetched results to the database
                if (networkPictureOfDay.mediaType == "image")
                    database.asteroidDao.insertPictureOfDay(networkPictureOfDay.asDatabaseModel())
                status.value = NasaApiStatus.DONE
            }
            catch (e: Exception){
                // Network Error (no internet)
                //status.value = NasaApiStatus.ERROR
            }
        }
    }
}//end AsteroidsRepository