package my.weatherapp.core.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_rv.view.*
import my.weatherapp.R
import my.weatherapp.core.model.daily.Daily
import java.text.SimpleDateFormat
import java.util.*

class RVAdapter(var context: Context) : RecyclerView.Adapter<RVAdapter.DataViewHolder>() {


    var data = listOf<Daily>()

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(d: Daily) {
            itemView.max_temp.text = String.format("%.1f°C", d.temp!!.day!! - 273.15)
            itemView.min_temp.text = String.format("%.1f°C", d.temp.night!! - 273.15)
            val dt = Date((d.dt!!.toLong().times(1000)))
            val sdf = SimpleDateFormat("dd-MMM-yyyy")
            itemView._date.text = sdf.format(dt)

            val s = d.weather!![0].icon
            Glide.with(context).load("http://openweathermap.org/img/wn/${s}@2x.png")
                .into(itemView._image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bindData(data[position])

    override fun getItemCount(): Int = data.size


}