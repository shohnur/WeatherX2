package my.weatherapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import my.weatherapp.R
import my.weatherapp.core.adapter.RVAdapter
import my.weatherapp.core.db.Database
import my.weatherapp.core.model.Data
import my.weatherapp.core.model.current.CurrentWeather
import my.weatherapp.core.model.daily.DailyData
import my.weatherapp.core.utils.hide
import my.weatherapp.core.utils.show
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ViewModel
    var lat: String? = null
    var lon: String? = null
    var locationGPS: Location? = null
    var locationManager: LocationManager? = null
    var adapter: RVAdapter = RVAdapter(this)

    private var d1: String? = null
    private var d2: String? = null
    private var i = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        getLocationAccess()
        progressBar.show()
        getLocation()

        if (lat != null && lon != null) {
            loadData()
        } else {
            progressBar.hide()
        }

        refreshLayout.setOnRefreshListener {
            getLocation()
            progressBar.show()
            if (lat != null && lon != null) {
                loadData()
            } else {
                progressBar.hide()
            }
            refreshLayout.isRefreshing = false
        }


    }

    private fun loadData() {
        progressBar.show()
        viewModel.loadData(lat!!, lon!!)
        viewModel.getCurrentWeather().observe(this, { data ->

            Log.d("mylog", data.toString())

            if (data != null) {

                d1 = Gson().toJson(data).toString()

                city_name.text = String.format("%s, %s", data.name, data.sys?.country)

                val sdf = SimpleDateFormat("dd-MMM-yyyy")
                val currentDate = sdf.format(Date())
                date.text = String.format("%s", currentDate)

                curr_weather.text = data.weather!![0].main

                curr_temp.text = String.format("%s°C", (data.main?.temp!! - 273.15).toString())

                humidity.text = String.format("%s", data.main.humidity.toString())
                hint_h.show()
                curr_pressure.text =
                    String.format("%s PA", (data.main.pressure!! * 100).toString())
                hint_p.show()
                val sunset = data.sys!!.sunset!!.times(1000).toLong() + 10800000
                val sunrise = data.sys.sunrise!!.times(1000).toLong() + 10800000

                val ss = Date(sunset)
                val sr = Date(sunrise)
                val df = SimpleDateFormat("hh:mm")

                sunSet.text = String.format("%sPM", df.format(ss))
                sunRise.text = String.format("%sAM", df.format(sr))
                hint_sr.show()
                hint_ss.show()
            }

        })
        viewModel.getDailyData().observe(this, { data ->
            d2 = Gson().toJson(data).toString()
            change()
            rView.adapter = adapter
            adapter.data = data.daily!!
            adapter.notifyDataSetChanged()
            progressBar.hide()
        })
        viewModel.getError().observe(this, { s ->
            progressBar.hide()
            i++
            Log.e("MYerror", s)
            showLastData()
        })

    }

    private fun showLastData() {

        if (Database.getDB().dao().getData() != null && Database.getDB().dao().getData()!!
                .isNotEmpty()
        ) {
            val d = Database.getDB().dao().getData()
            showCurrentWeatherOffline(d!![0].current)
            showDailyData(d[0].daily)
        } else {
            if (i % 2 == 0)
                Toast.makeText(this, "Database is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDailyData(daily: String?) {
        val data = Gson().fromJson(daily, DailyData::class.java)
        rView.adapter = adapter
        adapter.data = data.daily!!
        adapter.notifyDataSetChanged()
        progressBar.hide()
    }

    private fun showCurrentWeatherOffline(current: String?) {
        val data = Gson().fromJson(current, CurrentWeather::class.java)
        city_name.text = String.format("%s, %s", data.name, data.sys?.country)

        val sdf = SimpleDateFormat("dd-MMM-yyyy")
        val currentDate = sdf.format(Date())
        date.text = String.format("%s", currentDate)
        curr_weather.text = data.weather!![0].main

        curr_temp.text = String.format("%s°C", (data.main?.temp!! - 273.15).toString())

        humidity.text = String.format("%s", data.main.humidity.toString())
        hint_h.show()
        curr_pressure.text =
            String.format("%s PA", (data.main.pressure!! * 100).toString())
        hint_p.show()
        val sunset = data.sys!!.sunset!!.times(1000).toLong() + 10800000
        val sunrise = data.sys.sunrise!!.times(1000).toLong() + 10800000

        val ss = Date(sunset)
        val sr = Date(sunrise)
        val df = SimpleDateFormat("hh:mm")

        sunSet.text = String.format("%sPM", df.format(ss))
        sunRise.text = String.format("%sAM", df.format(sr))
        hint_sr.show()
        hint_ss.show()
    }

    private fun change() {
        Database.getDB().dao().clear()
        Database.getDB().dao().insert(Data(0, d1, d2))
    }

    private fun getLocation() {

        if (locationGPS != null) {
            lat = locationGPS!!.latitude.toString()
            lon = locationGPS!!.longitude.toString()
        } else {
            Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show()
        }

        Log.d("mytag", "getLocation: ")


        /*try {
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        lon = location.longitude.toString()
                        lat = location.latitude.toString()
                        Log.d("mytag", "$lon $lat")
                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }
            )
        } catch (ex: SecurityException) {
            Log.d("mytag", "Security Exception, no location available")
        }*/

    }

    private fun getLocationAccess() {
        try {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        Log.d("mytag", "getLocationAccess: $locationManager")

        locationGPS =
            locationManager?.let {
                it.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

    }


}