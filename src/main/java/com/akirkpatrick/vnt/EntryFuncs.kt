package com.akirkpatrick.vnt.btree

public val <T : Comparable<T>> Entry<T>.firstChild : Entry<T> get() = getChild(0)
public val <T : Comparable<T>> Entry<T>.lastChild : Entry<T> get() = getChild(childCount-1)
public val <T : Comparable<T>> Entry<T>.firstKey : T get() = getKey(0)
public val <T : Comparable<T>> Entry<T>.lastKey : T get() = getKey(keyCount-1)
public val <T : Comparable<T>> Entry<T>.hasKeys : Boolean get() = keyCount > 0
public val <T : Comparable<T>> Entry<T>.hasChildren: Boolean get() = childCount > 0

public fun <T : Comparable<T>> Entry<T>.collectKeys(start: Int, end: Int, f: (T) -> Unit) {
    for ( i in start..end ) {
        f(this.getKey(i))
    }
}
public fun <T : Comparable<T>> Entry<T>.collectChildren(start: Int, end: Int, f: (Entry<T>) -> Unit) {
    if ( !hasChildren ) {
        return
    }
    for ( i in start..end+1 ) {
        f(getChild(i))
    }
}

public fun <T : Comparable<T>> Entry<T>.eachKey(f: (T)->Unit) {
    for ( i in 0..keyCount-1 ) {
        f(getKey(i))
    }
}

public fun <T : Comparable<T>> Entry<T>.eachChild(f: (Entry<T>)->Unit) {
    for ( i in 0..childCount-1 ) {
        f(getChild(i))
    }
}

