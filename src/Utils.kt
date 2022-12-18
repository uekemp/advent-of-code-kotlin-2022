import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

data class Coordinate(val x: Int, val y: Int) {

    fun move(dx : Int = 0, dy: Int = 0): Coordinate {
        return Coordinate(x + dx, y + dy)
    }

    fun move(coordinate: Coordinate) = move(coordinate.x, coordinate.y)
}

data class Rect(val position: Coordinate, val width: Int, val height: Int) {

    fun move(dx : Int = 0, dy: Int = 0): Rect {
        return copy(position = position.move(dx, dy))
    }

    fun intersects(other: Rect): Boolean {
        return this.position.x < other.position.x + other.width
                && this.position.x + this.width > other.position.x
                && this.position.y < other.position.y + other.height
                && this.height + this.position.y > other.position.y
    }
}

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')
