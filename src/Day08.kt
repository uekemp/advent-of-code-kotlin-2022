

fun parseInput(input: List<String>): Array<IntArray> {
    val cols = input[0].length
    val rows = input.size
    val grid = Array(rows) { IntArray(cols) }

    input.forEachIndexed { y, line ->
        val row = grid[y]
        line.toCharArray().forEachIndexed { x, char ->
            row[x] = char.digitToInt()
        }
    }

//    grid.forEach { row ->
//        println(row.joinToString())
//    }
    return grid
}

class NoProgression(val value: Int): Iterable<Int> {

    private val iterator = object : Iterator<Int> {
        override fun hasNext() = true

        override fun next() = value
    }

    override fun iterator() = iterator
}

fun Array<IntArray>.isVisible(x0: Int, y0: Int): Boolean {
    val cols = this[0].size
    val rows = this.size
    val treeSize = this[y0][x0]
    var vLeft = true
    for (x in x0 - 1 downTo 0) {
        if (this[y0][x] >= treeSize) {
            vLeft = false
            break
        }
    }
    var vRight = true
    for (x in x0 + 1 until cols) {
        if (this[y0][x] >= treeSize) {
            vRight = false
            break
        }
    }
    var vTop = true
    for (y in y0 - 1 downTo 0) {
        if (this[y][x0] >= treeSize) {
            vTop = false
            break
        }
    }
    var vBottom = true
    for (y in y0 + 1 until rows) {
        if (this[y][x0] >= treeSize) {
            vBottom = false
            break
        }
    }

    return vLeft || vRight || vTop || vBottom
}

fun main() {
    val r1 = IntProgression.fromClosedRange(1, 1, 0)
    r1.forEach {
        println(it)
    }
    fun part1(input: List<String>): Int {
        val grid = parseInput(input)
        var count = 0
        grid.forEachIndexed { y, row ->
            row.forEachIndexed { x, tree ->
                if (grid.isVisible(x, y)) {
                    count++
                }
            }
        }
        return count
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)

    val input = readInput("Day08")
    check(part1(input) == 1798)
    println(part2(input))
}
