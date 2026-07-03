package com.example.tsenantsika.network.server

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.koin.android.ext.android.inject

class BoutiqueServerService : Service() {

    private val serverManager: KtorServerManager by inject()
    private val serviceDiscovery: ServiceDiscovery by inject()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tsenantsika")
            .setContentText("Serveur boutique actif")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
        serverManager.start()
        serviceDiscovery.register(serverManager.port)
        return START_STICKY
    }

    override fun onDestroy() {
        serviceDiscovery.unregister()
        serverManager.stop()
        super.onDestroy()
    }

    private fun createChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Serveur boutique", NotificationManager.IMPORTANCE_LOW)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "boutique_server"
        private const val NOTIFICATION_ID = 1
        fun start(context: Context) = context.startForegroundService(Intent(context, BoutiqueServerService::class.java))
        fun stop(context: Context) = context.stopService(Intent(context, BoutiqueServerService::class.java))
    }
}
