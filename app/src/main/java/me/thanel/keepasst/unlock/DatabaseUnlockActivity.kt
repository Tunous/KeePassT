package me.thanel.keepasst.unlock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import de.slackspace.openkeepass.KeePassDatabase
import me.thanel.keepasst.base.BaseActivity
import me.thanel.keepasst.KeePassStorage
import java.io.FileInputStream

class DatabaseUnlockActivity : BaseActivity() {
    lateinit var keePass: KeePassDatabase

    override val displayHomeAsUpEnabled = false

    override fun createFragment() = DatabaseUnlockFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inputStream = FileInputStream(KeePassStorage.getDatabaseFile(this))
        keePass = KeePassDatabase.getInstance(inputStream)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, DatabaseUnlockActivity::class.java)
        }
    }
}
