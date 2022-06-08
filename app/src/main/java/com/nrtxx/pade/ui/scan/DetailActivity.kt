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
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder


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
        val byteBuffer = ByteBuffer.allocateDirect(4 * 32 * 32 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = intArrayOf(32 * 32)
        image.getPixels(intValues, 0, 32, 0, 0, 32, 32)
        var pixel = 0
        for (i in 0..32) {
            for (j in 0..32) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16) and 0xFF) * (1f / 255))
                byteBuffer.putFloat(((value shr 32) and 0xFF) * (1f / 255))
                byteBuffer.putFloat((value and 0xFF) * (1f / 255))
            }
        }

        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val confidence = outputFeature0.floatArray
        var maxPos = 0
        var maxConfidence = 0.0f
        for (i in 0..confidence.size) {
            if (confidence[i] > maxConfidence) {
                maxConfidence = confidence[i]
                maxPos = i
            }
        }

        val classes = arrayOf("Bacterial leaf blight", "Leaf smut", "Brown spot", "Healthy")
        val resultIdentify = classes[maxPos]
        binding.tvNameDetail.text = resultIdentify

//        when (resultIdentify) {
//            "Bacterial leaf blight" -> {
//                getDetail("HD")
//            }
//            "Leaf smut" -> {
//                getDetail("BD")
//            }
//            "Brown spot" -> {
//                getDetail("BDC")
//            }
//            "Healthy" -> {
//                getDetail("ST")
//            }
//        }

        // Releases model resources if no longer used.
        model.close()
    }
}