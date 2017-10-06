package me.thanel.keepasst.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt

fun Context.resolveDrawable(@AttrRes attrResId: Int): Drawable {
    val a = obtainStyledAttributes(intArrayOf(attrResId))
    val drawable = a.getDrawable(0)
    a.recycle()
    return drawable
}

@ColorInt
fun Context.resolveColor(@AttrRes attrResId: Int): Int {
    val a = obtainStyledAttributes(intArrayOf(attrResId))
    val color = a.getColor(0, 0)
    a.recycle()
    return color
}
