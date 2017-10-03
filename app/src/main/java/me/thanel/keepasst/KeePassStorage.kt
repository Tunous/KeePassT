package me.thanel.keepasst

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.SystemClock
import android.util.Log
import de.slackspace.openkeepass.domain.KeePassFile
import java.io.File
import java.util.concurrent.TimeUnit

object KeePassStorage {
    private val authTimeoutMillis = TimeUnit.MINUTES.toMillis(5)
    private var keePassFile: KeePassFile? = null
    private var lastAuthTime = 0L

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Intent.ACTION_SCREEN_OFF == intent.action) {
                Log.d(KeePassStorage::class.java.simpleName, "Screen off, clean keepass file")
                set(context, null)
            }
        }
    }

    fun get(context: Context): KeePassFile? {
        if (keePassFile != null && hasExpired()) {
            set(context, null)
        }
        return keePassFile
    }

    fun set(context: Context, file: KeePassFile?) {
        if (keePassFile == null && file != null) {
            // first set file, register screen-off receiver.
            registerBroadcastReceiver(context)
        } else if (keePassFile != null && file == null) {
            // clear file, unregister it.
            context.applicationContext.unregisterReceiver(broadcastReceiver)
        }
        keePassFile = file
        lastAuthTime = SystemClock.elapsedRealtime()
    }

    fun getDatabaseFile(context: Context): File {
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.noBackupFilesDir
        } else {
            context.filesDir
        }
        return File(dir, "database.kdbx")
    }

    private fun registerBroadcastReceiver(context: Context) {
        val screenOffFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        context.applicationContext
                .registerReceiver(broadcastReceiver, screenOffFilter)
    }

    private fun hasExpired() = SystemClock.elapsedRealtime() - lastAuthTime > authTimeoutMillis
}