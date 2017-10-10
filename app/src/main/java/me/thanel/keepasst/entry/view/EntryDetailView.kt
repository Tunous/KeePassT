package me.thanel.keepasst.entry.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.MenuRes
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.PopupMenu
import android.util.AttributeSet
import android.widget.TextView
import kotlinx.android.synthetic.main.view_entry_detail.view.*
import me.thanel.keepasst.R
import me.thanel.keepasst.util.isVisible
import me.thanel.keepasst.util.resolveColor
import me.thanel.keepasst.util.setImageDrawableTinted
import me.thanel.keepasst.view.PopupMenuIcon

class EntryDetailView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    val actionIcon: PopupMenuIcon
    val contentView: TextView

    init {
        inflate(context, R.layout.view_entry_detail, this)

        actionIcon = findViewById(R.id.actionIcon)
        contentView = findViewById(R.id.contentView)

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.EntryDetailView, defStyleAttr, 0)

        setTitle(a.getText(R.styleable.EntryDetailView_title))
        setIcon(a.getDrawable(R.styleable.EntryDetailView_iconDrawable))
        setMenu(a.getResourceId(R.styleable.EntryDetailView_menu, -1))

        a.recycle()
    }

    fun setOnMenuItemClickListener(listener: PopupMenu.OnMenuItemClickListener) {
        actionIcon.setOnMenuItemClickListener(listener)
    }

    fun setTitle(title: CharSequence?) {
        titleView.text = title
        titleView.isVisible = title != null
    }

    fun setMenu(@MenuRes menuRes: Int) {
        if (menuRes == -1) {
            actionIcon.isVisible = false
            return
        }

        actionIcon.setMenu(menuRes)
        actionIcon.isVisible = true

        setOnClickListener {
            actionIcon.performAction()
        }
    }

    fun setIcon(drawable: Drawable?, @ColorInt color: Int? = null) {
        if (drawable == null) {
            icon.setImageDrawable(null)
            icon.isVisible = false
            return
        }

        val tintColor = color ?: context.resolveColor(android.R.attr.textColorSecondary)
        icon.setImageDrawableTinted(drawable, tintColor)
        icon.isVisible = true
    }
}

