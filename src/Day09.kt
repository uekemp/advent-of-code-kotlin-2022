import kotlin.math.absoluteValue
import kotlin.math.sign

data class Motion(var dx: Int, var dy: Int) {

    fun isDone(): Boolean {
        return dx == 0 && dy == 0
    }

    fun move(knot: Knot) {
        if (isDone()) {
            error("Motion is exhausted")
        }
        val sx = dx.sign
        val sy = dy.sign
        knot.move(sx, sy)
        dx -= sx
        dy -= sy
    }

    companion object {
        fun from(line: String): Motion {
            val (dir, steps) = line.split(" ")
            return when (dir) {
                "R" -> { Motion(steps.toInt(), 0) }
                "L" -> { Motion(-steps.toInt(), 0) }
                "U" -> { Motion(0, steps.toInt()) }
                "D" -> { Motion(0, -steps.toInt()) }
                else -> error("Invalid input: $line")
            }
        }
    }
}

data class Knot(val name: String, var x: Int = 0, var y: Int = 0) {

    fun move(dx: Int, dy: Int) {
        x += dx
        y += dy
    }

    fun position(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun drag(other: Knot): Boolean {
        if (!isTouching(other)) {
            val (dx, dy) = distanceTo(other)
            if ((dx.absoluteValue > 1) && (dy.absoluteValue > 1)) {
                other.position(x + dx.sign, y + dy.sign)
            } else if (dx.absoluteValue > 1) {
                other.position(x + dx.sign, y)
            } else if (dy.absoluteValue > 1) {
                other.position(x, y + dy.sign)
            }
            return true
        }
        return false
    }

    fun isTouching(other: Knot): Boolean {
        return other.x in x-1..x+1 && other.y in y-1..y+1
    }

    fun rememberMe(visitedPoints: MutableSet<String>) {
        visitedPoints.add("$x,$y")
    }

    fun distanceTo(other: Knot): Pair<Int, Int> {
        return (other.x - this.x) to (other.y - this.y)
    }
}

fun parseMotions(input: List<String>): List<Motion> {
    val result = mutableListOf<Motion>()
    input.forEach {
        result.add(Motion.from(it))
    }
    return result
}

fun main() {
    fun part1(input: List<String>): Int {
        val motions = parseMotions(input)
        val visited = HashSet<String>()
        val head = Knot("head")
        val tail = Knot("tail")
        tail.rememberMe(visited)
        motions.forEach { motion ->
            while (!motion.isDone()) {
                motion.move(head)
                if (head.drag(tail)) {
                    tail.rememberMe(visited)
                }
            }
        }
        return visited.size
    }

    fun part2(input: List<String>): Int {
        val motions = parseMotions(input)
        val visited = HashSet<String>()
        val knots = listOf(Knot("head"), Knot("1"), Knot("2"), Knot("3"), Knot("4"), Knot("5"), Knot("6"), Knot("7"), Knot("8"), Knot("9"))
        knots.last().rememberMe(visited)
        motions.forEach { motion ->
            while (!motion.isDone()) {
                motion.move(knots.first())
                for (i in 1 until knots.size) {
                    knots[i - 1].drag(knots[i])
                }
                knots.last().rememberMe(visited)
            }
        }
        return visited.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
//    check(part1(testInput) == 13)
    check(part2(testInput) == 36)

    val input = readInput("Day09")
    check(part1(input) == 6311)
    check(part2(input) == 2482)
}
