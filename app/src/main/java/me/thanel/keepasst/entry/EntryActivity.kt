package me.thanel.keepasst.entry

import android.content.Context
import android.content.Intent
import me.thanel.keepasst.base.BaseActivity
import java.util.*

class EntryActivity : BaseActivity() {
    override val requireDatabase = true

    override fun createFragment() = EntryFragment.newInstance(
            intent.getSerializableExtra(EXTRA_ENTRY_ID) as UUID)

    override fun getSupportParentActivityIntent(): Intent? {
        return super.getSupportParentActivityIntent()
                ?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }

    companion object {
        private const val EXTRA_ENTRY_ID = "entry_id"

        fun newIntent(context: Context, entryId: UUID): Intent =
            Intent(context, EntryActivity::class.java)
                    .putExtra(EXTRA_ENTRY_ID, entryId)
    }
}
