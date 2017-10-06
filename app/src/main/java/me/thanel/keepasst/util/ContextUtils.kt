package me.thanel.keepasst.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
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

fun Context.copyToClipboard(label: CharSequence, text: CharSequence) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.primaryClip = ClipData.newPlainText(label, text)
}

fun Context.openInBrowser(uri: Uri) {
    // TODO: Custom tabs
    startActivity(Intent(Intent.ACTION_VIEW, uri)
            .addCategory(Intent.CATEGORY_BROWSABLE))
}
