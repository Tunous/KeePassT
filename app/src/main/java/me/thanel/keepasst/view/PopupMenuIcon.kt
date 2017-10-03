package me.thanel.keepasst.view

import android.content.Context
import android.support.annotation.MenuRes
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.view_entry_detail.view.*
import me.thanel.keepasst.R

class PopupMenuIcon @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val popupMenu = PopupMenu(context, this)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PopupMenuIcon, defStyleAttr, 0)
        val menuRes = a.getResourceId(R.styleable.PopupMenuIcon_menu, -1)
        a.recycle()

        if (menuRes != -1) {
            setMenu(menuRes)
        }
    }

    fun setMenu(@MenuRes menuRes: Int, listener: PopupMenu.OnMenuItemClickListener? = null) {
        val menu = popupMenu.menu
        menu.clear()
        popupMenu.inflate(menuRes)

        val item = menu.getItem(0)
        if (menu.size() == 1 && item.icon != null) {
            // When there is only one item with icon make it inlined
            actionIcon.setImageDrawable(item.icon)
        }

        setOnMenuItemClickListener(listener)
    }

    fun performAction() {
        val menu = popupMenu.menu
        val item = menu.getItem(0)
        if (menu.size() == 1 && item.icon != null) {
            menu.performIdentifierAction(item.itemId, 0)
        } else {
            popupMenu.show()
        }
    }

    fun setOnMenuItemClickListener(listener: PopupMenu.OnMenuItemClickListener?) {
        popupMenu.setOnMenuItemClickListener(listener)
    }
}