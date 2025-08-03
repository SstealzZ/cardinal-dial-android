package com.cardinaldial.app

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity to request battery optimization exemption
 * This is crucial for the call service to work when the device is in Doze mode
 */
class BatteryOptimizationActivity : AppCompatActivity() {
    
    companion object {
        private const val REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 1000
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestBatteryOptimizationExemption()
    }
    
    /**
     * Requests the user to disable battery optimization for this app
     */
    @SuppressLint("BatteryLife")
    private fun requestBatteryOptimizationExemption() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            } catch (e: Exception) {
                // Fallback to general battery optimization settings
                try {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    startActivity(intent)
                    Toast.makeText(
                        this,
                        "Veuillez désactiver l'optimisation de batterie pour Cardinal Dial",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e2: Exception) {
                    Toast.makeText(
                        this,
                        "Impossible d'ouvrir les paramètres d'optimisation de batterie",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
            }
        } else {
            // Battery optimization not available on older versions
            finish()
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(
                    this,
                    "Optimisation de batterie désactivée. L'application fonctionnera mieux en arrière-plan.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "L'optimisation de batterie est toujours active. Cela peut affecter le fonctionnement de l'application.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        finish()
    }
}