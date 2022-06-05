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
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val imageSize = 32
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

        result = Bitmap.createScaledBitmap(result, imageSize, imageSize, true)

        identifyImage(result)

    }

    private fun getDetail(resultIdentify: String) {
        showLoading(true)
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

    private fun identifyImage(image: Bitmap) {
        val model = PadeModel.newInstance(this)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
        val tBuffer = TensorImage.fromBitmap(image)
        val byteBuffer = tBuffer.buffer

        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val max = getMax(outputFeature0.floatArray)

        val resultIdentify = outputFeature0.floatArray[max].toString()
        binding.tvNameDetail.text = resultIdentify
        getDetail(resultIdentify)
        // Releases model resources if no longer used.
        model.close()
    }

    private fun getMax(arr: FloatArray): Int {
        var ind = 0
        var min = 0.0f

        for (i in 0..1000) {
            if(arr[i] > min) {
                ind = i
                min = arr[i]
            }
        }
        return ind
    }
}