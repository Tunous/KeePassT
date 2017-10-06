package me.thanel.keepasst.entry

import android.os.Bundle
import android.text.InputType
import kotlinx.android.synthetic.main.fragment_entry.*
import me.thanel.keepasst.R
import me.thanel.keepasst.base.BaseFragment
import me.thanel.keepasst.util.highlightLinks
import java.text.SimpleDateFormat
import java.util.*

class EntryFragment : BaseFragment() {
    override val layoutResId = R.layout.fragment_entry

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val id = arguments.getSerializable(EXTRA_ENTRY_ID) as UUID
        val entry = database?.getEntryByUUID(id) ?: return

        activity.title = entry.title

        userNameView.contentView.apply {
            text = entry.username
        }
        passwordView.contentView.apply {
            text = entry.password
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        urlView.contentView.apply {
            text = entry.url
            highlightLinks()
        }
        notesView.contentView.apply {
            text = entry.notes
            setTextIsSelectable(true)
        }

        val format = SimpleDateFormat.getDateTimeInstance()
        creationDateView.contentView.apply {
            text = format.format(entry.times.creationTime.time)
        }
        modificationDateView.contentView.apply {
            text = format.format(entry.times.lastModificationTime.time)
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
