package me.thanel.keepasst.database

import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.ViewHolder
import de.slackspace.openkeepass.domain.Group
import kotlinx.android.synthetic.main.item_entry.view.*
import me.thanel.keepasst.util.setImageByteArray

class HeaderItem(private val group: Group, level: Int) : BaseEntryItem(level), ExpandableItem {
    private lateinit var expandableGroup: ExpandableGroup

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        expandableGroup = onToggleListener
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        with(viewHolder.itemView) {
            entryTitle.text = group.name
            groupIcon.setImageByteArray(group.iconData)
            setOnClickListener {
                expandableGroup.onToggleExpanded()
                bindExpandIcon(viewHolder, expandableGroup.isExpanded)
            }
            bindExpandIcon(viewHolder, expandableGroup.isExpanded)
        }
    }
}