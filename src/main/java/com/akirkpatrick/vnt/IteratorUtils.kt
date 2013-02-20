package com.akirkpatrick.vnt

class MappingIterator<T, K>(val inner : Iterator<T>, val f : (T) -> K) : Iterator<K> {
    public override fun next(): K {
        return f(inner.next())
    }
    public override fun hasNext(): Boolean {
        return inner.hasNext()
    }

}

class EmptyIterator<T> : Iterator<T> {
    public override fun next(): T {
        throw UnsupportedOperationException()
    }
    public override fun hasNext(): Boolean {
        return false
    }
}