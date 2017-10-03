package me.thanel.keepasst.util

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView

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
