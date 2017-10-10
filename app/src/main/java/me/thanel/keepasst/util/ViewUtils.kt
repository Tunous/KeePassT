package me.thanel.keepasst.util

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.graphics.drawable.DrawableCompat
import android.text.InputType
import android.text.util.Linkify
import android.view.View
import android.widget.ImageView
import android.widget.TextView

fun ImageView.setImageByteArray(byteData: ByteArray, offset: Int = 0) {
    val image = BitmapDrawable(resources,
            BitmapFactory.decodeByteArray(byteData, offset, byteData.size))
    setImageDrawable(image)
}

var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

fun TextView.highlightLinks() {
    Linkify.addLinks(this, Linkify.WEB_URLS)
    movementMethod = null
    linksClickable = false
}

fun ImageView.setImageDrawableTinted(drawable: Drawable, @ColorInt color: Int) {
    val tintDrawable = DrawableCompat.wrap(drawable).mutate()
    DrawableCompat.setTint(tintDrawable, color)
    setImageDrawable(tintDrawable)
}

var TextView.isPasswordProtected: Boolean
    get() = inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    set(value) {
        val flag = if (value) {
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        inputType = InputType.TYPE_CLASS_TEXT or flag
    }
