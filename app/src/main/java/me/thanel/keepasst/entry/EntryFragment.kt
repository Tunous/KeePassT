package me.thanel.keepasst.entry

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_entry.*
import me.thanel.keepasst.R
import me.thanel.keepasst.base.BaseFragment
import java.text.SimpleDateFormat
import java.util.*

class EntryFragment : BaseFragment() {
    override val layoutResId = R.layout.fragment_entry

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val id = arguments.getSerializable(EXTRA_ENTRY_ID) as UUID
        val entry = database?.getEntryByUUID(id) ?: return

        with(entry) {
            activity.title = title

            userNameView.content = username
            urlView.content = url
            passwordView.content = password
            notesView.content = notes

            val format = SimpleDateFormat.getDateTimeInstance()
            creationDateView.content = format.format(times.creationTime.time)
            modificationDateView.content = format.format(times.lastModificationTime.time)
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
