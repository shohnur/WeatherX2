package my.weatherapp.core.network

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import my.weatherapp.core.utils.baseUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client {

    companion object {

        private var retrofit: Retrofit? = null

        fun getRetrofit(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build()
            }
            return retrofit!!
        }


    }

}