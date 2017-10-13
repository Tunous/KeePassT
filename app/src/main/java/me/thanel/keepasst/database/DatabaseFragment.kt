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
import me.thanel.keepasst.entry.matcher.EntryMatcher

class DatabaseFragment : BaseFragment() {
    private val groupAdapter = FastItemAdapter<BaseEntryItem>()
    private val flatAdapter = FastItemAdapter<BaseEntryItem>()
    private val entryMatcher = EntryMatcher()
    private var searchText: String? = null

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
            item is EntryItem && item.filter(constraint?.toString(), entryMatcher)
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
        inflater.inflate(R.menu.advanced_search, menu)

        val lockItem = menu.findItem(R.id.action_lock)
        val filtersItem = menu.findItem(R.id.search_filters)
        val optionsItem = menu.findItem(R.id.search_options)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                databaseRecyclerView.adapter = flatAdapter
                lockItem.isVisible = false
                filtersItem.isVisible = true
                optionsItem.isVisible = true
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                databaseRecyclerView.adapter = groupAdapter
                lockItem.isVisible = true
                filtersItem.isVisible = false
                optionsItem.isVisible = false
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                onFilter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                onFilter(newText)
                return true
            }
        })
    }

    private fun onFilter(query: String?) {
        if (query.isNullOrEmpty()) {
            flatAdapter.adapterItems
                    .filterIsInstance<EntryItem>()
                    .forEach { it.filterText = null }
        }
        searchText = query
        flatAdapter.filter(query)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.isCheckable) {
            item.isChecked = !item.isChecked
        }

        when (item.itemId) {
            R.id.filter_title -> entryMatcher.filterByTitle = item.isChecked
            R.id.filter_url -> entryMatcher.filterByUrl = item.isChecked
            R.id.filter_username -> entryMatcher.filterByUserName = item.isChecked
            R.id.filter_password -> entryMatcher.filterByPassword = item.isChecked
            R.id.filter_notes -> entryMatcher.filterByNotes = item.isChecked
            R.id.filter_properties -> entryMatcher.filterByProperties = item.isChecked
            R.id.filter_tags -> entryMatcher.filterByTags = item.isChecked
            R.id.filter_group_name -> entryMatcher.filterByGroupName = item.isChecked

            R.id.option_regex -> entryMatcher.matchByRegex = item.isChecked
            R.id.option_case_sensitive -> entryMatcher.caseSensitive = item.isChecked
            R.id.option_exclude_expired -> entryMatcher.excludeExpired = item.isChecked

            else -> return super.onOptionsItemSelected(item)
        }

        flatAdapter.filter(searchText)

        return false
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
