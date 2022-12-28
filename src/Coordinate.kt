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

    operator fun rangeTo(other: Coordinate): Iterator<Coordinate> {
        return if (this.x == other.x) {
            if (this.y < other.y) {
                (this.y..other.y).map { y -> Coordinate(this.x, y) }.iterator()
            } else {
                (this.y downTo other.y).map { y -> Coordinate(this.x, y) }.iterator()
            }
        } else if (this.y == other.y) {
            if (this.x < other.x) {
                (this.x..other.x).map { x -> Coordinate(x, this.y) }.iterator()
            } else {
                (this.x downTo other.x).map { x -> Coordinate(x, this.y) }.iterator()
            }
        } else {
            throw IllegalArgumentException("For a coordinate range at least one dimension must be identical $this, $other")
        }
    }

    fun copy(x: Int = this.x, y: Int = this.y) = Coordinate(x, y)

    fun moveBy(dx : Int = 0, dy: Int = 0) = Coordinate(x + dx, y + dy)

    fun moveBy(coordinate: Coordinate) = moveBy(coordinate.x, coordinate.y)

    override fun toString(): String {
        return "Coordinate [$x, $y]"
    }

    companion object {

        val Zero = Coordinate(0, 0)
    }
}

fun packInts(x: Int, y: Int): Long {
    return (x.toLong() shl 32) or (y.toLong() and 0xFFFFFFFF)
}
