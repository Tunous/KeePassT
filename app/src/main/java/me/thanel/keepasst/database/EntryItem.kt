package me.thanel.keepasst.database

import android.graphics.Paint
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import de.slackspace.openkeepass.domain.Entry
import kotlinx.android.synthetic.main.item_entry.view.*
import me.thanel.keepasst.R
import me.thanel.keepasst.entry.matcher.EntryMatcher
import me.thanel.keepasst.entry.matcher.MatchResult
import me.thanel.keepasst.entry.matcher.MatchType
import me.thanel.keepasst.util.hasExpired
import me.thanel.keepasst.util.isVisible
import me.thanel.keepasst.util.setImageByteArray

class EntryItem(val entry: Entry, level: Int) : BaseEntryItem(level) {
    var filterText: CharSequence? = null

    override fun getType() = R.id.item_type_entry

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        with(holder.itemView) {
            entryTitle.text = entry.title
            if (entry.hasExpired) {
                entryTitle.paintFlags = entryTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                entryTitle.paintFlags = entryTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            filterTextView.text = filterText
            filterTextView.isVisible = !filterText.isNullOrEmpty()
            groupIcon.setImageByteArray(entry.iconData)
        }
    }

    fun filter(constraint: String?, matcher: EntryMatcher): Boolean {
        val result = matcher.matches(entry, constraint)
        if (result is MatchResult.Success) {
            filterText = generateMatchText(result, constraint!!.length)
            return false
        }
        if (result is MatchResult.All) {
            filterText = null
            return false
        }
        filterText = null
        return true
    }

    private fun generateMatchText(result: MatchResult.Success,
            length: Int): SpannableStringBuilder {
        val text = when (result.matchType) {
            MatchType.Title -> entry.title
            MatchType.Url -> entry.url
            MatchType.UserName -> entry.username
            MatchType.Password -> entry.password
            MatchType.Notes -> entry.notes
            MatchType.Property -> result.matchedProperty!!.value
            MatchType.Tags -> TODO()
            MatchType.GroupName -> TODO()
        }

        val title = when (result.matchType) {
            MatchType.Title -> "Title"
            MatchType.Url -> "Url"
            MatchType.UserName -> "User name"
            MatchType.Password -> "Password"
            MatchType.Notes -> "Notes"
            MatchType.Property -> result.matchedProperty!!.key
            MatchType.Tags -> "Tags"
            MatchType.GroupName -> "Group name"
        }

        return SpannableStringBuilder(text).apply {
            setSpan(StyleSpan(Typeface.BOLD), result.startIndex, result.startIndex + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }.insert(0, "$title: ")
    }
}
