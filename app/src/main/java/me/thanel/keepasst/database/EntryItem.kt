package me.thanel.keepasst.database

import android.graphics.Paint
import de.slackspace.openkeepass.domain.Entry
import kotlinx.android.synthetic.main.item_entry.view.*
import me.thanel.keepasst.R
import me.thanel.keepasst.util.hasExpired
import me.thanel.keepasst.util.isVisible
import me.thanel.keepasst.util.setImageByteArray

class EntryItem(val entry: Entry, level: Int) : BaseEntryItem(level), FilterableItem {
    var filterText = ""

    override fun getType() = R.id.item_type_entry

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        with(holder.itemView) {
            entryTitle.text = entry.title
            if (entry.hasExpired) {
                entryTitle.paintFlags = entryTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                entryTitle.paintFlags = entryTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            filterTextView.text = filterText
            filterTextView.isVisible = filterText.isNotEmpty()
            groupIcon.setImageByteArray(entry.iconData)
        }
    }

    override fun filter(constraint: String?, options: SearchOptions): Boolean {
        if (options.excludeExpired && entry.hasExpired) return true

        // If filter text is empty keep this entry visible
        if (constraint == null || constraint.isEmpty()) {
            filterText = ""
            return false
        }

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
        if (options.filterByProperties && entry.customProperties.any {
            it.value.contains(constraint, ignoreCase)
        }) {
            filterText = "Matches custom property"
            return false
        }

        return true
        // TODO:
        //  - filter by tags
        //  - filter by group name
        //  - regex
    }
}
