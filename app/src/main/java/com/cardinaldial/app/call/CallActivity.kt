package com.cardinaldial.app.call

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.cardinaldial.app.databinding.ActivityCallBinding

class CallActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCallBinding
    private val handler = Handler(Looper.getMainLooper())
    private var isCallActive = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if launched from background service
        val fromBackgroundService = intent.getBooleanExtra("from_background_service", false)
        Log.d("CallActivity", "Created - from background service: $fromBackgroundService")
        
        setupWindowFlags(fromBackgroundService)
        initializeBinding()
        setupCallInterface()
        setupButtonListeners()
        
        // If launched from background service, ensure visibility
        if (fromBackgroundService) {
            Log.d("CallActivity", "Ensuring activity visibility for background launch")
            
            // Force the window to be visible and interactive
            window.decorView.systemUiVisibility = (
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
            
            // Ensure the activity is brought to front
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    Log.d("CallActivity", "Attempting to bring window to front")
                    window.decorView.requestFocus()
                    window.decorView.bringToFront()
                } catch (e: Exception) {
                    Log.e("CallActivity", "Failed to bring window to front", e)
                }
            }, 200)
        }
        
        // Auto-close after 10 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 10000)
    }
    
    /**
     * Configures window flags to show the call interface over lock screen
     * and other applications using modern Android APIs
     */
    private fun setupWindowFlags(fromBackgroundService: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // Use modern methods for API 27+
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            
            // Request to dismiss keyguard if possible
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            // Fallback to deprecated flags for older versions
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        
        // Essential flags for showing over other apps and lock screen
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
        
        // Set window type based on launch context
        if (fromBackgroundService) {
            Log.d("CallActivity", "Setting up overlay window for background service launch")
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                } else {
                    @Suppress("DEPRECATION")
                    window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                }
                
                // Additional flags for overlay display
                window.addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                )
            } catch (e: Exception) {
                Log.e("CallActivity", "Failed to set overlay type", e)
            }
        } else {
            Log.d("CallActivity", "Normal activity launch - no overlay needed")
        }
    }
    
    /**
     * Initializes the view binding for the call activity
     */
    private fun initializeBinding() {
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    
    /**
     * Sets up the initial call interface with caller information
     */
    private fun setupCallInterface() {
        binding.callerName.text = "Cardinal Dial Test"
        binding.callerNumber.text = "+33 1 23 45 67 89"
        binding.callStatus.text = "Appel entrant..."
    }
    
    /**
     * Configures click listeners for answer and decline buttons
     */
    private fun setupButtonListeners() {
        binding.answerButton.setOnClickListener {
            answerCall()
        }
        
        binding.declineButton.setOnClickListener {
            declineCall()
        }
    }
    
    /**
     * Handles the answer call action
     * Simulates an active call for 5 seconds then ends automatically
     */
    private fun answerCall() {
        isCallActive = true
        updateCallInterface()
        
        handler.postDelayed({
            endCall()
        }, 5000)
    }
    
    /**
     * Handles the decline call action
     * Immediately ends the call
     */
    private fun declineCall() {
        endCall()
    }
    
    /**
     * Updates the interface to show active call state
     */
    private fun updateCallInterface() {
        binding.callStatus.text = "En cours..."
        binding.answerButton.isEnabled = false
        binding.answerButton.hide()
    }
    
    /**
     * Ends the call and closes the activity
     */
    private fun endCall() {
        stopService(Intent(this, CallService::class.java))
        finish()
    }
    
    override fun onBackPressed() {
        // Prevent back button from closing call interface
    }
}