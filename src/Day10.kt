
class Cpu(var clock: Int = 1, var x: Int = 1) {

    var sum = 0

    fun tick(increment: Int = 0) {
        clock++
        x += increment
        if ((clock - 20) % 40 == 0) {
            sum += clock * x
        }
    }
}

class Crt(val cpu: Cpu) {

    private val screen = Array(6) { CharArray(40) }

    fun drawPixel() {
        val pixel = (cpu.clock % 240) - 1
        val row = pixel / 40
        val column = if (pixel % 40 >= 0) pixel % 40 else 39
        val char = if (column in cpu.x - 1..cpu.x + 1) {
            '#'
        } else {
            '.'
        }
        screen[row][column] = char
    }

    override fun toString(): String {
        return screen.joinToString(separator = "\n", transform = { array -> array.joinToString(separator = "") })
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val cpu = Cpu()
        input.forEach { line ->
            if (line == "noop") {
                cpu.tick()
            } else {
                val (_, text) = line.split(" ")
                val inc = text.toInt()
                cpu.tick()
                cpu.tick(inc)
            }
        }

        return cpu.sum
    }

    fun part2(input: List<String>): Crt {
        val cpu = Cpu()
        val crt = Crt(cpu)
        input.forEach { line ->
            if (line == "noop") {
                crt.drawPixel()
                cpu.tick()
            } else {
                val (_, text) = line.split(" ")
                val inc = text.toInt()
                crt.drawPixel()
                cpu.tick()
                crt.drawPixel()
                cpu.tick(inc)
            }
        }

        return crt
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)

    println(part2(testInput))
    println("---------------------------------------------------------------")

    val input = readInput("Day10")
    check(part1(input) == 15680)
    println(part2(input))
}
