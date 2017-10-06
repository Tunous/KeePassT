package me.thanel.keepasst.unlock

import android.app.KeyguardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.util.Log
import android.view.View
import android.widget.Toast
import de.slackspace.openkeepass.KeePassDatabase
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException
import kotlinx.android.synthetic.main.fragment_database_unlock.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import me.thanel.keepasst.KeePassStorage
import me.thanel.keepasst.MainActivity
import me.thanel.keepasst.R
import me.thanel.keepasst.base.BaseFragment
import me.thanel.keepasst.database.DatabaseActivity
import me.thanel.keepasst.util.isVisible
import java.io.FileInputStream
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException

class DatabaseUnlockFragment : BaseFragment() {
    override val layoutResId = R.layout.fragment_database_unlock

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        unlockButton.setOnClickListener {
            onUnlock()
        }

        changeDatabaseButton.setOnClickListener {
            changeDatabase()
        }

        enableFingerprintCheckBox.apply {
            isVisible = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val fingerprintManager = context.getSystemService(
                        Context.FINGERPRINT_SERVICE) as FingerprintManager
                fingerprintManager.isHardwareDetected
            } else {
                false
            }
            isChecked = FingerprintPrefs.hasStoredPassword
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    tryShowFingerprintDialog()
                } else {
                    hideError()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tryShowFingerprintDialog()
    }

    private fun onUnlock() {
        val isFingerprintEnabled = enableFingerprintCheckBox.isChecked
        if (!isFingerprintEnabled) {
            FingerprintPrefs.clear()
        } else if (tryShowFingerprintDialog()) {
            return
        }

        launch(UI) {
            val password = passwordInput.text.toString()
            if (!unlockDatabase(password)) {
                // Most likely user has entered wrong master password
                return@launch
            }

            if (isFingerprintEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Fingerprint authentication dialog will open the database once the password
                // has been encrypted with the fingerprint and saved to preferences
                enableFingerprintAuth()
            } else {
                openDatabase()
            }
        }
    }

    private fun tryShowFingerprintDialog(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
        if (!enableFingerprintCheckBox.isEnabled) return false

        val fingerprintManager = context.getSystemService(
                Context.FINGERPRINT_SERVICE) as FingerprintManager
        if (!fingerprintManager.isHardwareDetected) {
            return false
        }
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            showError(R.string.register_fingerprint)
            return false
        }

        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isKeyguardSecure) {
            showError(R.string.enable_secure_lock_screen)
            return false
        }

        hideError()

        if (FingerprintPrefs.hasStoredPassword &&
                fragmentManager.findFragmentByTag(TAG_FINGERPRINT_DIALOG) == null) {
            showFingerprintDialog(FingerprintStorage.getDecryptCipher())
            return true
        }
        return false
    }

    private fun showError(@StringRes errorTextResId: Int) {
        errorView.setText(errorTextResId)
        errorView.isVisible = true
        unlockButton.isEnabled = false
    }

    private fun hideError() {
        errorView.isVisible = false
        unlockButton.isEnabled = true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun onFingerprintSuccess(cipher: Cipher) {
        launch(UI) {
            try {
                val databaseUnlocked = if (FingerprintPrefs.hasStoredPassword) {
                    val password = FingerprintStorage.getPassword(cipher)
                    passwordInput.setText(password)
                    unlockDatabase(password)
                } else {
                    val password = passwordInput.text.toString()
                    FingerprintStorage.putPassword(cipher, password)
                    unlockDatabase(password)
                }

                if (databaseUnlocked) {
                    openDatabase()
                }
            } catch (e: BadPaddingException) {
                Toast.makeText(getContext(),
                        "Failed to encrypt the data with the generated key. " + "Retry the purchase",
                        Toast.LENGTH_LONG).show()
                Log.e(TAG, "Failed to encrypt the data with the generated key." + e.message)
            } catch (e: IllegalBlockSizeException) {
                Toast.makeText(getContext(),
                        "Failed to encrypt the data with the generated key. " + "Retry the purchase",
                        Toast.LENGTH_LONG).show()
                Log.e(TAG, "Failed to encrypt the data with the generated key." + e.message)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun enableFingerprintAuth() {
        FingerprintStorage.generateNewKey()
        showFingerprintDialog(FingerprintStorage.getEncryptCipher())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showFingerprintDialog(cipher: Cipher) {
        val fragment = FingerprintAuthenticationDialogFragment()
        fragment.setCryptoObject(FingerprintManager.CryptoObject(cipher))
        fragment.show(fragmentManager, TAG_FINGERPRINT_DIALOG)
    }

    private suspend fun unlockDatabase(password: String): Boolean {
        unlockProgressBar.isVisible = true
        unlockButton.visibility = View.INVISIBLE

        val database = try {
            run(CommonPool) {
                val inputStream = FileInputStream(KeePassStorage.getDatabaseFile(context))
                val keePass = KeePassDatabase.getInstance(inputStream)
                keePass.openDatabase(password)
            }
        } catch (error: KeePassDatabaseUnreadableException) {
            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            return false
        } finally {
            unlockProgressBar.isVisible = false
            unlockButton.visibility = View.VISIBLE
        }

        KeePassStorage.set(context, database)
        return true
    }

    private fun openDatabase() {
        startActivity(Intent(context, DatabaseActivity::class.java))
        activity.finish()
    }

    private fun changeDatabase() {
        KeePassStorage.getDatabaseFile(context).delete()

        activity.startActivity(Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        activity.finish()
    }

    companion object {
        private const val TAG_FINGERPRINT_DIALOG = "fingerprintDialog"
    }
}
