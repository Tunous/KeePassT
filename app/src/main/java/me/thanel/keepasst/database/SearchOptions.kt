package me.thanel.keepasst.database

data class SearchOptions(
        var filterByTitle: Boolean = true,
        var filterByUrl: Boolean = true,
        var filterByUsername: Boolean = true,
        var filterByPassword: Boolean = false,
        var filterByNotes: Boolean = true,
        var filterByProperties: Boolean = true,
        var filterByTags: Boolean = true,
        var filterByGroupName: Boolean = false,

        var matchByRegex: Boolean = false,
        var caseSensitive: Boolean = false,
        var excludeExpired: Boolean = false
)