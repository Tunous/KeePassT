@file:Suppress("ClassName")

package me.thanel.keepasst.entry.matcher

import de.slackspace.openkeepass.domain.Entry
import de.slackspace.openkeepass.domain.EntryBuilder
import de.slackspace.openkeepass.domain.Property
import de.slackspace.openkeepass.domain.TimesBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class EntryMatcherTest {
    @Test
    fun `Should mach entry by title`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByTitle = true
            val enabledResult = matches(entry, "title")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.Title)

            filterByTitle = false
            val disabledResult = matches(entry, "title")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match entry by URL`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByUrl = true
            val enabledResult = matches(entry, "url")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.Url)

            filterByUrl = false
            val disabledResult = matches(entry, "url")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match entry by user name`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByUserName = true
            val enabledResult = matches(entry, "username")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.UserName)

            filterByUserName = false
            val disabledResult = matches(entry, "username")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match entry by password`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByPassword = true
            val enabledResult = matches(entry, "password")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.Password)

            filterByPassword = false
            val disabledResult = matches(entry, "password")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match entry by notes`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByNotes = true
            val enabledResult = matches(entry, "notes")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.Notes)

            filterByNotes = false
            val disabledResult = matches(entry, "notes")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match entry by custom property`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByProperties = true
            val enabledResult = matches(entry, "property")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.Property)
            assertEquals(entry.customProperties[0], enabledResult.matchedProperty)

            filterByProperties = false
            val disabledResult = matches(entry, "property")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match entry by custom protected property`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            filterByProperties = true
            val enabledResult = matches(entry, "protected")
            assertTrue(enabledResult is MatchResult.Success)
            assertTrue((enabledResult as MatchResult.Success).matchType == MatchType.Property)
            assertEquals(entry.customProperties[1], enabledResult.matchedProperty)

            filterByProperties = false
            val disabledResult = matches(entry, "protected")
            assertTrue(disabledResult is MatchResult.Failure)
        }
    }

    @Test
    fun `Should match all entries given empty constraint`() {
        with(buildMatcher()) {
            assertTrue(matches(buildEntry(), null) is MatchResult.All)
            assertTrue(matches(buildEntry(), "") is MatchResult.All)
            assertTrue(matches(buildExpiredEntry(), null) is MatchResult.All)
            assertTrue(matches(buildExpiredEntry(), "") is MatchResult.All)
        }
    }

    @Test
    fun `Should match expired entries`() {
        with(buildMatcher()) {
            excludeExpired = true
            assertTrue(matches(buildExpiredEntry(), "title") is MatchResult.Failure)

            excludeExpired = false
            assertTrue(matches(buildExpiredEntry(), "title") is MatchResult.Success)
        }
    }

    @Test
    fun `Should respect case-sensitive setting`() {
        with(buildMatcher()) {
            val entry = buildEntry()
            caseSensitive = true
            assertTrue(matches(entry, "title") is MatchResult.Success)
            assertTrue(matches(entry, "tiTle") is MatchResult.Failure)

            caseSensitive = false
            assertTrue(matches(entry, "title") is MatchResult.Success)
            assertTrue(matches(entry, "tiTle") is MatchResult.Success)
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