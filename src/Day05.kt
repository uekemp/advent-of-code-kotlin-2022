

class Depot {

    private val stacks = mutableListOf<ArrayDeque<Char>>()

    private val moves = mutableListOf<Move>()

    fun setup(stackCount: Int) {
        repeat(stackCount) {
            stacks.add(ArrayDeque())
        }
    }

    fun add(stackNumber: Int, crate: Char) = stacks[stackNumber - 1].addLast(crate)

    fun add(move: Move) = moves.add(move)

    fun applyMoves(move: Depot.(Move) -> Unit) {
        moves.forEach {
            move(it)
        }
    }

    fun moveOneByOne(move: Move) {
        repeat(move.amount) {
            stacks[move.destination - 1].addLast(stacks[move.source - 1].removeLast())
        }
    }

    fun moveAll(move: Move) {
        val moving = mutableListOf<Char>()
        repeat(move.amount) {
            moving.add(stacks[move.source - 1].removeLast())
        }
        moving.reverse()
        stacks[move.destination - 1].addAll(moving)
    }

    fun result(): String {
        return buildString {
            stacks.forEach { append(it.last()) }
        }
    }
}

class Move(val amount: Int, val source: Int, val destination: Int) {

    companion object {

        private val MoveParser = """move ([0-9]+) from ([0-9]+) to ([0-9]+)""".toRegex()

        fun from(line: String): Move {
            val result = MoveParser.find(line) ?: error("Invalid input")
            return Move(result.groups[1]!!.value.toInt(), result.groups[2]!!.value.toInt(), result.groups[3]!!.value.toInt())
        }
    }
}


fun parseDepot(input: List<String>): Depot {
    val depot = Depot()
    val depotLines = mutableListOf<String>()
    val (initial, moves) = input.partition { line -> !line.startsWith("move") }

    for (line in initial) {
        if (line.startsWith(" 1")) {
            val count = line.trim().last().digitToInt()
            depot.setup(count)
            depotLines.reverse()
            depotLines.forEach { depotLine ->
                for (index in 1..count) {
                    val pos = 1 + ((index - 1) * 4)
                    if (pos < depotLine.length) {
                        val char = depotLine[pos]
                        if (char != ' ') depot.add(index, char)
                    }
                }
            }
        } else if (line.isNotBlank()) {
            depotLines.add(line)
        }
    }

    moves.forEach { line ->
        depot.add(Move.from(line))
    }

    return depot
}

fun main() {
    fun part1(input: List<String>): String {
        val depot = parseDepot(input)
        depot.applyMoves(Depot::moveOneByOne)
        return depot.result()
    }

    fun part2(input: List<String>): String {
        val depot = parseDepot(input)
        depot.applyMoves(Depot::moveAll)
        return depot.result()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")

    val input = readInput("Day05")
    check(part1(input) == "PSNRGBTFT")
    check(part2(input) == "BNTZFPMMW")
}
