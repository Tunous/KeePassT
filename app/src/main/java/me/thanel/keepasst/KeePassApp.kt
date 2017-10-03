package me.thanel.keepasst

import android.app.Application
import com.chibatching.kotpref.Kotpref

class KeePassApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Kotpref.init(this)
    }
}