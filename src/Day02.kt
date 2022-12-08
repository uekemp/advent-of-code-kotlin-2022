
@JvmInline
value class RockPaperScissors(val points: Int) {

    fun playWith(opponent: RockPaperScissors): Int {
        return if (points == opponent.points) {
            3 + points
        } else if ((points - opponent.points % 3) == 1) {
            6 + points
        } else {
            points
        }
    }

    fun looserPoints(): Int {
        val looser = points - 1
        return if (looser > 0) looser else 3
    }

    fun winnerPoints(): Int {
        val winner = points + 1
        return if (winner <= 3) winner else 1
    }

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
                'X' -> opponent.looserPoints()
                'Y' -> opponent.points + 3
                'Z' -> opponent.winnerPoints() + 6
                else -> error("Wrong input")
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
