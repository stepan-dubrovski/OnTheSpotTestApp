package com.stepandubrovski.onthespottestapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val DEFAULT_INITIAL_DELAY = 2L
        private const val WORK_NAME = "BOOT_TRACKER"

        const val KEY_TEXT_REPLY = "KEY_TEXT_REPLY"
        var NOTIFICATION_ID = 111;
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("BOOT_RECEIVER_INFO", "Started")
        Log.i("BOOT_RECEIVER_INFO", intent.action.toString())

        var initialDelay = DEFAULT_INITIAL_DELAY

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // check and save counter amount to data store
        } else {
            // catch the Exception just in case of wrong value passed
            initialDelay = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY).toString().toLong()

            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
        }

        val work = OneTimeWorkRequest.Builder(BootCountWorker::class.java)
            .setInitialDelay(initialDelay, TimeUnit.MINUTES)
            .addTag(WORK_NAME)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, work)
    }
}
