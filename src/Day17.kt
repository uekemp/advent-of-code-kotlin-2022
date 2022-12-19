
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

    var index: Int = 0

    fun next(): Int {
        return patterns[index++].also {
            if (index >= patterns.size) {
                index = 0
            }
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

    var nextRockIndex = 0

    private val settledRocks = mutableListOf<Rock>()

    fun newRock(): Rock {
        val sprite = Sprites[nextRockIndex++].also {
            if (nextRockIndex >= Sprites.size) {
                nextRockIndex = 0
            }
        }

        return Rock(sprite, Coordinate(2, bounds.height + 3))
    }

    fun letRockFall(rock: Rock) {
        var current = rock
        while (true) {
            val (pushed, _) = tryMovement(current, dx = jet.next())
            val (next, moved) = tryMovement(pushed, dy = -1)
            if (!moved) {
                settledRocks.add(pushed)
                val newHeight = settledRocks.maxOfOrNull { r -> r.position.y + r.bounds.height }!!
                bounds = bounds.copy(height = newHeight)
                break
            } else {
                current = next
            }
        }
    }

    private fun tryMovement(current: Rock, dx: Int = 0, dy: Int = 0): Pair<Rock, Boolean> {
        val candidate = current.move(dx, dy)
        val rockBounds = candidate.bounds

        // Check left/right and bottom
        if ((rockBounds.position.x < 0) || (rockBounds.position.x + rockBounds.width > 7) || (rockBounds.position.y < 0)) {
            return current to false
        }

        // Check intersection with settled rocks
        if (candidate.intersects(bounds)) {
            settledRocks.forEach { settled ->
                if (settled.intersects(candidate)) {
                    return current to false
                }
            }
        }
        return candidate to true
    }

    /**
     * Construct a string identifying the upper last 20 rows of the chamber.
     */
    fun gameState(): String {
        val printRows = 20
        val text = Array(printRows) { CharArray(bounds.width) { '.' } }
        val upper = bounds.height - printRows
        for (i in settledRocks.size - 1 downTo 0) {
            val rock = settledRocks[i]
            if (rock.position.y < upper) {
                break
            } else {
                rock.coordinates().forEach { c -> text[c.y - upper][c.x] = '#' }
            }
        }
        return buildString {
            text.forEach { line ->
                line.forEach { c -> append(c) }
            }
        }
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

class GameState(val rockCount: Long, val height: Int, val jetIndex: Int, val rockIndex: Int, val top: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState

        if (jetIndex != other.jetIndex) return false
        if (rockIndex != other.rockIndex) return false
        if (top != other.top) return false

        return true
    }

    override fun hashCode(): Int {
        var result = jetIndex
        result = 31 * result + rockIndex
        result = 31 * result + top.hashCode()
        return result
    }

    override fun toString(): String {
        return "GameState jetIndex=$jetIndex, rockIndex=$rockIndex"
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val max = 2022
        val chamber = Chamber(JetPattern(input[0]));
        for (i in 1..max) {
            chamber.letRockFall(chamber.newRock())
        }

        return chamber.bounds.height
    }

    fun part2(input: List<String>): Long {
        val max = 1_000_000_000_000
        val jet = JetPattern(input[0])
        val chamber = Chamber(jet);
        val states = mutableSetOf<GameState>()
        var computedHeight = 0L

        for (i in 1..max) {
            chamber.letRockFall(chamber.newRock())
            val currentState = GameState(i, chamber.bounds.height, jet.index, chamber.nextRockIndex, chamber.gameState())
            if (states.contains(currentState)) {
                // Found a sequence, now we can estimate the rest:
                val lastState = states.first { s -> s == currentState }
                val sequenceHeight = currentState.height - lastState.height
                val sequenceLength = currentState.rockCount - lastState.rockCount
                val repeat = (max - i) / sequenceLength
                val remaining = (max -i) % sequenceLength
                computedHeight = chamber.bounds.height + (sequenceHeight * repeat)
                computedHeight += chamber.bounds.height.let { initialHeight ->
                    repeat(remaining.toInt()) {
                        chamber.letRockFall(chamber.newRock())
                    }
                    chamber.bounds.height - initialHeight
                }
                break
            } else {
                states.add(currentState)
            }
        }
        return computedHeight
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 3068)
    check(part2(testInput) == 1514285714288)

    val input = readInput("Day17")
    check(part1(input) == 3175)
    check(part2(input) == 1555113636385)
}
