package com.oxy.kotlin.collections.node

sealed class NodeList<out E> {
    data object Nil : NodeList<Nothing>()
    data class Cons<out E>(
        val head: E,
        val tail: NodeList<E> = Nil
    ) : NodeList<E>()
}

fun <E> NodeList<E>.remove(element: E): NodeList<E> {
    return when (this) {
        NodeList.Nil -> this
        is NodeList.Cons -> {
            if (head == element) {
                tail
            } else {
                NodeList.Cons(head, tail.remove(element))
            }
        }
    }
}

tailrec fun <E> NodeList<E>.forEach(action: (E) -> Unit) {
    when (this) {
        NodeList.Nil -> return
        is NodeList.Cons -> {
            action(head)
            tail.forEach(action)
        }
    }
}

fun <E> NodeList<E>.forEachReversed(action: (E) -> Unit) {
    val deque = ArrayDeque<E>()
    forEach { deque.addLast(it) }
    while (deque.isNotEmpty()) action(deque.removeLast())
}

inline fun <reified T> NodeList<*>.loopIn(crossinline action: (T) -> Unit) {
    forEach {
        if (it is T) action(it)
    }
}

fun <E> singlenode(element: E): NodeList.Cons<E> {
    return NodeList.Cons(element)
}

operator fun <E> NodeList<E>.plus(element: E): NodeList<E> {
    return NodeList.Cons(element, this)
}

fun <E> NodeList<E>.reduce(operation: (E, E) -> E): E {
    if (this !is NodeList.Cons) throw UnsupportedOperationException("Empty NodeList can't be reduced.")
    return fold(head, operation)
}

fun <E> NodeList<E>.fold(initial: E, operation: (E, E) -> E): E {
    if (this !is NodeList.Cons) return initial
    return fold(operation(initial, head), operation)
}

operator fun <E> NodeList<E>.plus(another: NodeList<E>): NodeList<E> {
    var accumulator: NodeList<E> = this
    another.forEachReversed {
        accumulator += it
    }
    return accumulator
}

operator fun <E> NodeList<E>.minus(element: E): NodeList<E> {
    return this.remove(element)
}

fun <E> Iterator<E>.toNodeList(): NodeList<E> {
    var accumulator: NodeList<E> = NodeList.Nil
    while (hasNext()) {
        accumulator += next()
    }
    return accumulator
}

fun <E> Iterable<E>.toNodeList(): NodeList<E> {
    return iterator().toNodeList()
}
