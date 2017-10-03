package me.thanel.keepasst.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.AttrRes

fun Context.resolveDrawable(@AttrRes attrResId: Int): Drawable {
    val a = obtainStyledAttributes(intArrayOf(attrResId))
    val drawable = a.getDrawable(0)
    a.recycle()
    return drawable
}
