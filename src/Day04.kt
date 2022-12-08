


data class Section(private val lower: Int, private val upper: Int) {

    init {
        if (lower > upper) {
            error("Wrong input: $lower > $upper")
        }
    }

    fun fullyContains(other: Section): Boolean {
        return this.lower >= other.lower && this.upper <= other.upper
    }

    fun overlaps(other: Section): Boolean {
        return (lower in other.lower..other.upper)  || (other.lower in lower..upper)
    }
}

private val parser = "([0-9]+)-([0-9]+)".toRegex()

fun Section(text: String): Section {
    val result = parser.find(text) ?: error("Bad input: $text")
    return Section(result.groups[1]?.value!!.toInt(), result.groups[2]?.value!!.toInt())
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.count { line ->
            val (sec1, sec2) = line.split(",")
            val s1 = Section(sec1)
            val s2 = Section(sec2)
            (s1.fullyContains(s2) || s2.fullyContains(s1))
        }
    }

    fun part2(input: List<String>): Int {
        return input.count { line ->
            val (sec1, sec2) = line.split(",")
            val s1 = Section(sec1)
            val s2 = Section(sec2)
            s1.overlaps(s2)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    check(part1(input) == 494)
    check(part2(input) == 833)
}
