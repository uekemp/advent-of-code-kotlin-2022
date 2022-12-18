
data class RockSprite(val name: String, val coordinates: List<Coordinate>) {

    val width = coordinates.map { c -> c.x }.distinct().size

    val height = coordinates.map { c -> c.y }.distinct().size

    override fun toString(): String {
        return "RockSprite '$name', width=$width, height=$height"
    }
}

val Sprites = listOf(
    RockSprite("-", listOf(Coordinate(0, 0), Coordinate(1, 0), Coordinate(2, 0), Coordinate(3, 0))),
    RockSprite("+", listOf(Coordinate(1, 0), Coordinate(0, 1), Coordinate(1, 1), Coordinate(2, 1), Coordinate(1, 2))),
    RockSprite("L", listOf(Coordinate(0, 0), Coordinate(1, 0), Coordinate(2, 0), Coordinate(2, 1), Coordinate(2, 2))),
    RockSprite("|", listOf(Coordinate(0, 0), Coordinate(0, 1), Coordinate(0, 2), Coordinate(0, 3))),
    RockSprite("o", listOf(Coordinate(0, 0), Coordinate(1, 0), Coordinate(0, 1), Coordinate(1, 1))),
)

class Rock(private val sprite: RockSprite, position: Coordinate) {

    var bounds = Rect(position, sprite.width, sprite.height)

    val position: Coordinate
        get() = bounds.position

    fun intersects(other: Rock): Boolean {
        if (bounds.intersects(other.bounds)) {
            val coordinates = coordinates()
            coordinates.retainAll(other.coordinates())
            return coordinates.isNotEmpty()
        }
        return false
    }

    fun intersects(other: Rect): Boolean {
        return this.bounds.intersects(other)
    }

    fun move(dx : Int = 0, dy: Int = 0): Rock {
        return Rock(sprite, position.move(dx, dy))
    }

    fun coordinates(): MutableList<Coordinate> {
        return sprite.coordinates.map { c -> c.move(position) }.toMutableList()
    }

    override fun toString(): String {
        return "Rock $sprite at $position"
    }
}

class JetPattern(text: String) {

    private val patterns = text.trim().toCharArray().map { c -> charToMovement(c) }

    private var index: Int = 0

    val size: Int
        get() = patterns.size

    fun next(): Int {
        return if (index < patterns.size) {
            patterns[index++]
        } else {
            index = 1
            patterns[0]
        }
    }

    private fun charToMovement(char: Char): Int {
        return when (char) {
            '<' -> { -1 }
            '>' -> { 1 }
            else -> { error("Unexpected jet pattern: '$char'") }
        }
    }
}

class Chamber(private val jet: JetPattern) {

    var bounds = Rect(Coordinate(0, 0), 7, 0)

    private val settledRocks = mutableListOf<Rock>()

    private var nextRockIndex = 0

    private var rockCount = 0L

    fun newRock(): Rock {
        val sprite = if (nextRockIndex < Sprites.size) {
            Sprites[nextRockIndex++]
        } else {
            nextRockIndex = 1
            Sprites[0]
        }
        val x = 2
        val y = bounds.height + 3
        rockCount++
        if (rockCount % 100_000 == 0L) {
            println("Created rock $rockCount")
        }
        return Rock(sprite, Coordinate(x, y))
    }

    fun letRockFall(rock: Rock) {
        var current = rock
        while (true) {
            val dx = jet.next()
            if (isAllowedToMoveTo(current.move(dx = dx))) {
                current = current.move(dx = dx)
            }
            val candidate = current.move(dy = -1)
            if (!isAllowedToMoveTo(candidate)) {
                settledRocks.add(current)
                val overallHeight = settledRocks.maxOfOrNull { r -> r.position.y + r.bounds.height }!!
                bounds = bounds.copy(height = overallHeight)
                break
            } else {
                current = candidate
            }
        }
    }

    private fun isAllowedToMoveTo(rock: Rock): Boolean {
        val rockBounds = rock.bounds

        // Check left/right and bottom
        if ((rockBounds.position.x < 0) || (rockBounds.position.x + rockBounds.width > 7) || (rockBounds.position.y < 0)) {
            return false
        }

        // Check intersection with settled rocks
        if (rock.intersects(bounds)) {
            settledRocks.forEach { settled ->
                if (settled.intersects(rock)) {
                    return false
                }
            }
        }
        return true
    }

    override fun toString(): String {
        val text = Array(bounds.height) { CharArray(bounds.width) { '.' } }
        settledRocks.forEach { rock ->
            rock.coordinates().forEach { c -> text[c.y][c.x] = '#' }
        }
        text.reverse()
        return buildString {
            text.forEach { line ->
                line.forEach { c -> append(c) }
                append("\n")
            }
        }
    }
}

data class GameState(val jetIndex: Int, val rockIndex: Int, val top: String)

fun main() {
    fun part1(input: List<String>): Int {
        val chamber = Chamber(JetPattern(input[0]));
        for (i in 1..2022) {
            chamber.letRockFall(chamber.newRock())
        }

        return chamber.bounds.height
    }

    fun part2(input: List<String>): Long {
        val jet = JetPattern(input[0])
        var chamber = Chamber(jet);
//        println("-------> ${1_000_000_000_000 % max}")
        return 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    val result = part1(testInput)
    check(result == 3068)
    val result2 = part2(testInput)
    println(part2(testInput))
    check(result2 == 1514285714288)

    val input = readInput("Day17")
    check(part1(input) == 3175)
    println(part2(input))
}
