package me.thanel.keepasst.database

import de.slackspace.openkeepass.domain.Entry
import kotlinx.android.synthetic.main.item_entry.view.*
import me.thanel.keepasst.R
import me.thanel.keepasst.util.setImageByteArray

class EntryItem(val entry: Entry, level: Int) : BaseEntryItem(level), FilterableItem {
    override fun getType() = R.id.item_type_entry

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        with(holder.itemView) {
            entryTitle.text = entry.title
            groupIcon.setImageByteArray(entry.iconData)
        }
    }

    override fun filter(constraint: String?): Boolean {
        // If filter text is empty keep this entry visible
        if (constraint.isNullOrEmpty()) return false

        // Ignore case only if all characters are lower case
        val ignoreCase = constraint!!.all { it.isLowerCase() }

        return !entry.title.contains(constraint, ignoreCase)
    }
}
