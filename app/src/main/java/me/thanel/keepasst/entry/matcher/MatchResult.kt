package me.thanel.keepasst.entry.matcher

import de.slackspace.openkeepass.domain.Property

sealed class MatchResult {
    class Success(
            val startIndex: Int,
            val matchType: MatchType
    ) : MatchResult() {
        var matchedProperty: Property? = null
            get() = if (matchType != MatchType.Property) {
                throw IllegalStateException("Property can only be accessed for property match type")
            } else {
                field
            }

        constructor(startIndex: Int, matchedProperty: Property) :
                this(startIndex, MatchType.Property) {
            this.matchedProperty = matchedProperty
        }
    }

    class All : MatchResult()
    class Failure : MatchResult()
}