package me.thanel.keepasst.database

import android.content.Context
import android.support.annotation.CallSuper
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_entry.view.*
import me.thanel.keepasst.R
import me.thanel.keepasst.util.isVisible

abstract class BaseEntryItem(private val level: Int) : Item<ViewHolder>() {
    override fun getLayout() = R.layout.item_entry

    @CallSuper
    override fun bind(viewHolder: ViewHolder, position: Int) = with(viewHolder.itemView) {
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

        expandIcon.isVisible = this@BaseEntryItem is ExpandableItem
    }

    protected fun bindExpandIcon(viewHolder: ViewHolder, isExpanded: Boolean) {
        viewHolder.itemView.expandIcon.setImageResource(if (isExpanded) {
            R.drawable.ic_arrow_drop_up
        } else {
            R.drawable.ic_arrow_drop_down
        })
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