package com.cardinaldial.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cardinaldial.app.call.CallService
import com.cardinaldial.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupClickListener()
        checkSystemAlertWindowPermission()
        checkBatteryOptimizationOnStartup()
    }
    
    /**
     * Sets up the click listeners for the buttons
     */
    private fun setupClickListener() {
        binding.mainButton.setOnClickListener {
            showWelcomeMessage()
        }
        
        binding.mainButton.setOnLongClickListener {
            openBatteryOptimizationSettings()
            true
        }
    }
    
    /**
     * Checks and requests SYSTEM_ALERT_WINDOW permission if needed
     * This permission is required to show call interface over other apps
     */
    private fun checkSystemAlertWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Log.d("MainActivity", "SYSTEM_ALERT_WINDOW permission not granted")
                Toast.makeText(this, "Permission requise pour afficher les appels", Toast.LENGTH_LONG).show()
                
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            } else {
                Log.d("MainActivity", "SYSTEM_ALERT_WINDOW permission already granted")
            }
        }
    }
    
    /**
     * Initiates a simulated call with delay
     * Shows confirmation message and starts the call service
     */
    private fun showWelcomeMessage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Permission d'affichage requise - voir paramètres", Toast.LENGTH_LONG).show()
            return
        }
        
        Log.d("MainActivity", "showWelcomeMessage called")
        Toast.makeText(this, "Appel en cours de préparation...", Toast.LENGTH_SHORT).show()
        CallService.startCallWithDelay(this)
    }
    
    /**
     * Checks battery optimization status on app startup
     * Automatically requests exemption if not already granted
     */
    private fun checkBatteryOptimizationOnStartup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val packageName = packageName
            
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                Toast.makeText(
                    this, 
                    "Configuration de l'optimisation batterie requise", 
                    Toast.LENGTH_LONG
                ).show()
                openBatteryOptimizationSettings()
            }
        }
    }
    
    /**
     * Opens battery optimization settings
     */
    private fun openBatteryOptimizationSettings() {
        val intent = Intent(this, BatteryOptimizationActivity::class.java)
        startActivity(intent)
    }
}