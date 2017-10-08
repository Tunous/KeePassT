package me.thanel.keepasst.database

import android.content.Context
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.view.View
import com.mikepenz.fastadapter.commons.items.AbstractExpandableItem
import kotlinx.android.synthetic.main.item_entry.view.*
import me.thanel.keepasst.R
import me.thanel.keepasst.util.isVisible

abstract class BaseEntryItem(private val level: Int) : AbstractExpandableItem<BaseEntryItem, ViewHolder, BaseEntryItem>() {

    protected open val isExpandable = false

    override fun getLayoutRes() = R.layout.item_entry

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) = with(holder.itemView) {
        super.bindView(holder, payloads)

        val levelWidth = resources.getDimensionPixelSize(R.dimen.level_width) * level
        if (levelWidth > 0) {
            levelMarker.layoutParams = levelMarker.layoutParams.apply {
                width = levelWidth
            }
            levelMarker.isVisible = true
            colorMarker.isVisible = true

            colorMarker.setBackgroundColor(getMarkerColor(context, level - 1))
        } else {
            levelMarker.isVisible = false
            colorMarker.isVisible = false
        }

        expandIcon.isVisible = isExpandable
    }

    override fun getViewHolder(v: View) = ViewHolder(v)

    protected fun bindExpandIcon(viewHolder: ViewHolder, isExpanded: Boolean) {
        viewHolder.itemView.expandIcon.setImageResource(R.drawable.ic_arrow_drop_down)
        viewHolder.itemView.expandIcon.rotation = if (isExpanded) 180f else 0f
    }

    @ColorInt
    private fun getMarkerColor(context: Context, level: Int): Int {
        val colorId = when (level % 5) {
            0 -> R.color.level1
            1 -> R.color.level2
            2 -> R.color.level3
            3 -> R.color.level4
            else -> R.color.level5
        }
        return ContextCompat.getColor(context, colorId)
    }
}
