

enum class CaveType(val char: Char) {
    OPEN('.'),
    ROCK('#'),
    SAND('o')
}

class Cave {

    val data = mutableMapOf<Coordinate, CaveType>()

    private val left: Int
        get() = data.keys.minOf { c -> c.x }

    private val right: Int
        get() = data.keys.maxOf { c -> c.x }

    private val top: Int
        get() = data.keys.minOf { c -> c.y }

    private val bottom: Int
        get() = data.keys.maxOf { c -> c.y }

    private var lowestRock = Int.MIN_VALUE   // Used for part 1

    private var floor = Int.MAX_VALUE        // Used for part 2

    operator fun get(c: Coordinate): CaveType {
        return if (c.y >= floor) {
            CaveType.ROCK
        } else {
            data[c] ?: CaveType.OPEN
        }
    }

    operator fun set(c: Coordinate, type: CaveType) {
        data[c] = type
    }

    private fun isBlocked(c: Coordinate) = this[c] != CaveType.OPEN

    fun addRocks(coordinates: List<Coordinate>) {
        coordinates.zipWithNext { start, end ->
            for (rockCoordinate in start..end) {
                this[rockCoordinate] = CaveType.ROCK

                if (rockCoordinate.y > lowestRock) {
                    lowestRock = rockCoordinate.y
                    floor = lowestRock + 2
                }
            }
        }
    }

    fun nextPart1(): Boolean {
        var sand = Coordinate(500, 0)

        while (sand.y < lowestRock) {
            val next = tryToMove(sand)
            if (next == sand) {
                break
            } else {
                sand = next
            }
        }

        return if (sand.y >= lowestRock) {
            false
        } else {
            this[sand] = CaveType.SAND
            true
        }
    }

    fun nextPart2(): Boolean {
        var sand = Coordinate(500, 0)

        if (isBlocked(sand)) {
            return false
        }

        while (true) {
            val next = tryToMove(sand)
            if (next == sand) {
                this[sand] = CaveType.SAND
                break
            } else {
                sand = next
            }
        }
        return true
    }

    private fun tryToMove(sand: Coordinate): Coordinate {
        val down = sand.moveBy(dy = 1)
        return if (!isBlocked(down)) {
            down
        } else {
            val left = sand.moveBy(dx = -1, dy = 1)
            if (!isBlocked(left)) {
                left
            } else {
                val right = sand.moveBy(dx = 1, dy = 1)
                if (!isBlocked(right)) {
                    right
                } else {
                    sand
                }
            }
        }
    }

    override fun toString(): String {
        return buildString {
            for (y in top .. bottom) {
                for (x in left..right) {
                    append(this@Cave[Coordinate(x, y)].char)
                }
                appendLine()
            }
        }
    }

    companion object {

        fun of(input: List<String>): Cave {
            return Cave().apply {
                input.forEach { line ->
                    val rocks = line.split(" -> ")
                        .map { text -> text.split(",") }
                        .map { numbers -> Coordinate(numbers[0].toInt(), numbers[1].toInt()) }
                    addRocks(rocks)
                }
            }
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return with(Cave.of(input)) {
            var count = 0
            while (nextPart1()) {
                count++
            }
            count
        }
    }

    fun part2(input: List<String>): Int {
        return with(Cave.of(input)) {
            var count = 0
            while (nextPart2()) {
                count++
            }
            count
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)

    val input = readInput("Day14")
    check(part1(input) == 672)
    check(part2(input) == 26831)
}
