package me.thanel.keepasst.unlock

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.slackspace.openkeepass.exception.KeePassDatabaseUnreadableException
import kotlinx.android.synthetic.main.fragment_database_unlock.*
import me.thanel.keepasst.KeePassStorage
import me.thanel.keepasst.MainActivity
import me.thanel.keepasst.R
import me.thanel.keepasst.database.DatabaseActivity

class DatabaseUnlockFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_database_unlock, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        unlockButton.setOnClickListener {
            unlockDatabase()
        }

        changeDatabaseButton.setOnClickListener {
            changeDatabase()
        }
    }

    private fun unlockDatabase() {
        try {
            val password = passwordInput.text.toString()
            val database = (activity as DatabaseUnlockActivity).keePass.openDatabase(password)
            KeePassStorage.set(context, database)

            startActivity(Intent(context, DatabaseActivity::class.java))
            activity.finish()
        } catch (error: KeePassDatabaseUnreadableException) {
            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun changeDatabase() {
        KeePassStorage.getDatabaseFile(context).delete()

        activity.startActivity(Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        activity.finish()
    }
}
