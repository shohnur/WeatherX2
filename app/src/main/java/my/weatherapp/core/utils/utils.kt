package my.weatherapp.core.utils

import android.view.View

const val appid = "7e681e65ffdb1b82b79c2b758557e255"

const val baseUrl = "https://api.openweathermap.org/data/2.5/"

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}