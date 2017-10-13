@file:Suppress("ClassName")

package me.thanel.keepasst.entry.matcher

import de.slackspace.openkeepass.domain.Entry
import de.slackspace.openkeepass.domain.EntryBuilder
import de.slackspace.openkeepass.domain.GroupBuilder
import de.slackspace.openkeepass.domain.KeePassFileBuilder
import de.slackspace.openkeepass.domain.MetaBuilder
import de.slackspace.openkeepass.domain.Property
import de.slackspace.openkeepass.domain.TimesBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class EntryMatcherTest {
    private val trashEntry = EntryBuilder("inTrashEntry").build()
    private val topEntry = EntryBuilder("topEntry").build()
    private val recycleBin = GroupBuilder("Recycle Bin")
            .addEntry(trashEntry)
            .build()
    private val database = KeePassFileBuilder("TestDatabase.kdbx")
            .addTopEntries(topEntry)
            .addTopGroups(recycleBin)
            .withMeta(MetaBuilder("TestDatabase.kdbx")
                    .recycleBinEnabled(true)
                    .recycleBinUuid(recycleBin.uuid)
                    .build())
            .build()

    @Test
    fun `Should mach entry by title`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByTitle = true
            val enabledResult = matches(database, entry, "title")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.Title)

            filterByTitle = false
            val disabledResult = matches(database, entry, "title")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match entry by URL`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByUrl = true
            val enabledResult = matches(database, entry, "url")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.Url)

            filterByUrl = false
            val disabledResult = matches(database, entry, "url")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match entry by user name`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByUserName = true
            val enabledResult = matches(database, entry, "username")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.UserName)

            filterByUserName = false
            val disabledResult = matches(database, entry, "username")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match entry by password`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByPassword = true
            val enabledResult = matches(database, entry, "password")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.Password)

            filterByPassword = false
            val disabledResult = matches(database, entry, "password")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match entry by notes`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByNotes = true
            val enabledResult = matches(database, entry, "notes")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.Notes)

            filterByNotes = false
            val disabledResult = matches(database, entry, "notes")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match entry by custom property`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByProperties = true
            val enabledResult = matches(database, entry, "property")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.Property)
            assertEquals(entry.customProperties[0], enabledResult.matchedProperty)

            filterByProperties = false
            val disabledResult = matches(database, entry, "property")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match entry by custom protected property`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByProperties = true
            val enabledResult = matches(database, entry, "protected")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.Property)
            assertEquals(entry.customProperties[1], enabledResult.matchedProperty)

            filterByProperties = false
            val disabledResult = matches(database, entry, "protected")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match all entries given empty constraint`() {
        with(buildMatcher()) {
            assertTrue(matches(database, buildEntry(), null) is MatchResult.All)
            assertTrue(matches(database, buildEntry(), "") is MatchResult.All)
            assertTrue(matches(database, buildExpiredEntry(), null) is MatchResult.All)
            assertTrue(matches(database, buildExpiredEntry(), "") is MatchResult.All)
        }
    }

    @Test
    fun `Should match expired entries`() {
        with(buildMatcher()) {
            excludeExpired = true
            assertTrue(matches(database, buildExpiredEntry(), "title") is MatchResult.Failure)

            excludeExpired = false
            assertTrue(matches(database, buildExpiredEntry(), "title") is MatchResult.Success)
        }
    }

    @Test
    fun `Should respect case-sensitive setting`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            caseSensitive = true
            assertTrue(matches(database, entry, "title") is MatchResult.Success)
            assertTrue(matches(database, entry, "tiTle") is MatchResult.Failure)

            caseSensitive = false
            assertTrue(matches(database, entry, "title") is MatchResult.Success)
            assertTrue(matches(database, entry, "tiTle") is MatchResult.Success)
        }
    }

    @Test
    fun `Should respect in-trash search setting`() {
        with(buildMatcher()) {
            searchInRecycleBin = true
            assertTrue(matches(database, trashEntry, "inTrash") is MatchResult.Success)
            searchInRecycleBin = false
            assertTrue(matches(database, trashEntry, "inTrash") is MatchResult.Failure)
        }
    }

    private fun buildMatcher() = EntryMatcher()

    private fun buildEntry() = EntryBuilder("title")
            .url("url")
            .username("username")
            .password("password")
            .notes("notes")
            .apply { customPropertyList.add(Property("p1", "property", false)) }
            .apply { customPropertyList.add(Property("p2", "protected", true)) }
            .build()

    private fun buildExpiredEntry(): Entry = EntryBuilder(buildEntry())
            .times(TimesBuilder()
                    .expires(true)
                    .expiryTime(Calendar.getInstance().apply {
                        add(Calendar.DATE, -1)
                    })
                    .build())
            .build()
}