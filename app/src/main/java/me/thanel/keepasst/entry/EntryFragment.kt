package me.thanel.keepasst.entry

import android.net.Uri
import android.os.Bundle
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
import me.thanel.keepasst.util.openInBrowser
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

        userNameView.initContentView {
            text = entry.username
        }
        userNameView.setOnMenuItemClickListener(this)

        passwordView.initContentView {
            text = entry.password
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        passwordView.setOnMenuItemClickListener(this)
        updatePasswordMenuItem()

        urlView.initContentView {
            text = entry.url
            highlightLinks()
        }
        urlView.setOnMenuItemClickListener(this)

        notesView.initContentView {
            text = entry.notes
            setTextIsSelectable(true)
        }

        val format = SimpleDateFormat.getDateTimeInstance()
        creationDateView.initContentView {
            text = format.format(entry.times.creationTime.time)
        }
        modificationDateView.initContentView {
            text = format.format(entry.times.lastModificationTime.time)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_entry, menu)
        updatePasswordMenuItem(menu.findItem(R.id.toggle_password_visibility))
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

        passwordView.initContentView {
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
