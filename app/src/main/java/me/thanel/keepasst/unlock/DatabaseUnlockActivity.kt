package me.thanel.keepasst.unlock

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import me.thanel.keepasst.R
import me.thanel.keepasst.base.BaseActivity
import javax.crypto.Cipher

class DatabaseUnlockActivity : BaseActivity(), FingerprintUiHelper.Callback {
    override val displayHomeAsUpEnabled = false

    override fun createFragment() = DatabaseUnlockFragment()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onAuthenticated(cipher: Cipher) {
        val unlockFragment = supportFragmentManager.findFragmentById(
                R.id.fragmentContainer) as DatabaseUnlockFragment

        unlockFragment.onFingerprintSuccess(cipher)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, DatabaseUnlockActivity::class.java)
        }
    }
}
