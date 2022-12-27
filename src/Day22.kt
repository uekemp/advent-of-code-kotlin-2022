
enum class Facing(val value: Int) {

    RIGHT(0) {
        override fun isHorizontal() = true

        override fun move(c: Coordinate) = c.moveBy(dx = 1)

        override fun rotate(turn: Char): Facing {
            return when(turn) {
                'R' -> DOWN
                'L' -> UP
                else -> error("Unknown turn: $turn")
            }
        }
    },

    LEFT(2) {
        override fun isHorizontal() = true

        override fun move(c: Coordinate) = c.moveBy(dx = -1)

        override fun rotate(turn: Char): Facing {
            return when(turn) {
                'R' -> UP
                'L' -> DOWN
                else -> error("Unknown turn: $turn")
            }
        }
    },

    DOWN(1) {
        override fun isHorizontal() = false

        override fun move(c: Coordinate) = c.moveBy(dy = 1)

        override fun rotate(turn: Char): Facing {
            return when(turn) {
                'R' -> LEFT
                'L' -> RIGHT
                else -> error("Unknown turn: $turn")
            }
        }
    },

    UP(3) {
        override fun isHorizontal() = false

        override fun move(c: Coordinate) = c.moveBy(dy = -1)

        override fun rotate(turn: Char): Facing {
            return when(turn) {
                'R' -> RIGHT
                'L' -> LEFT
                else -> error("Unknown turn: $turn")
            }
        }
    };

    abstract fun isHorizontal(): Boolean

    abstract fun move(c: Coordinate): Coordinate

    abstract fun rotate(turn: Char): Facing
}

enum class TileType(val char: Char) {

    INVALID(' '),
    ROCK('#'),
    OPEN('.');

    override fun toString(): String {
        return char.toString()
    }
}

class BoardMap(private val rows: List<String>, private val commands: String) {

    private var facing = Facing.RIGHT

    private var position = findStart()

    private var commandIndex = 0

    private fun typeOf(c: Coordinate): TileType {
        val rowIndex = c.y - 1
        if (rowIndex >= rows.size || rowIndex < 0) {
            return TileType.INVALID
        }
        val row = rows[rowIndex]
        val colIndex = c.x - 1
        if (colIndex >= row.length || colIndex < 0) {
            return TileType.INVALID
        }
        return when (row[colIndex]) {
            TileType.INVALID.char -> TileType.INVALID
            TileType.ROCK.char -> TileType.ROCK
            TileType.OPEN.char -> TileType.OPEN
            else -> error("Unknown tile type: ${row[colIndex]}")
        }
    }

    private fun findStart(): Coordinate {
        for (x in 1..rows[0].length) {
            val c = Coordinate(x, 1)
            if (typeOf(c) == TileType.OPEN) {
                return c
            }
        }
        error("Cannot find start")
    }

    fun walk(maxSteps: Int = Int.MAX_VALUE) {
        println("Start position is $position")
        var currentStep = 0
        val rotationCommands = charArrayOf('R', 'L')

        while (commandIndex < commands.length) {
            if (rotationCommands.contains(commands[commandIndex])) {
                facing = facing.rotate(commands[commandIndex])
                commandIndex++
            } else {
                val nextRotationIndex = commands.indexOfAny(rotationCommands, commandIndex).let {
                    if (it == -1) commands.length else it
                }
                val number = commands.substring(commandIndex, nextRotationIndex).toInt()
                commandIndex = nextRotationIndex

                for (i in 1..number) {
                    if (nextValidTileFor(position) == TileType.ROCK) {
                        break
                    }
                }
            }
            if (currentStep++ >= maxSteps) {
                break
            }
        }
    }

    /**
     * Returns either OPEN (in which case the position is moved) or ROCK (in which case the position is not moved).
     */
    private fun nextValidTileFor(start: Coordinate): TileType {
        val nextPosition = moveWrapped(start)

        return when(typeOf(nextPosition)) {
            TileType.OPEN -> {
                position = nextPosition
                TileType.OPEN
            }
            TileType.ROCK -> {
                TileType.ROCK
            }
            TileType.INVALID -> {
                nextValidTileFor(nextPosition)
            }
        }
    }

    private fun moveWrapped(coordinate: Coordinate): Coordinate {
        val next = facing.move(coordinate)
        val wrapped = if (facing.isHorizontal()) {
            if (next.x <= 0) {
                next.copy(x = rows[next.y].length)
            } else if (next.x > rows[next.y - 1].length) {
                next.copy(x = 1)
            } else {
                next
            }
        } else {
            if (next.y <= 0) {
                next.copy(y = rows.size)
            } else if (next.y > rows.size) {
                next.copy(y = 1)
            } else {
                next
            }
        }
        // Move until we are back on the map
        if (typeOf(wrapped) == TileType.INVALID) {
            moveWrapped(wrapped)
        }
        return wrapped
    }

    fun computeResult(): Long {
        return (position.y * 1000L) + (position.x * 4L) + facing.value
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        val emptyLineIndex = input.indexOf("")
        val board = BoardMap(input.subList(0, emptyLineIndex), input[emptyLineIndex + 1])
        board.walk()
        return board.computeResult()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check(part1(testInput) == 6032L)

    val input = readInput("Day22")
    check(part1(input) == 131052L)
//    println(part2(input))
}
