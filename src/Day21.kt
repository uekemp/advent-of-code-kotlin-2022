
sealed class MonkeyWithJob

data class YellingMonkey(var number: Long): MonkeyWithJob()

data class ComputingMonkey(val operation: ((Long, Long) -> Long), val first: String, val second: String): MonkeyWithJob()

typealias Monkeys = Map<String, MonkeyWithJob>

fun readMonkeys(input: List<String>): Monkeys {
    return input.associate { line ->
        val name = line.substringBefore(":")
        val secondPart = line.substringAfter(":").trim()
        val number = secondPart.toLongOrNull()

        if (number == null) {
            val (first, op, second) = secondPart.split(" ")
            val operation: (Long, Long) -> Long = when (op) {
                "+" -> { x, y -> x + y }
                "-" -> { x, y -> x - y }
                "*" -> { x, y -> x * y }
                "/" -> { x, y -> x / y }
                else -> error("Unexpected operation: $op")
            }
            name to ComputingMonkey(operation, first, second)
        } else {
            name to YellingMonkey(number)
        }
    }
}

fun Monkeys.resultOf(name: String, visitor: ((String) -> Unit)? = null): Long {
    visitor?.invoke(name)

    return when (val monkey = this[name]) {
        is YellingMonkey -> {
            monkey.number
        }
        is ComputingMonkey -> {
            monkey.operation(resultOf(monkey.first, visitor), resultOf(monkey.second, visitor))
        }
        else -> error("Missing monkey: '$name'")
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        return readMonkeys(input).resultOf("root")
    }

    fun part2(input: List<String>): Int {
        val monkeys = readMonkeys(input)
        (monkeys["humn"] as YellingMonkey).number = 301L
        val root = monkeys["root"] as ComputingMonkey
        val involvedMonkeys = mutableListOf<MonkeyWithJob>()
        val result1 = monkeys.resultOf(root.first) { name ->  involvedMonkeys.add(monkeys[name]!!)  }
        val result2 = monkeys.resultOf(root.second) { name -> if (name == "humn") { println("second contains me") } }

        println(involvedMonkeys.size)
        println("$result1 = $result2")
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 152L)
    part2(testInput)

    val input = readInput("Day21")
    check(part1(input) == 152479825094094L)
    println(part2(input))
}
