package me.thanel.keepasst.unlock

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.util.Base64
import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.bulk
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@RequiresApi(Build.VERSION_CODES.M)
object FingerprintStorage {
    private const val KEY_ALIAS = "KeePassT"

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    fun generateNewKey() {
        val keySpec = KeyGenParameterSpec.Builder(KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .build()

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore")
        keyGenerator.init(keySpec)
        keyGenerator.generateKey()
    }

    fun getEncryptCipher() = getCipher(Cipher.ENCRYPT_MODE, null)

    fun getDecryptCipher() =
        getCipher(Cipher.DECRYPT_MODE, Base64.decode(FingerprintPrefs.iv, Base64.DEFAULT))

    fun putPassword(cipher: Cipher, password: String) {
        val passwordBytes = password.toByteArray()
        val encryptedPassword = cipher.doFinal(passwordBytes)
        val encodedPassword = Base64.encodeToString(encryptedPassword, Base64.DEFAULT)
        FingerprintPrefs.bulk {
            storePassword = encodedPassword
            iv = Base64.encodeToString(cipher.iv, Base64.DEFAULT)
        }
    }

    fun getPassword(cipher: Cipher): String {
        val encodedPassword = FingerprintPrefs.storePassword
        val encryptedPassword = Base64.decode(encodedPassword, Base64.DEFAULT)
        val passwordBytes = cipher.doFinal(encryptedPassword)
        return String(passwordBytes, Charsets.UTF_8)
    }

    private fun getCipher(mode: Int, iv: ByteArray?): Cipher {
        val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
        val cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/" +
                        KeyProperties.BLOCK_MODE_CBC + "/" +
                        KeyProperties.ENCRYPTION_PADDING_PKCS7)
        if (iv != null) {
            cipher.init(mode, key, IvParameterSpec(iv))
        } else {
            cipher.init(mode, key)
        }
        return cipher
    }
}

object FingerprintPrefs : KotprefModel() {
    var iv by nullableStringPref()
    var storePassword by nullableStringPref()

    val hasStoredPassword get() = iv != null && storePassword != null
}