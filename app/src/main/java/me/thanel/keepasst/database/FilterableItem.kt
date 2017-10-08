package me.thanel.keepasst.database

interface FilterableItem {
    fun filter(constraint: String?, options: SearchOptions): Boolean
}