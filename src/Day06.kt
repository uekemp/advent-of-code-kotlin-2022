


fun detect(line: String, markerCount: Int): Int {
    val chars = line.toCharArray()
    val bucket = mutableSetOf<Char>()

    for (index in 0 until chars.size - markerCount) {
        chars.copyTo(index, index + markerCount, bucket)
        if (bucket.size == markerCount) {
            return index + markerCount
        }
        bucket.clear()
    }
    return -1
}

fun CharArray.copyTo(from: Int, to: Int, chars: MutableSet<Char>) {
    for (index in from until to) {
        chars.add(this[index])
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return detect(input[0], 4)
    }

    fun part2(input: List<String>): Int {
        return detect(input[0], 14)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    println(part1(testInput))

    val input = readInput("Day06")
    check(part1(input) == 1140)
    check(part2(input) == 3495)
}
