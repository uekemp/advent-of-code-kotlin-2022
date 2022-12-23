sealed class MonkeyWithJob

data class YellingMonkey(var number: Long) : MonkeyWithJob()

class ComputingMonkey(
    private val operation: String,
    val left: String,
    val right: String
) : MonkeyWithJob() {

    val compute: ((Long, Long) -> Long) = when(operation) {
        "+" -> { x, y -> x + y }
        "-" -> { x, y -> x - y }
        "*" -> { x, y -> x * y }
        "/" -> { x, y -> x / y }
        else -> error("Unknown operation: $operation")
    }

    fun invertLeftKnown(result: Long, left: Long): Long {
        val right = when(operation) {
            "+" -> result - left
            "-" -> -result + left
            "*" -> result / left
            "/" -> result / left
            else -> error("Unknown operation: $operation")
        }
        checkInversion(result, left, right)
        return right
    }

    fun invertRightKnown(result: Long, right: Long): Long {
        val left = when(operation) {
            "+" -> result - right
            "-" -> result + right
            "*" -> result / right
            "/" -> result * right
            else -> error("Unknown operation: $operation")
        }
        checkInversion(result, left, right)
        return left
    }

    private fun checkInversion(result: Long, left: Long, right: Long) {
        when (operation) {
            "+" -> check(left + right == result) { "$left + $right != $result" }
            "-" -> check(left - right == result) { "$left - $right != $result" }
            "*" -> check(left * right == result) { "$left * $right != $result" }
            "/" -> check(left / right == result) { "$left / $right != $result" }
        }
    }

    override fun toString(): String {
        return "ComputingMonkey ($left $operation $right)"
    }
}

private typealias MonkeyMap = Map<String, MonkeyWithJob>

fun monkeyMapOf(input: List<String>): MonkeyMap {
    return input.associate { line ->
        val name = line.substringBefore(":")
        val secondPart = line.substringAfter(":").trim()
        val number = secondPart.toLongOrNull()

        if (number == null) {
            val (left, op, right) = secondPart.split(" ")
            name to ComputingMonkey(op, left, right)
        } else {
            name to YellingMonkey(number)
        }
    }
}

private fun MonkeyMap.resultFor(name: String, visitor: ((String) -> Unit)? = null): Long {
    visitor?.invoke(name)

    return when (val monkey = this[name]!!) {
        is YellingMonkey -> monkey.number
        is ComputingMonkey -> monkey.compute(resultFor(monkey.left, visitor), resultFor(monkey.right, visitor))
    }
}

private fun MonkeyMap.containsHuman(name: String): Pair<Boolean, Long> {
    var isHuman = false
    val result = resultFor(name) { if (it == "humn") isHuman = true }
    return isHuman to result
}

private fun MonkeyMap.computeHumanFor(expected: Long, name: String) {
    when (val monkey = this[name]!!) {
        is YellingMonkey -> monkey.number
        is ComputingMonkey -> {
            if (monkey.left == "humn") {
                human.number = monkey.invertRightKnown(expected, resultFor(monkey.right))
            } else if (monkey.right == "humn") {
                human.number = monkey.invertLeftKnown(expected, resultFor(monkey.left))
            } else {
                val (leftContainsHuman, leftResult) = containsHuman(monkey.left)
                val (rightContainsHuman, rightResult) = containsHuman(monkey.right)

                if (leftContainsHuman) {
                    computeHumanFor(monkey.invertRightKnown(expected, rightResult), monkey.left)
                } else if (rightContainsHuman) {
                    computeHumanFor(monkey.invertLeftKnown(expected, leftResult), monkey.right)
                } else {
                    error("Unexpected result")
                }
            }
        }
    }
}

private val MonkeyMap.human: YellingMonkey
    get() = this["humn"] as YellingMonkey


fun main() {
    fun part1(input: List<String>): Long {
        return monkeyMapOf(input).resultFor("root")
    }

    fun part2(input: List<String>): Long {
        val monkeys = monkeyMapOf(input)
        val root = monkeys["root"] as ComputingMonkey
        val (leftContainsHuman, leftResult) = monkeys.containsHuman(root.left)
        val (rightContainsHuman, rightResult) = monkeys.containsHuman(root.right)

        if (leftContainsHuman && rightContainsHuman) {
            error("Too difficult, both branches are variable")
        } else if (leftContainsHuman) {
            monkeys.computeHumanFor(rightResult, root.left)
        } else if (rightContainsHuman) {
            monkeys.computeHumanFor(leftResult, root.right)
        } else {
            error("No branch contains a human")
        }

        return monkeys.human.number
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 152L)

    val input = readInput("Day21")
    check(part1(input) == 152479825094094L)
    check(part2(input) == 3360561285172L)
}
