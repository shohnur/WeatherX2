package my.weatherapp.core.model.current


import com.google.gson.annotations.SerializedName


data class Clouds(
    @SerializedName("all")
    val all: Int?
)