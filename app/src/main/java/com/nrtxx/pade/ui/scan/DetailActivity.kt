package com.nrtxx.pade.ui.scan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.nrtxx.pade.api.ApiConfig
import com.nrtxx.pade.databinding.ActivityDetailBinding
import com.nrtxx.pade.helper.Fields
import com.nrtxx.pade.helper.PenyakitResponse
import com.nrtxx.pade.helper.rotateBitmap
import com.nrtxx.pade.ml.PadeModel
import org.tensorflow.lite.support.image.TensorImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    companion object {
        private const val TAG = "DetailActivity"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myPicture = intent.getSerializableExtra("picture") as File
        var result = rotateBitmap(BitmapFactory.decodeFile(myPicture.path))
        binding.imgPreview.setImageBitmap(result)

        result = Bitmap.createScaledBitmap(result, 32, 32, true)

        identifyImage(result)

    }

    private fun getDetail(resultIdentify: String) {
        val client = ApiConfig.getApiService().getPenyakit(resultIdentify)
        client.enqueue(object : Callback<PenyakitResponse> {
            override fun onResponse(
                call: Call<PenyakitResponse>,
                response: Response<PenyakitResponse>
            ) {
                showLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    setDetail(responseBody.fields)
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

    private fun setDetail(fields: Fields) {
        binding.tvGejalaDetail.text = fields.gejala.stringValue
        binding.tvPenyebabDetail.text = fields.penyebab.stringValue
        binding.tvInfoDetail.text = fields.info.stringValue
        binding.tvHADDetail.text = fields.HAD.stringValue
        binding.tvHAVDetail.text = fields.HAV.stringValue
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
        binding.tvNameDetail.text = probability.label

        when (probability.label) {
            "Bacterial leaf blight" -> {
                getDetail("HD")
            }
            "Leaf smut" -> {
                getDetail("BD")
            }
            "Brown spot" -> {
                getDetail("BDC")
            }
            "Healthy" -> {
                getDetail("ST")
            }
        }

        // Releases model resources if no longer used.
        model.close()
    }
}