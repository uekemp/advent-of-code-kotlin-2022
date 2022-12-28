
typealias Snafu = String

private fun Snafu.toInt(): Int {
    val chars = toCharArray()
    chars.reverse()
    var f = 1
    return chars.sumOf { c ->
        val result = f * snafuToInt(c)
        f *= 5
        result
    }
}

fun snafuToInt(digit: Char): Int {
    return when(digit) {
        '2' -> 2
        '1' -> 1
        '0' -> 0
        '-' -> -1
        '=' -> -2
        else -> error("Unknown SNAFU digit: $digit")
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line -> line.toInt() }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    check(part1(testInput) == 4890)

    val input = readInput("Day25")
    println(part1(input))
    println(part2(input))
}
