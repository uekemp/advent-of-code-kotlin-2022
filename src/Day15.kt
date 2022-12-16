import kotlin.math.absoluteValue


class SensorMap(val sensors: List<Sensor>) {

    val map = mutableMapOf<Coordinate, Char>()

    init {
        sensors.forEach {sensor ->
            this[sensor.position] = SENSOR
            this[sensor.beacon] = BEACON
        }
    }

    private val left: Int
        get() = map.keys.map { c -> c.x }.min()

    private val right: Int
        get() = map.keys.map { c -> c.x }.max()

    private val top: Int
        get() = map.keys.map { c -> c.y }.min()

    private val bottom: Int
        get() = map.keys.map { c -> c.y }.max()

    operator fun get(c: Coordinate) = map[c]

    operator fun set(c: Coordinate, value: Char) {
        map[c] = value
    }

    fun getRow(row: Int): CharArray {
        return buildString {
            for (x in left..right) {
                append(this@SensorMap[Coordinate(x, row)] ?: UNDEFINED)
            }
        }.toCharArray()
    }

    fun computeCoverage(predicate: (Sensor) -> Boolean = { s -> true }): SensorMap {
        sensors.filter(predicate).forEach { sensor ->
            println("Checking coverage of $sensor")
            val distance = sensor.distanceToBeacon
            for (y in sensor.position.y - distance..sensor.position.y + distance) {
                for (x in sensor.position.x - distance..sensor.position.x + distance) {
                    val c = Coordinate(x, y)
                    if ((this[c] == null) && (sensor.distanceTo(c) <= distance)) {
                        this[c] = COVERED
                    }
                }
            }
        }
        return this
    }

    fun computeRowCoverage(row: Int) {
        sensors.forEach { sensor ->
            println("Computing coverage for row $row for $sensor")
            val x = sensor.position.x
            val distance = sensor.distanceToBeacon
            val left = distance - (sensor.position.y - row).absoluteValue
            if (left > 0) {
                for (i in x - left..x + left) {
                    val c = Coordinate(i, row)
                    if (this[c] == null) {
                        this[c] = COVERED
                    }
                }
            }
        }
    }

    override fun toString(): String {
        return buildString {
            for (y in top..bottom) {
                for (x in left..right) {
                    append(map[Coordinate(x, y)] ?: UNDEFINED)
                }
                append("\n")
            }
        }
    }

    companion object {
        const val UNDEFINED = '.'
        const val SENSOR = 'S'
        const val BEACON = 'B'
        const val COVERED = '#'
    }
}

data class Sensor(val position: Coordinate, val beacon: Coordinate) {

    val distanceToBeacon: Int
        get() = (beacon.x - position.x).absoluteValue + (beacon.y - position.y).absoluteValue

    fun distanceTo(other: Coordinate) = (position.x - other.x).absoluteValue + (position.y - other.y).absoluteValue

    override fun toString(): String {
        return "Sensor at $position, beacon: $beacon, distance=$distanceToBeacon"
    }
}

data class Coordinate(val x: Int, val y: Int)


fun parseSensorMap(input: List<String>): SensorMap {
    val regexp = """x=(-?\b[0-9]+\b), y=(-?\b[0-9]+\b)""".toRegex()
    val results = input.map { line ->
        val result = regexp.findAll(line).toList()
        val x = result[0].groups[1]!!.value.toInt()
        val y = result[0].groups[2]!!.value.toInt()
        val beaconX = result[1].groups[1]!!.value.toInt()
        val beaconY = result[1].groups[2]!!.value.toInt()
        Sensor(Coordinate(x, y), Coordinate(beaconX, beaconY))
    }

    return SensorMap(results)
}

fun main() {
    fun part1(input: List<String>, row: Int): Int {
        val map = parseSensorMap(input)
//        map.computeCoverage { sensor -> sensor.c.x == 8 && sensor.c.y == 7 }
        map.computeRowCoverage(row)
//        println(map)
        val result = map.getRow(row)
//        println(row)
        return result.count { c -> c == SensorMap.COVERED }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput, 10) == 26)

    val input = readInput("Day15")
    check(part1(input, 2000000) == 5832528)
    println(part2(input))
}
