
@Suppress("NOTHING_TO_INLINE")
inline fun Coordinate(x: Int, y: Int) = Coordinate(packInts(x, y))

@JvmInline
value class Coordinate(private val packedValue: Long) {

    val x: Int
        get() = (packedValue shr 32).toInt()

    val y: Int
        get() = (packedValue and 0xFFFFFFFF).toInt()

    operator fun component1(): Int = x

    operator fun component2(): Int = y

    fun copy(x: Int = this.x, y: Int = this.y) = Coordinate(x, y)

    fun move(dx : Int = 0, dy: Int = 0) = Coordinate(x + dx, y + dy)

    fun move(coordinate: Coordinate) = move(coordinate.x, coordinate.y)

    override fun toString(): String {
        return "[$x, $y]"
    }

    companion object {

        val Zero = Coordinate(0, 0)
    }
}

fun packInts(x: Int, y: Int): Long {
    return (x.toLong() shl 32) or (y.toLong() and 0xFFFFFFFF)
}
