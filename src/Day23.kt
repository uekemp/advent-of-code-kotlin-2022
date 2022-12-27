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

    fun move(): Elf {
        return if (proposedPosition != null) {
            copy(position = proposedPosition!!, proposedPosition = null)
        } else {
            this
        }
    }
}

class Grove(var elfs: List<Elf>) {

    private val directions = ArrayDeque<Direction>(4)

    private val elfPositions = HashSet<Coordinate>()

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

    private fun isOccupied(coordinate: Coordinate) = elfPositions.contains(coordinate)

    private fun isOccupied(coordinates: List<Coordinate>): Boolean {
        return elfPositions.any { coordinates.contains(it) }
    }

    fun move(): Boolean {
        rememberPositions()

        // First half:
        val newPositions = mutableMapOf<Coordinate, MutableList<Elf>>()
        elfs.forEach { elf ->
            if (isOccupied(elf.surroundingPositions)) {
                for (direction in directions) {
                    if (!isOccupied(direction.coordinatesFor(elf.position))) {
                        val newPosition = elf.position.move(dx = direction.dx, dy = direction.dy)
//                        println("New position for $elf is $newPosition")

                        newPositions[newPosition]?.add(elf) ?: run {
                            elf.proposedPosition = newPosition
                            newPositions[newPosition] = mutableListOf(elf)
                        }
                        break
                    }
                }
            }
        }
        nextDirection()

        // Second half:
        newPositions.values.forEach { elfs ->
            if (elfs.size > 1) {
                elfs.forEach { elf -> elf.proposedPosition = null }
            }
        }
        val elfsToMove = elfs.count { elf -> elf.proposedPosition != null }
        println("Elfs to move: $elfsToMove, grove=${width}x$height")
        if (elfsToMove == 0) {
            return false
        } else {
            elfs = elfs.map { elf -> elf.move() }.toList()
            return true
        }
    }

    fun countEmptyTiles(): Int {
        var count = 0
        for (x in elfs.minOf { elf -> elf.position.x }..elfs.maxOf { elf -> elf.position.x }) {
            for (y in elfs.minOf { elf -> elf.position.y }..elfs.maxOf { elf -> elf.position.y }) {
                if (!isOccupied(Coordinate(x, y))) {
                    count++
                }
            }
        }

        return count
    }

    override fun toString(): String {
        return buildString {

        }
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
            return Grove(elfs)
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

    fun coordinatesFor(coordinate: Coordinate): List<Coordinate> {
        return listOf(coordinate.move(dx, dy), coordinate.move(altX1, altY1), coordinate.move(altX2, altY2))
    }
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
            println("Round: $count")
        }
        return count
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 110)

    val input = readInput("Day23")
    check(part1(input) == 3864)
    check(part2(input) == 946)
}
