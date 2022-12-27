import kotlin.system.measureTimeMillis

data class Elf(val position: Coordinate, var proposedPosition: Coordinate? = null) {

    val surroundingPositions: List<Coordinate>
        get() = buildList {
                add(Coordinate(position.x - 1, position.y + 1))
                add(Coordinate(position.x, position.y + 1))
                add(Coordinate(position.x + 1, position.y + 1))
                add(Coordinate(position.x - 1, position.y))
                add(Coordinate(position.x + 1, position.y))
                add(Coordinate(position.x - 1, position.y - 1))
                add(Coordinate(position.x, position.y - 1))
                add(Coordinate(position.x + 1, position.y - 1))
        }
}

class Grove(val elfs: MutableList<Elf>) {

    private val directions = ArrayDeque<Direction>(4)

    private val elfPositions = mutableSetOf<Coordinate>()

    init {
        directions.addAll(listOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST))
    }

    val width: Int
        get() = elfs.maxOf { elf -> elf.position.x } - elfs.minOf { elf -> elf.position.x } + 1

    val height: Int
        get() = elfs.maxOf { elf -> elf.position.y } - elfs.minOf { elf -> elf.position.y } + 1

    private fun nextDirection() {
        directions.addLast(directions.removeFirst())
    }

    private fun rememberPositions() {
        elfPositions.clear()
        elfs.forEach { elf -> elfPositions.add(elf.position) }
    }

    private fun hasElf(coordinate: Coordinate) = elfPositions.contains(coordinate)

    private fun hasElf(coordinates: List<Coordinate>): Boolean {
        // Note: varargs currently not possible with value classes!
        return elfPositions.any { coordinates.contains(it) }
    }

    private fun hasElf(c1: Coordinate, c2: Coordinate, c3: Coordinate): Boolean {
        return elfPositions.contains(c1) || elfPositions.contains(c2) || elfPositions.contains(c3)
    }

    fun move(): Boolean {
        rememberPositions()

        // First half:
        val newPositions = mutableMapOf<Coordinate, Elf>()
        elfs.filter { elf -> hasElf(elf.surroundingPositions) }
            .forEach { elf ->
            val elfPosition = elf.position
            for (direction in directions) {
                if (!hasElf(direction.coordinate1(elfPosition), direction.coordinate2(elfPosition), direction.coordinate3(elfPosition))) {
                    // There can never be more than 2 elfs competing for the same position, hence once we found a
                    // second elf, we can safely remove this position
                    val newPosition = elfPosition.moveBy(dx = direction.dx, dy = direction.dy)
                    val existing = newPositions[newPosition]
                    if (existing == null) {
                        elf.proposedPosition = newPosition
                        newPositions[newPosition] = elf
                    } else {
                        existing.proposedPosition = null
                        newPositions.remove(newPosition)
                    }
                    break
                }
            }
        }
        nextDirection()

        // Second half:
        var changeCount = 0
        elfs.forEachIndexed { index, elf ->
            elf.proposedPosition?.let { proposedPosition ->
                elfs[index] = elf.copy(position = proposedPosition, proposedPosition = null)
                changeCount++
            }
        }
        println("Elfs moved: $changeCount")
        return changeCount != 0
    }

    fun countEmptyTiles(): Int {
        var count = 0
        for (x in elfs.minOf { elf -> elf.position.x }..elfs.maxOf { elf -> elf.position.x }) {
            for (y in elfs.minOf { elf -> elf.position.y }..elfs.maxOf { elf -> elf.position.y }) {
                if (!hasElf(Coordinate(x, y))) {
                    count++
                }
            }
        }

        return count
    }

    companion object {

        fun of(input: List<String>): Grove {
            val width = input[0].length
            val height = input.size
            val elfs = buildList {
                for (y in 0 until height) {
                    val line = input[height - 1 - y]
                    for (x in 0 until width) {
                        if (line[x] == '#') {
                            add(Elf(Coordinate(x, y)))
                        }
                    }
                }
            }
            return Grove(elfs.toMutableList())
        }
    }
}

enum class Direction(
    val dx: Int,
    val dy: Int,
    private val altX1: Int,
    private val altY1: Int,
    private val altX2: Int,
    private val altY2: Int
) {
    NORTH(dx = 0, dy = 1,  altX1 = -1, altY1 = 1, altX2 = 1, altY2 = 1),
    SOUTH(dx = 0, dy = -1,  altX1 = -1, altY1 = -1, altX2 = 1, altY2 = -1),
    WEST(dx = -1, dy = 0,  altX1 = -1, altY1 = -1, altX2 = -1, altY2 = 1),
    EAST(dx = 1, dy = 0,  altX1 = 1, altY1 = -1, altX2 = 1, altY2 = 1);

    fun coordinate1(source: Coordinate) = source.moveBy(dx, dy)

    fun coordinate2(source: Coordinate) = source.moveBy(altX1, altY1)

    fun coordinate3(source: Coordinate) = source.moveBy(altX2, altY2)
}

fun main() {
    fun part1(input: List<String>): Int {
        val grove = Grove.of(input)
        repeat(10) {
            grove.move()
        }
        return grove.countEmptyTiles()
    }

    fun part2(input: List<String>): Int {
        val grove = Grove.of(input)
        var count = 1
        while (grove.move()) {
            count++
        }
        return count
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 110)

    val input = readInput("Day23")
    check(part1(input) == 3864)
    val d = measureTimeMillis { check(part2(input) == 946) }
    println("Duration: ${d}ms")
}
