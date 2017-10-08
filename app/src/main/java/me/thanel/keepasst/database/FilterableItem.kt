package me.thanel.keepasst.database

interface FilterableItem {
    fun filter(constraint: String?): Boolean
}