package me.thanel.keepasst.database

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import me.thanel.keepasst.KeePassStorage
import me.thanel.keepasst.R
import me.thanel.keepasst.base.BaseActivity
import me.thanel.keepasst.unlock.DatabaseUnlockActivity

class DatabaseActivity : BaseActivity() {
    override val displayHomeAsUpEnabled = false
    override val requireDatabase = true

    override fun createFragment() = DatabaseFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = KeePassStorage.get()
        if (database == null) {
            lockDatabaseAndFinish()
            return
        }

        val name = database.meta.databaseName
        supportActionBar?.title = if (name.isNullOrBlank()) {
            getString(R.string.unnamed_database)
        } else {
            name
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.database, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_lock -> lockDatabaseAndFinish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun lockDatabaseAndFinish() {
        KeePassStorage.set(this, null)
        startActivity(DatabaseUnlockActivity.newIntent(this)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }
}
