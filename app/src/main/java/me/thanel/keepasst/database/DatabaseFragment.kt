package me.thanel.keepasst.database

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import de.slackspace.openkeepass.domain.Entry
import de.slackspace.openkeepass.domain.Group
import kotlinx.android.synthetic.main.fragment_database.*
import me.thanel.keepasst.R
import me.thanel.keepasst.base.BaseFragment
import me.thanel.keepasst.entry.EntryActivity

class DatabaseFragment : BaseFragment() {
    private val groupAdapter = FastItemAdapter<BaseEntryItem>()
    private val flatAdapter = FastItemAdapter<BaseEntryItem>()

    override val layoutResId = R.layout.fragment_database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val database = database ?: return

        setupClickListeners()

        flatAdapter.itemFilter.withFilterPredicate { item, constraint ->
            constraint != null && item is FilterableItem && item.filter(constraint.toString())
        }

        databaseRecyclerView.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        groupAdapter.clear()
        flatAdapter.clear()

        @Suppress("LoopToCallChain")
        for (topGroup in database.topGroups) {
            groupAdapter.add(addGroup(topGroup))
        }
        @Suppress("LoopToCallChain")
        for (topEntry in database.topEntries) {
            groupAdapter.add(EntryItem(topEntry, 0))
            flatAdapter.add(EntryItem(topEntry, 0))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_database, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                databaseRecyclerView.adapter = flatAdapter
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                databaseRecyclerView.adapter = groupAdapter
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                flatAdapter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                flatAdapter.filter(newText)
                return true
            }
        })
    }

    private fun setupClickListeners() {
        val clickListener = FastAdapter.OnClickListener<BaseEntryItem> { _, _, item, _ ->
            when (item) {
                is EntryItem -> openEntry(item.entry)
                else -> return@OnClickListener false
            }
            return@OnClickListener true
        }

        groupAdapter.withOnClickListener(clickListener)
        flatAdapter.withOnClickListener(clickListener)
    }

    private fun addGroup(group: Group, level: Int = 0): BaseEntryItem {
        val headerItem = GroupItem(group, level)
        val subItems = mutableListOf<BaseEntryItem>()

        @Suppress("LoopToCallChain")
        for (subGroup in group.groups) {
            subItems.add(addGroup(subGroup, level + 1))
        }
        @Suppress("LoopToCallChain")
        for (entry in group.entries) {
            subItems.add(EntryItem(entry, level + 1))
            flatAdapter.add(EntryItem(entry, 0))
        }

        headerItem.withSubItems(subItems)
        return headerItem
    }

    private fun openEntry(entry: Entry) {
        startActivity(EntryActivity.newIntent(context, entry.uuid))
    }
}
