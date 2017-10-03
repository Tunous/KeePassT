package me.thanel.keepasst.base

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*
import me.thanel.keepasst.KeePassStorage
import me.thanel.keepasst.R
import me.thanel.keepasst.unlock.DatabaseUnlockActivity

abstract class BaseActivity : AppCompatActivity() {
    /**
     * Specifies whether the toolbar should display navigate up button.
     */
    protected open val displayHomeAsUpEnabled = true

    /**
     * Tells whether this activity requires unlocked database to function correctly.
     *
     * If set to `true` then the database will be checked on every resume. If it's detected to be
     * locked then this activity will be finished and user will be redirected to unlock screen.
     */
    protected open val requireDatabase = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled)

        checkDatabase()

        if (savedInstanceState == null && !isFinishing) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, createFragment())
                    .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        checkDatabase()
    }

    private fun checkDatabase() {
        if (!requireDatabase || isFinishing) return

        val database = KeePassStorage.get(this)
        if (database == null) {
            startActivity(DatabaseUnlockActivity.newIntent(this)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }

    abstract fun createFragment(): Fragment
}