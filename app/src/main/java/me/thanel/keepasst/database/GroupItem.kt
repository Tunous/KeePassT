package me.thanel.keepasst.database

import android.support.v4.view.ViewCompat
import com.mikepenz.fastadapter.FastAdapter
import de.slackspace.openkeepass.domain.Group
import kotlinx.android.synthetic.main.item_entry.view.*
import me.thanel.keepasst.R
import me.thanel.keepasst.util.setImageByteArray

class GroupItem(private val group: Group, level: Int) : BaseEntryItem(level) {
    override val isExpandable = true

    override fun getType() = R.id.item_type_group

    private val onClickListener = FastAdapter.OnClickListener<BaseEntryItem> { v, _, item, _ ->
        if (item.isExpanded) {
            ViewCompat.animate(v.expandIcon).rotation(180f).start()
        } else {
            ViewCompat.animate(v.expandIcon).rotation(0f).start()
        }
        return@OnClickListener true
    }

    override fun getOnItemClickListener(): FastAdapter.OnClickListener<BaseEntryItem> {
        return onClickListener
    }

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        with(holder.itemView) {
            entryTitle.text = group.name
            groupIcon.setImageByteArray(group.iconData)
            bindExpandIcon(holder, isExpanded)
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.itemView.expandIcon.clearAnimation()
    }
}