package com.nrtxx.pade.ui.main

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nrtxx.pade.R
import com.nrtxx.pade.databinding.ActivityMainBinding
import com.nrtxx.pade.helper.uriToFile
import com.nrtxx.pade.ui.scan.DetailActivity
import com.nrtxx.pade.ui.scan.ScanActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }
    private var clicked = false

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        private const val REQUEST_CODE_PERMISSION = 10

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, "Tidak mendapatkan permission.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION)
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f

        binding.fabScan.setOnClickListener(this)
        binding.fabCamera.setOnClickListener(this)
        binding.fabGallery.setOnClickListener(this)
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.fabCamera.visibility = View.VISIBLE
            binding.fabGallery.visibility = View.VISIBLE
        } else {
            binding.fabCamera.visibility = View.INVISIBLE
            binding.fabGallery.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.fabCamera.startAnimation(fromBottom)
            binding.fabGallery.startAnimation(fromBottom)
            binding.fabScan.startAnimation(rotateOpen)
        } else {
            binding.fabCamera.startAnimation(toBottom)
            binding.fabGallery.startAnimation(toBottom)
            binding.fabScan.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            binding.fabCamera.isClickable = true
            binding.fabGallery.isClickable = true
        } else {
            binding.fabCamera.isClickable = false
            binding.fabGallery.isClickable = false
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fab_scan -> {
                setVisibility(clicked)
                setAnimation(clicked)
                setClickable(clicked)
                clicked = !clicked
            }
            R.id.fab_camera -> {
                val intent = Intent(this, ScanActivity::class.java)
                startActivity(intent)
            }
            R.id.fab_gallery -> {
                val intent = Intent()
                intent.action = ACTION_GET_CONTENT
                intent.type = "image/*"
                val chooser = Intent.createChooser(intent, "Choose a Picture")
                intentGallery.launch(chooser)

            }
        }
    }

    private val intentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@MainActivity)
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtra("file", myFile)
            DetailActivity.fromGallery = true
            startActivity(intent)
        }
    }
}