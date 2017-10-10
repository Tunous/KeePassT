package me.thanel.keepasst.view

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.MenuRes
import android.support.v4.content.ContextCompat
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.view.menu.MenuPopupHelper
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.PopupMenu
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.view_entry_detail.view.*
import me.thanel.keepasst.R

@SuppressLint("RestrictedApi")
class PopupMenuIcon @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val popupMenu = PopupMenu(context, this)
    private val menuHelper = MenuPopupHelper(context, popupMenu.menu as MenuBuilder, this).apply {
        setForceShowIcon(true)
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PopupMenuIcon, defStyleAttr, 0)
        val menuRes = a.getResourceId(R.styleable.PopupMenuIcon_menu, -1)
        a.recycle()

        if (menuRes != -1) {
            setMenu(menuRes)
        }
    }

    val menu get() = popupMenu.menu

    fun setMenu(@MenuRes menuRes: Int, listener: PopupMenu.OnMenuItemClickListener? = null) {
        val menu = popupMenu.menu
        menu.clear()
        popupMenu.inflate(menuRes)
        updateActionIcon()
        setOnMenuItemClickListener(listener)
    }

    fun setMenuItemVisible(@IdRes id: Int, visible: Boolean) {
        popupMenu.menu.findItem(id).isVisible = visible
        updateActionIcon()
    }

    fun performAction() {
        val menu = popupMenu.menu

        val item = getOnlyVisibleItemWithIcon(menu)
        if (item != null) {
            menu.performIdentifierAction(item.itemId, 0)
        } else {
            menuHelper.show()
        }
    }

    fun setOnMenuItemClickListener(listener: PopupMenu.OnMenuItemClickListener?) {
        popupMenu.setOnMenuItemClickListener(listener)
    }

    private fun updateActionIcon() {
        val item = getOnlyVisibleItemWithIcon(popupMenu.menu)
        if (item != null) {
            // When there is only one item with icon make it inlined
            actionIcon.setImageDrawable(item.icon)
        } else {
            actionIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_more_vert))
        }
    }

    /**
     * Get the only visible item with icon in the specified [menu]. If the menu has no items or
     * there are more visible items than one, then this method will return `null`.
     *
     * @return The only visible item with icon or `null` if there were no or more than one item.
     */
    private fun getOnlyVisibleItemWithIcon(menu: Menu): MenuItem? {
        val menuItems = (0 until menu.size()).map(menu::getItem)
        return menuItems.singleOrNull { it.isVisible && it.icon != null }
    }
}