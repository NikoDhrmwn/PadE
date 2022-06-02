package com.nrtxx.pade.ui.scan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nrtxx.pade.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    companion object {
        const val CAMERA_RESULT = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore

        val docRef = db.collection("penyakit").document("BD")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("exist", "DocumentSnapshot data: ${document.data}")
                    binding.tvNameDetail.text = document.getString("Nama")
                    binding.tvPenyebabDetail.text = document.getString("Penyebab")
                    binding.tvGejalaDetail.text = document.getString("Gejala")
                    binding.tvInfoDetail.text = document.getString("Info")
                    binding.tvHADDetail.text = document.getString("HAD")
                    binding.tvHAVDetail.text = document.getString("HAV")
                } else {
                    Log.d("notExist", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("errorDB", "get failed with ", exception)
            }
    }

}