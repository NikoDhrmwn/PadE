package com.nrtxx.pade.ui.main

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.nrtxx.pade.R
import com.nrtxx.pade.api.ApiConfig
import com.nrtxx.pade.databinding.FragmentHomeBinding
import com.nrtxx.pade.helper.WeatherItem
import com.nrtxx.pade.helper.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var lat: Double? = null
    private var lon: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateNow = Calendar.getInstance().time
        val hariIni = DateFormat.format("EEE", dateNow) as String

        getToday(hariIni)
        getLatlon()
    }

    private fun getToday(hariIni: String) {
        val date = Calendar.getInstance().time
        val tanggal = DateFormat.format("d MMM yyyy", date) as String
        val formatDate = "$hariIni, $tanggal"
        binding.tvDate.text = formatDate
    }

    @SuppressLint("MissingPermission")
    private fun getLatlon() {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = locationManager.getBestProvider(criteria, true)
        val location = locationManager.getLastKnownLocation(provider.toString())
        if (location != null) {
            lat = location.latitude
            lon = location.longitude
            getCurrentWeather()
            setWeather()
        } else {
            //locationManager.requestLocationUpdates(provider, 20000, 0f, this)
        }
    }

    private fun getCurrentWeather() {
        val client = ApiConfig.getApiServiceCuaca().getWeather(lat, lon, "f7a716c616d63ead152a0f4d621d6e6c")
        client.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    binding.tvTemperature.text = String.format(Locale.getDefault(), "%.0f°C", responseBody.main.temp)
                    //binding.tvKecepatanAngin.text = resources.getString(R.string.kecepatan_angin, responseBody.wind.speed)
                   // binding.tvKelembaban.text = resources.getString(R.string.kelembaban, responseBody.main.humidity)
                } else {
                    Log.e("HomeFragment", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("HomeFragment", "onFailure: ${t.message}")
            }
        })
    }

    private fun setWeather() {
        val client = ApiConfig.getApiServiceCuaca().getWeather2(lat, lon, "f7a716c616d63ead152a0f4d621d6e6c")
        client.enqueue(object : Callback<WeatherItem> {
            override fun onResponse(
                call: Call<WeatherItem>,
                response: Response<WeatherItem>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    binding.tvWeather.text = responseBody.description
                } else {
                    Log.e("HomeFragment", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherItem>, t: Throwable) {
                Log.e("HomeFragment", "onFailure: ${t.message}")
            }
        })
    }

//    private fun checkPermission(permission: String): Boolean {
//        return ContextCompat.checkSelfPermission(
//            this,
//            permission
//        ) == PackageManager.PERMISSION_GRANTED
//    }
}