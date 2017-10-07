package me.thanel.keepasst.entry

import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.PopupMenu
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import de.slackspace.openkeepass.domain.Entry
import kotlinx.android.synthetic.main.fragment_entry.*
import me.thanel.keepasst.R
import me.thanel.keepasst.base.BaseFragment
import me.thanel.keepasst.util.copyToClipboard
import me.thanel.keepasst.util.highlightLinks
import me.thanel.keepasst.util.isVisible
import me.thanel.keepasst.util.openInBrowser
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class EntryFragment : BaseFragment(), PopupMenu.OnMenuItemClickListener {
    private var isPasswordVisible = false
    private lateinit var entry: Entry

    override val layoutResId = R.layout.fragment_entry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val id = arguments.getSerializable(EXTRA_ENTRY_ID) as UUID
        entry = database?.getEntryByUUID(id) ?: return

        activity.title = entry.title

        if (!entry.username.isNullOrEmpty()) {
            userNameView.contentView.text = entry.username
            userNameView.setOnMenuItemClickListener(this)
        } else {
            userNameView.isVisible = false
            userNameDivider.isVisible = false
        }

        if (!entry.password.isNullOrEmpty()) {
            passwordView.contentView.apply {
                text = entry.password
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordView.setOnMenuItemClickListener(this)
            updatePasswordMenuItem()
        } else {
            passwordView.isVisible = false
            passwordDivider.isVisible = false
        }

        if (!entry.url.isNullOrEmpty()) {
            urlView.contentView.apply {
                text = entry.url
                highlightLinks()
            }
            urlView.setOnMenuItemClickListener(this)
        } else {
            urlView.isVisible = false
            urlDivider.isVisible = false
        }

        creationDateView.contentView.text = formatTime(entry.times.creationTime)
        modificationDateView.contentView.text = formatTime(entry.times.lastModificationTime)
        if (entry.times.expires()) {
            val expiryTime = entry.times.expiryTime
            expirationDateView.contentView.text = formatTime(expiryTime)

            if (expiryTime.before(Calendar.getInstance())) {
                // If the password has expired display colored warning icon
                val icon = ContextCompat.getDrawable(context, R.drawable.ic_warning)
                val color = ContextCompat.getColor(context, R.color.warning_color)
                expirationDateView.setIcon(icon, color)
            }
        } else {
            expirationDateView.isVisible = false
        }

        if (!entry.notes.isNullOrEmpty()) {
            notesView.contentView.apply {
                text = entry.notes
                setTextIsSelectable(true)
            }
        } else {
            notesView.isVisible = false
            notesDivider.isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_entry, menu)

        val passwordToggleItem = menu.findItem(R.id.toggle_password_visibility)
        if (entry.password.isNullOrEmpty()) {
            passwordToggleItem.isVisible = false
        } else {
            updatePasswordMenuItem(passwordToggleItem)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onMenuItemClick(item) || super.onOptionsItemSelected(item)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.copy_username -> copyUsername()
            R.id.copy_password -> copyPassword()
            R.id.toggle_password_visibility -> togglePasswordVisibility()
            R.id.copy_url -> copyUrl()
            R.id.open_in_browser -> openUrlInBrowser()
            else -> return false
        }
        return true
    }

    private fun formatTime(calendar: Calendar): String {
        val formattedTime = SimpleDateFormat.getDateTimeInstance().format(calendar.time)
        val relativeTime = PrettyTime().format(calendar)
        return getString(R.string.time_format, formattedTime, relativeTime)
    }

    private fun copyUsername() {
        context.copyToClipboard(getString(R.string.username), entry.username)
        Toast.makeText(context, R.string.username_copied, Toast.LENGTH_SHORT).show()
    }

    private fun copyPassword() {
        context.copyToClipboard(getString(R.string.password), entry.password)
        Toast.makeText(context, R.string.password_copied, Toast.LENGTH_SHORT).show()
    }

    private fun copyUrl() {
        context.copyToClipboard(getString(R.string.url), entry.url)
        Toast.makeText(context, R.string.url_copied, Toast.LENGTH_SHORT).show()
    }

    private fun openUrlInBrowser() {
        context.openInBrowser(Uri.parse(entry.url))
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        passwordView.contentView.apply {
            val passwordFlag = if (isPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            inputType = InputType.TYPE_CLASS_TEXT or passwordFlag
        }

        updatePasswordMenuItem()
        activity.invalidateOptionsMenu()
    }

    private fun updatePasswordMenuItem(item: MenuItem? = null) {
        val visibilityItem =
                item ?: passwordView.actionMenu.findItem(R.id.toggle_password_visibility)
        visibilityItem.setTitle(
                if (isPasswordVisible) R.string.hide_password else R.string.show_password)
        if (item == null) {
            visibilityItem.setIcon(
                    if (isPasswordVisible) R.drawable.ic_hide else R.drawable.ic_show)
        } else {
            visibilityItem.setIcon(
                    if (isPasswordVisible) R.drawable.ic_hide_white else R.drawable.ic_show_white)
        }
    }

    companion object {
        private const val EXTRA_ENTRY_ID = "entry_id"

        fun newInstance(entryId: UUID) = EntryFragment().apply {
            arguments = Bundle().apply {
                putSerializable(EXTRA_ENTRY_ID, entryId)
            }
        }
    }
}
