package com.nrtxx.pade.ui.scan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.nrtxx.pade.api.ApiConfig
import com.nrtxx.pade.databinding.ActivityDetailBinding
import com.nrtxx.pade.db.History
import com.nrtxx.pade.helper.Fields
import com.nrtxx.pade.helper.PenyakitResponse
import com.nrtxx.pade.helper.getDate
import com.nrtxx.pade.helper.rotateBitmap
import com.nrtxx.pade.ml.PadeModel
import com.nrtxx.pade.ui.history.HistoryViewModel
import org.tensorflow.lite.support.image.TensorImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    companion object {
        private const val TAG = "DetailActivity"
        var fromCamera = false
        var fromGallery = false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (fromCamera) {
            val myPicture = intent.getSerializableExtra("picture") as File
            var result = rotateBitmap(BitmapFactory.decodeFile(myPicture.path))
            binding.imgPreview.setImageBitmap(result)
            result = Bitmap.createScaledBitmap(result, 32, 32, true)
            identifyImage(result)
        } else if (fromGallery) {
            val myFile = intent.getSerializableExtra("file") as File
            var result = rotateBitmap(BitmapFactory.decodeFile(myFile.path))
            binding.imgPreview.setImageBitmap(result)
            result = Bitmap.createScaledBitmap(result, 32, 32, true)
            identifyImage(result)
        }

    }

    private fun getDetail(resultIdentify: String, image: Bitmap) {
        val client = ApiConfig.getApiService().getPenyakit(resultIdentify)
        client.enqueue(object : Callback<PenyakitResponse> {
            override fun onResponse(
                call: Call<PenyakitResponse>,
                response: Response<PenyakitResponse>
            ) {
                showLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    if (resultIdentify == "ST") {
                        setDetailSehat(responseBody.fields, image)
                    } else {
                        setDetail(responseBody.fields, image)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PenyakitResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun setDetail(fields: Fields, image: Bitmap) {
        binding.tvNameDetail.text = fields.nama.stringValue
        binding.tvGejalaDetail.text = fields.gejala.stringValue
        binding.tvPenyebabDetail.text = fields.penyebab.stringValue
        binding.tvInfoDetail.text = fields.info.stringValue
        binding.tvHADDetail.text = fields.HAD.stringValue
        binding.tvHAVDetail.text = fields.HAV.stringValue

        val viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        val history = History()
        history.let {
            it.penyakit = fields.nama.stringValue
            it.date = getDate()
            it.image = image
        }
        viewModel.insertHistory(history)
    }

    private fun setDetailSehat(fields: Fields, image: Bitmap) {
        binding.tvNameDetail.text = fields.nama.stringValue
        binding.tvGejalaDetail.text = null
        binding.tvPenyebabDetail.text = null
        binding.tvInfoDetail.text = null
        binding.tvHADDetail.text = null
        binding.tvHAVDetail.text = null

        val viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        val history = History()
        history.let {
            it.penyakit = fields.nama.stringValue
            it.date = getDate()
            it.image = image
        }
        viewModel.insertHistory(history)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun identifyImage(bitmap: Bitmap) {
        showLoading(true)
        val model = PadeModel.newInstance(this)

        // Creates inputs for reference.
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val tImage = TensorImage.fromBitmap(newBitmap)

        // Runs model inference and gets result.
        val outputs = model.process(tImage)
            .probabilityAsCategoryList.apply {
                sortByDescending { it.score }
            }
        val probability = outputs[0]

        when (probability.label) {
            "Bacterial leaf blight" -> {
                getDetail("HD", bitmap)
            }
            "Leaf smut" -> {
                getDetail("BD", bitmap)
            }
            "Brown spot" -> {
                getDetail("BDC", bitmap)
            }
            "Healthy" -> {
                getDetail("ST", bitmap)
            }
        }

        // Releases model resources if no longer used.
        model.close()
    }
}