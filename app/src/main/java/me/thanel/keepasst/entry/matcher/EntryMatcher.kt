package me.thanel.keepasst.entry.matcher

import de.slackspace.openkeepass.domain.Entry
import de.slackspace.openkeepass.domain.KeePassFile
import me.thanel.keepasst.util.containsEntry
import me.thanel.keepasst.util.hasExpired
import me.thanel.keepasst.util.recycleBin

class EntryMatcher {
    var filterByTitle: Boolean = true
    var filterByUrl: Boolean = true
    var filterByUserName: Boolean = true
    var filterByPassword: Boolean = false
    var filterByNotes: Boolean = true
    var filterByProperties: Boolean = true
    var filterByTags: Boolean = true
    var filterByGroupName: Boolean = false

    var matchByRegex: Boolean = false
    var caseSensitive: Boolean = false
    var excludeExpired: Boolean = false
    var searchInRecycleBin: Boolean = true

    fun matches(database: KeePassFile, entry: Entry, constraint: String?): MatchResult {
        if (excludeExpired && entry.hasExpired) {
            return MatchResult.Failure()
        }

        if (!searchInRecycleBin) {
            val recycleBin = database.recycleBin
            if (recycleBin != null && recycleBin.containsEntry(entry.uuid)) {
                return MatchResult.Failure()
            }
        }

        if (constraint == null || constraint.isEmpty()) {
            return MatchResult.All()
        }

        if (filterByTitle) {
            val index = entry.title?.indexOf(constraint, ignoreCase = !caseSensitive) ?: -1
            if (index >= 0) {
                return MatchResult.Success(index, MatchType.Title)
            }
        }

        if (filterByUrl) {
            val index = entry.url?.indexOf(constraint, ignoreCase = !caseSensitive) ?: -1
            if (index >= 0) {
                return MatchResult.Success(index, MatchType.Url)
            }
        }

        if (filterByUserName) {
            val index = entry.username?.indexOf(constraint, ignoreCase = !caseSensitive) ?: -1
            if (index >= 0) {
                return MatchResult.Success(index, MatchType.UserName)
            }
        }

        if (filterByPassword) {
            val index = entry.password?.indexOf(constraint, ignoreCase = !caseSensitive) ?: -1
            if (index >= 0) {
                return MatchResult.Success(index, MatchType.Password)
            }
        }

        if (filterByNotes) {
            val index = entry.notes?.indexOf(constraint, ignoreCase = !caseSensitive) ?: -1
            if (index >= 0) {
                return MatchResult.Success(index, MatchType.Notes)
            }
        }

        if (filterByProperties) {
            for (customProperty in entry.customProperties) {
                val index = customProperty.value?.indexOf(constraint,
                        ignoreCase = !caseSensitive) ?: -1
                if (index >= 0) {
                    return MatchResult.Success(index, customProperty)
                }
            }
        }

        return MatchResult.Failure()
    }
}