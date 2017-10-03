package me.thanel.keepasst

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.thanel.keepasst.unlock.DatabaseUnlockActivity
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
                    .setType("*/*")
            val chooserIntent = Intent.createChooser(intent, "Choose a file")
            startActivityForResult(chooserIntent, REQUEST_OPEN_FILE)
        }

        val databaseFile = KeePassStorage.getDatabaseFile(this)
        if (databaseFile.exists()) {
            openDatabaseAndFinish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_OPEN_FILE -> if (resultCode == Activity.RESULT_OK && data != null) {
                val inputStream = getInputStream(data.data)
                if (inputStream != null) {
                    val databaseFile = KeePassStorage.getDatabaseFile(this)
                    val outputStream = BufferedOutputStream(FileOutputStream(databaseFile))
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()
                }
                openDatabaseAndFinish()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getInputStream(uri: Uri) = when (uri.scheme) {
        "content" -> contentResolver.openInputStream(uri)
        "file" -> FileInputStream(File(uri.toString()))
        else -> null
    }

    private fun openDatabaseAndFinish() {
        startActivity(DatabaseUnlockActivity.newIntent(this))
        finish()
    }

    companion object {
        private const val REQUEST_OPEN_FILE = 1
    }
}
