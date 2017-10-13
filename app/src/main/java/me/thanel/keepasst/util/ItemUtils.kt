package me.thanel.keepasst.util

import de.slackspace.openkeepass.domain.Entry
import java.util.*

val Entry.hasExpired: Boolean
    get() = times.expires() && times.expiryTime.before(Calendar.getInstance())
