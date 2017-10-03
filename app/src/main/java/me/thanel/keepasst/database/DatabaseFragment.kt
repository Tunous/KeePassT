package me.thanel.keepasst.database

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.ViewHolder
import de.slackspace.openkeepass.domain.Group
import kotlinx.android.synthetic.main.fragment_database.*
import me.thanel.keepasst.R
import me.thanel.keepasst.base.BaseFragment
import me.thanel.keepasst.entry.EntryActivity

class DatabaseFragment : BaseFragment() {
    private val groupAdapter = GroupAdapter<ViewHolder>()

    override val layoutResId = R.layout.fragment_database

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val database = database ?: return

        groupAdapter.setOnItemClickListener { item, _ ->
            if (item is EntryItem) {
                startActivity(EntryActivity.newIntent(context, item.entry.uuid))
            }
        }

        databaseRecyclerView.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        groupAdapter.clear()

        @Suppress("LoopToCallChain")
        for (topGroup in database.topGroups) {
            groupAdapter.add(addGroup(topGroup))
        }
        @Suppress("LoopToCallChain")
        for (topEntry in database.topEntries) {
            groupAdapter.add(EntryItem(topEntry, 0))
        }
    }

    private fun addGroup(group: Group, level: Int = 0): NestedGroup {
        val headerItem = HeaderItem(group, level)
        val expandableGroup = ExpandableGroup(headerItem)

        @Suppress("LoopToCallChain")
        for (subGroup in group.groups) {
            expandableGroup.add(addGroup(subGroup, level + 1))
        }
        @Suppress("LoopToCallChain")
        for (entry in group.entries) {
            expandableGroup.add(EntryItem(entry, level + 1))
        }

        return expandableGroup
    }
}
