

val Char.priority: Int
    get() {
        return when (this) {
            in 'a'..'z' -> this - 'a' + 1
            in 'A'..'Z' -> this - 'A' + 27
            else -> error("Unknown priority for $this")
        }
    }

fun findCompartmentDuplicates(rucksack: String): Set<Char> {
    val duplicates = mutableSetOf<Char>()
    val half = rucksack.length / 2
    for (index in 0 until half) {
        val char = rucksack[index]
        if ((rucksack.indexOf(char, half) > 0) && (!duplicates.contains(char))) {
            duplicates.add(char)
        }
    }
    return duplicates
}

fun findBadge(rucksack: String, vararg rucksacks: String): Char {
    val badges = rucksack.toCharArray().toMutableSet()
    for (r in rucksacks) {
        badges.retainAll(r.toCharArray().toSet())
    }
    if (badges.isEmpty() || badges.size > 1) {
        error("Too many badges: $badges")
    }
    return badges.first()
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            findCompartmentDuplicates(line).sumOf { char -> char.priority }
        }
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3).sumOf { chunk ->
            findBadge(chunk[0], chunk[1], chunk[2]).priority
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)

    val input = readInput("Day03")
    check(part1(input) == 7746)
    check(part2(input) == 2604)
}
