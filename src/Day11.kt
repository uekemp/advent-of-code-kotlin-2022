import kotlin.math.floor

data class Monkey(
    val id: Int,
    val items: ArrayDeque<Long>,
    val operation: (Long) -> Long,
    val divideByThree: Boolean,
    val divisor: Int,
    val nextIdTrue: Int,
    val nextIdFalse: Int
) {

    var inspections = 0L

    var correctionMod = 0L

    fun accept(throwItem: ThrowItem) {
        if (throwItem.id != id) {
            error("Got wrong throw")
        }
        items.addLast(throwItem.worryLevel)
    }

    fun inspectItems(): List<ThrowItem> {
        val result = mutableListOf<ThrowItem>()
        val iterator = items.iterator()

        while (iterator.hasNext()) {
            var worryLevel = iterator.next()
            inspections++
            iterator.remove()
            worryLevel = operation(worryLevel)
            if (correctionMod > 0) worryLevel %= correctionMod
            if (divideByThree) {
                worryLevel = floor(worryLevel / 3F).toLong()
            }
            val test = worryLevel % divisor == 0L
            if (test) {
                result.add(ThrowItem(nextIdTrue, worryLevel))
            } else {
                result.add(ThrowItem(nextIdFalse, worryLevel))
            }
        }
        return result
    }

    override fun toString(): String {
        return "items=$items, inspections=$inspections"
    }
}

data class ThrowItem(val id: Int, val worryLevel: Long)

fun parseMonkeys(input: List<String>, divideByThree: Boolean): Map<Int, Monkey> {
    val monkeys = HashMap<Int, Monkey>()
    val iterator = input.iterator()
    val monkeyId = """Monkey ([0-9]+):""".toRegex()

    while (iterator.hasNext()) {
        val line = iterator.next()
        val matchResult = monkeyId.find(line)
        if (matchResult != null) {
            val id = matchResult.groups[1]!!.value.toInt()
            val itemList = iterator.next().split(":")[1].split(",")
            val items = ArrayDeque(itemList.map { it.trim().toLong() }.toList())
            val operationText = iterator.next().split("=")[1].trim()
            val operation = if (operationText.contains("+")) {
                val arg = operationText.substring(operationText.indexOf("+") + 1).trim().toInt()
                val func: (Long) -> Long = { arg + it }
                func
            } else if (operationText.contains("*")) {
                val arg = operationText.substring(operationText.indexOf("*") + 1).trim()
                if (arg == "old") {
                    val func: (Long) -> Long = { it * it }
                    func
                } else {
                    val func: (Long) -> Long = { arg.toLong() * it }
                    func
                }
            } else {
                error("Cannot parse operation: '$operationText'")
            }
            val divisor = iterator.next().split("by")[1].trim().toInt()
            val trueMonkey = iterator.next().split("monkey")[1].trim().toInt()
            val falseMonkey = iterator.next().split("monkey")[1].trim().toInt()

//            println("id=$id, items=$items, operation=$operation, divisor=$divisor, true=$trueMonkey, false=$falseMonkey")
            monkeys[id] = Monkey(id, items, operation, divideByThree, divisor, trueMonkey, falseMonkey)
        }
    }

    return monkeys
}

fun playOneRound(ids: List<Int>, game: Map<Int, Monkey>) {
    for (id in ids) {
        val monkey = game[id]
        val items = monkey!!.inspectItems()
        for (item in items) {
            game[item.id]!!.accept(item)
        }
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        val game = parseMonkeys(input, true)
        val ids = game.keys.toList().sorted()
        repeat(20) {
            playOneRound(ids, game)
        }
//        println(game)
        val sorted = game.values.sortedByDescending { monkey -> monkey.inspections }
        return sorted[0].inspections * sorted[1].inspections
    }

    fun part2(input: List<String>): Long {
        val game = parseMonkeys(input, false)
        val mod = game.values.map { monkey -> monkey.divisor }.reduce { acc, i ->  acc * i }.toLong()
        game.values.forEach{ monkey -> monkey.correctionMod = mod }
        val ids = game.keys.toList().sorted()
        repeat(10_000) {
            playOneRound(ids, game)
        }
//        println(game)
        val sorted = game.values.sortedByDescending { monkey -> monkey.inspections }
        return sorted[0].inspections * sorted[1].inspections
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605L)
    check(part2(testInput) == 2713310158L)

    val input = readInput("Day11")
    check(part1(input) == 118674L)
    check(part2(input) == 32333418600L)
}
