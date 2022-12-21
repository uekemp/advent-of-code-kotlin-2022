import kotlin.system.measureTimeMillis

typealias EncryptedList = ArrayDeque<ListItem>

// Using ArrayList instead of ArrayDeque consumes about the same time
//typealias EncryptedList = ArrayList<ListItem>

// Using LinkedList instead of ArrayDeque is roughly 4 times slower for part2!
// typealias EncryptedList = LinkedList<ListItem>

data class ListItem(val value: Long, val position: Int) {

    override fun toString(): String {
        return value.toString()
    }
}

fun EncryptedList.moveItemAt(index: Int) {
    val item = this[index]

    if (item.value != 0L) {
        val insertAt = index + item.value

        removeAt(index)
        if (insertAt > 0) {
            add((insertAt % size).toInt(), item)
        } else if (insertAt < 0) {
            add((size + (insertAt % size)).toInt(), item)
        } else {
            add(size, item)
        }
    }
}

fun EncryptedList.computeResult(): Long {
    val indexOfZero = indexOfFirst { item -> item.value == 0L }
    val i1 = (indexOfZero + 1000) % size
    val i2 = (indexOfZero + 2000) % size
    val i3 = (indexOfZero + 3000) % size
    val sum = this[i1].value + this[i2].value + this[i3].value
    println("${this[i1]} + ${this[i2]} + ${this[i3]} = $sum")
    return sum
}

fun parseEncryptedData(input: List<String>, decryptionKey: Int = 1): EncryptedList {
    return EncryptedList(input.mapIndexed { index, line -> ListItem(line.toLong() * decryptionKey, index) }.toList())
}

fun main() {
    fun part1(input: List<String>): Long {
        val list = parseEncryptedData(input)
        for (index in 0 until list.size) {
            list.moveItemAt(list.indexOfFirst { item -> item.position == index })
        }

        return list.computeResult()
    }

    fun part2(input: List<String>): Long {
        val list = parseEncryptedData(input, 811589153)

        repeat(10) {
            for (index in 0 until list.size) {
                list.moveItemAt(list.indexOfFirst { item -> item.position == index })
            }
        }

        return list.computeResult()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 3L)

    val input = readInput("Day20")
    check(part1(input) == 9945L)

    val d = measureTimeMillis {
        check(part2(input) == 3338877775442L)
    }
    println("\nDuration for part2=${d}ms")
}
