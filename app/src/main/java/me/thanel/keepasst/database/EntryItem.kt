package me.thanel.keepasst.database

import de.slackspace.openkeepass.domain.Entry
import kotlinx.android.synthetic.main.item_entry.view.*
import me.thanel.keepasst.R
import me.thanel.keepasst.util.isVisible
import me.thanel.keepasst.util.setImageByteArray
import java.util.*

class EntryItem(val entry: Entry, level: Int) : BaseEntryItem(level), FilterableItem {
    var filterText = ""

    override fun getType() = R.id.item_type_entry

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        with(holder.itemView) {
            entryTitle.text = entry.title
            filterTextView.text = filterText
            filterTextView.isVisible = filterText.isNotEmpty()
            groupIcon.setImageByteArray(entry.iconData)
        }
    }

    override fun filter(constraint: String?, options: SearchOptions): Boolean {
        // If filter text is empty keep this entry visible
        if (constraint == null || constraint.isEmpty()) {
            filterText = ""
            return false
        }

        val hasExpired = entry.times.expires() &&
                entry.times.expiryTime.before(Calendar.getInstance())
        if (options.excludeExpired && hasExpired) return true

        val ignoreCase = !options.caseSensitive

        if (options.filterByTitle && entry.title.contains(constraint, ignoreCase)) {
            filterText = "Matches title"
            return false
        }
        if (options.filterByUsername && entry.username.contains(constraint, ignoreCase)) {
            filterText = "Matches username"
            return false
        }
        if (options.filterByNotes && entry.notes.contains(constraint, ignoreCase)) {
            filterText = "Matches notes"
            return false
        }
        if (options.filterByPassword && entry.password.contains(constraint, ignoreCase)) {
            filterText = "Matches password"
            return false
        }
        if (options.filterByUrl && entry.url.contains(constraint, ignoreCase)) {
            filterText = "Matches url"
            return false
        }
        return true
        // TODO:
        //  - filter by tags
        //  - filter by group name
        //  - regex
    }
}
