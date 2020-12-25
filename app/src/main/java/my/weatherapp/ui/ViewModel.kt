package my.weatherapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import my.weatherapp.core.model.current.CurrentWeather
import my.weatherapp.core.model.daily.DailyData
import my.weatherapp.core.network.Client
import my.weatherapp.core.network.MainService
import my.weatherapp.core.utils.appid
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewModel : ViewModel() {


    private val service = Client.getRetrofit().create(MainService::class.java)




    private var mutableLiveCurrentWeather: MutableLiveData<CurrentWeather> = MutableLiveData()
    private var mutableLiveDailyData: MutableLiveData<DailyData> = MutableLiveData()
    private var mutableLiveErrorMessage: MutableLiveData<String> = MutableLiveData()
    private var lat: String? = null
    private var lon: String? = null


    fun loadData(lat: String, lon: String) {
        this.lat = lat
        this.lon = lon

        service.getCurrentWeather(lat, lon, appid).enqueue(object :Callback<CurrentWeather>{
            override fun onResponse(
                call: Call<CurrentWeather>,
                response: Response<CurrentWeather>
            ) {
                Log.d("mylog", response.toString())
                if (response.isSuccessful){
                    mutableLiveCurrentWeather.value=response.body()
                }
            }

            override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                mutableLiveErrorMessage.value=t.localizedMessage
            }
        })

        service.getDailyWeekly(lat, lon,"daily,weekly",appid).enqueue(object :Callback<DailyData>{
            override fun onResponse(call: Call<DailyData>, response: Response<DailyData>) {
                Log.d("mylog", response.body().toString())
                if (response.isSuccessful&&response.body()!=null){
                    mutableLiveDailyData.value=response.body()
                }
            }

            override fun onFailure(call: Call<DailyData>, t: Throwable) {
                mutableLiveErrorMessage.value=t.localizedMessage
            }
        })
    }


    fun getCurrentWeather(): LiveData<CurrentWeather>{
        return mutableLiveCurrentWeather
    }

    fun getDailyData(): LiveData<DailyData> =mutableLiveDailyData

    fun getError(): LiveData<String> = mutableLiveErrorMessage

}