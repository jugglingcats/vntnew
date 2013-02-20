package com.akirkpatrick.vnt.node

class Property(val name:String, val value:String) : Comparable<Property> {
    public override fun compareTo(other: Property): Int {
        return name.compareTo(other.name)
    }

    public fun toString() : String {
        return "[${name}]='${value}'"
    }
}