
@JvmInline
value class RockPaperScissors(val points: Int) {

    init {
        if (points == -1) error("Invalid input: $points")
    }

    fun playWith(opponent: RockPaperScissors): Int {
        return if (points == opponent.points) {
            3 + points
        } else if ((points - opponent.points % 3) == 1) {
            6 + points
        } else {
            points
        }
    }

    fun looser() = if (points == 1) RockPaperScissors(3) else RockPaperScissors(points - 1)

    fun winner() = RockPaperScissors((points % 3) + 1)

    companion object {

        fun fromAbc(char: Char) = RockPaperScissors("ABC".indexOf(char) + 1)

        fun fromXyz(char: Char) = RockPaperScissors("XYZ".indexOf(char) + 1)
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            RockPaperScissors.fromXyz(line[2]).playWith(RockPaperScissors.fromAbc(line[0]))
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val opponent = RockPaperScissors.fromAbc(line[0])
            when (line[2]) {
                'X' -> opponent.looser().points
                'Y' -> opponent.points + 3
                'Z' -> opponent.winner().points + 6
                else -> error("Invalid input")
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)

    val input = readInput("Day02")
    check(part1(input) == 12586)
    check(part2(input) == 13193)
}
