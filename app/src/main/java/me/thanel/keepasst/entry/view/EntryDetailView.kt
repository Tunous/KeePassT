package me.thanel.keepasst.entry.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.TextView
import kotlinx.android.synthetic.main.view_entry_detail.view.*
import me.thanel.keepasst.R
import me.thanel.keepasst.util.isVisible
import me.thanel.keepasst.util.resolveDrawable

class EntryDetailView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        inflate(context, R.layout.view_entry_detail, this)

        background = context.resolveDrawable(R.attr.selectableItemBackground)

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.EntryDetailView, defStyleAttr, 0)
        titleView.text = a.getText(R.styleable.EntryDetailView_title)
        icon.setImageDrawable(a.getDrawable(R.styleable.EntryDetailView_iconDrawable))
        val menuRes = a.getResourceId(R.styleable.EntryDetailView_menu, -1)
        a.recycle()

        if (menuRes != -1) {
            actionIcon.setMenu(menuRes)
            actionIcon.isVisible = true

            setOnClickListener {
                actionIcon.performAction()
            }
        } else {
            actionIcon.isVisible = false
        }
    }

    val contentView: TextView = findViewById(R.id.contentView)
}

