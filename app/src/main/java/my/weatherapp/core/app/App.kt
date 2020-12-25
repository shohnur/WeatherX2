package my.weatherapp.core.app

import android.app.Application
import my.weatherapp.core.db.Database

class App : Application() {


    override fun onCreate() {
        super.onCreate()
        Database.init(this)
    }
}