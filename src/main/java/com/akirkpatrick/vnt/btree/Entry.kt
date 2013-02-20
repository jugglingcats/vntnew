package com.akirkpatrick.vnt.btree

import java.util.Arrays
import java.util.Comparator

public trait Entry<T : Comparable<T>> : Comparable<Entry<T>> {
    val parent : Entry<T>?
    fun setAsRoot() : Unit
    fun reparent(parent : Entry<T>) : Unit
    fun addKey(value : T) : Unit
    public val keyCount: Int
    public val childCount: Int
    val order : Int
    val sort : Boolean
    public fun getKey(index : Int) : T
    public fun getChild(index : Int) : Entry<T>
    fun addChild(child : Entry<T>) : Boolean
    fun removeChild(child : Entry<T>) : Int
    fun removeChild(index : Int) : Entry<T>
    fun indexOf(value : T) : Int
    fun indexOf(value : Entry<T>) : Int
    fun removeKey(value : T) : Int
    fun removeKey(index : Int) : T
    fun replaceKey(index : Int, value : T)
    fun replaceChild(index : Int, value : Entry<T>)
}

