package me.thanel.keepasst.database

import com.xwray.groupie.ViewHolder
import de.slackspace.openkeepass.domain.Entry
import kotlinx.android.synthetic.main.item_entry.view.*
import me.thanel.keepasst.R
import me.thanel.keepasst.util.setImageByteArray

class EntryItem(val entry: Entry, level: Int) : BaseEntryItem(level) {
    override fun getLayout() = R.layout.item_entry

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        with(viewHolder.itemView) {
            entryTitle.text = entry.title
            groupIcon.setImageByteArray(entry.iconData)
        }
    }
}