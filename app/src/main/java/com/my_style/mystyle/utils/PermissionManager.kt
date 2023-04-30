package com.my_style.mystyle.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.my_style.mystyle.R
import com.my_style.mystyle.databinding.LayoutPermissionBinding
import com.my_style.mystyle.view.activity.MainActivity
import kotlin.system.exitProcess

class PermissionManager : Activity() {
    private val permissionRequestCode = 441
    private lateinit var binding: LayoutPermissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermission()
    }

    private fun requestPermission() {
        if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    permissionRequestCode
                )
            else requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
                ),
                permissionRequestCode
            )
        } else {
            startActivity(Intent(this@PermissionManager, MainActivity::class.java))
            finish()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            permissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) && shouldShowRequestPermissionRationale(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        AlertDialog.Builder(this)
                            .setMessage(getString(R.string.massage_for_permission))
                            .setPositiveButton("OK") { _, _ ->
                                requestPermissions(
                                    arrayOf(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_PHONE_STATE
                                    ),
                                    permissionRequestCode
                                )
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    } else {
                        ifPermissionDenied()
                    }

                }
            }
        }
    }

    private fun ifPermissionDenied() {
        binding.txtPermissionMassage.visibility = View.VISIBLE
        binding.btnExitApp.visibility = View.VISIBLE
        binding.btnPermission.visibility = View.VISIBLE
        binding.txtPermissionMassage.text = getString(R.string.massage_for_permission)
        binding.btnExitApp.setOnClickListener {
            finishAffinity()
            exitProcess(0)
        }
        binding.btnPermission.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", packageName, null)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Thank you. For giving permission", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@PermissionManager, MainActivity::class.java))
            finish()
        }

    }

}
