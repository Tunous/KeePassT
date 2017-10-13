package me.thanel.keepasst

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import de.slackspace.openkeepass.domain.KeePassFile
import java.io.File

object KeePassStorage {
    private val TAG = KeePassStorage::class.java.simpleName
    private var keePassFile: KeePassFile? = null

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Intent.ACTION_SCREEN_OFF == intent.action) {
                Log.d(TAG, "Screen off, locking database")
                set(context, null)
            }
        }
    }

    fun get() = keePassFile

    fun set(context: Context, file: KeePassFile?) {
        if (keePassFile == null && file != null) {
            // first set file, register screen-off receiver.
            registerBroadcastReceiver(context)
        } else if (keePassFile != null && file == null) {
            // clear file, unregister it.
            context.applicationContext.unregisterReceiver(broadcastReceiver)
        }
        keePassFile = file
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
}