package me.thanel.keepasst.util

import de.slackspace.openkeepass.domain.Entry
import de.slackspace.openkeepass.domain.Group
import de.slackspace.openkeepass.domain.KeePassFile
import java.util.*

val Entry.hasExpired: Boolean
    get() = times.expires() && times.expiryTime.before(Calendar.getInstance())

fun Group.containsEntry(id: UUID): Boolean =
    entries.any { it.uuid == id } ||
            groups.any { it.containsEntry(id) }

val KeePassFile.recycleBin: Group?
    get() {
        return if (meta.recycleBinEnabled) {
            getGroupByUUID(meta.recycleBinUuid)
        } else {
            null
        }
    }
