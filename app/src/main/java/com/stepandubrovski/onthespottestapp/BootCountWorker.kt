package com.stepandubrovski.onthespottestapp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.stepandubrovski.onthespottestapp.BootReceiver.Companion.KEY_TEXT_REPLY
import com.stepandubrovski.onthespottestapp.BootReceiver.Companion.NOTIFICATION_ID

class BootCountWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "1"
        private const val CHANNEL_NAME = "Boot Notifications"
        private const val CHANNEL_DESC = "To show boot notifications"

        private const val RETRY_ACTION_NAME = "RETRY_ACTION_NAME"
        private const val RETRY_ACTION_LABEL = "Reset"
    }

    override suspend fun doWork(): Result {
        Log.i("BOOT_RECEIVER_INFO", "Worker")

        sendNotification(NOTIFICATION_ID)

        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(id: Int) {
        Log.i("BOOT_RECEIVER_INFO", "Notification channel created")

        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(CHANNEL_NAME)
            .setDescription(CHANNEL_DESC)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)

        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
            setLabel(RETRY_ACTION_LABEL)
            build()
        }

        val intent = Intent(context, BootReceiver::class.java).apply {
            action = RETRY_ACTION_NAME
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_MUTABLE)

        val action: NotificationCompat.Action =
            NotificationCompat.Action.Builder(android.R.drawable.ic_delete,
                "Reset", pendingIntent)
                .addRemoteInput(remoteInput)
                .build()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_delete)
            .setContentText("Boot notification")
            .setOngoing(true)
            .setAutoCancel(true)
            .addAction(action)
            .build()

        notificationManager.notify(id, notification)
    }
}
