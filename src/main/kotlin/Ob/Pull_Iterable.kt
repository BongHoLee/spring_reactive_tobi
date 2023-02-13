package Ob

fun main() {
    createAnonymousClassOfIterable()
}

private fun createAnonymousClassOfIterable() {
    val anonymousIterable = Iterable {
        object : Iterator<Int> {
            var i = 0
            val MAX = 10
            override fun hasNext() = i < MAX

            override fun next() = ++i
        }
    }

    for (i in anonymousIterable) {
        println(i)
    }
}

private fun justList() {
    val list: Iterable<Int> = listOf(1, 2, 3, 4, 5)
    for (i in list) {
        println(i)
    }
}

class Ob
