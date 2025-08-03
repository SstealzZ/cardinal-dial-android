package com.cardinaldial.app.call


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cardinaldial.app.MainActivity


/**
 * Foreground service that manages call simulation
 * Handles call triggering even when app is not in foreground and device is in Doze mode
 */
class CallService : Service() {

    companion object {
        private const val TAG = "CallService"
        private const val CALL_DELAY_MS = 7000L
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "call_service_channel"

        /**
         * Starts the call service with delay
         * @param context Application context
         */
        fun startCallWithDelay(context: Context) {
            val intent = Intent(context, CallService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    private var handler: Handler? = null
    private var callRunnable: Runnable? = null
    private var wakeLock: PowerManager.WakeLock? = null

    /**
     * Called when service is started
     * Schedules the call interface with basic service approach
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "CallService started")
        
        try {
            // Start as foreground service for persistence when app is closed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
                startForeground(NOTIFICATION_ID, createNotification())
                Log.d(TAG, "Foreground service started with notification")
            }
            
            acquireWakeLock()
            scheduleCallInterface()
            return START_STICKY
        } catch (e: Exception) {
            Log.e(TAG, "Error in onStartCommand", e)
            return START_NOT_STICKY
        }
    }

    /**
     * Schedules the call interface to appear after delay
     */
    private fun scheduleCallInterface() {
        handler = Handler(Looper.getMainLooper())
        callRunnable = Runnable {
            triggerCallInterface()
        }
        handler?.postDelayed(callRunnable!!, CALL_DELAY_MS)
        Log.d(TAG, "Call interface scheduled for ${CALL_DELAY_MS}ms")
    }

    /**
     * Triggers the call interface by starting CallActivity
     * Uses appropriate flags to show over lock screen and turn on screen
     * Service continues running in background after triggering call
     */
    private fun triggerCallInterface() {
        try {
            Log.d(TAG, "Triggering call interface")
            val callIntent = Intent(this, CallActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
                        Intent.FLAG_ACTIVITY_NO_ANIMATION
                
                // Add specific flags for showing over other apps
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                }
                
                // Add extra data to indicate this is from background service
                putExtra("from_background_service", true)
            }
            
            Log.d(TAG, "Starting CallActivity with flags: ${callIntent.flags}")
            startActivity(callIntent)
            
            Log.d(TAG, "CallActivity started successfully - service continues running")
        } catch (e: Exception) {
            Log.e(TAG, "Error triggering call interface", e)
        }
    }


    
    /**
     * Acquires partial wake lock to prevent Doze mode interference
     */
    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "CallService::WakeLock"
        )
        wakeLock?.acquire(CALL_DELAY_MS + 5000) // Extra 5 seconds for safety
        Log.d(TAG, "Wake lock acquired")
    }
    
    /**
     * Called when service is destroyed
     * Cleans up scheduled tasks and releases wake lock
     */
    override fun onDestroy() {
        super.onDestroy()
        callRunnable?.let { handler?.removeCallbacks(it) }
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
                Log.d(TAG, "Wake lock released")
            }
        }
        Log.d(TAG, "CallService destroyed")
    }

    /**
     * Creates notification channel for foreground service
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Service d'appel Cardinal Dial",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Service en arrière-plan pour les appels simulés"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Creates notification for foreground service
     */
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Cardinal Dial")
            .setContentText("Appel en préparation...")
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    /**
     * Service binding not supported
     */
    override fun onBind(intent: Intent?): IBinder? = null
}