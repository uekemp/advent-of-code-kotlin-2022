import java.util.*

data class HeightMap(val destination: Point, val nodes: Array<MutableList<Node>>) {

    private val width = nodes[0].size

    private val height = nodes.size

    operator fun get(point: Point): Node {
        return nodes[point.y][point.x]
    }

    operator fun get(x: Int, y: Int): Node {
        return nodes[y][x]
    }

    fun applyToAll(apply: (Node) -> Unit) {
        nodes.forEach { row ->
            row.forEach { node ->
                apply(node)
            }
        }
    }

    fun neighboursOf(point: Point): MutableList<Node> {
        val neighbours = mutableListOf<Node>()

        if ((point.x > 0) && canMoveTo(point, point.x - 1, point.y)) {
            neighbours.add(this[point.x - 1, point.y])
        }
        if ((point.x < width - 1) && canMoveTo(point, point.x + 1, point.y)) {
            neighbours.add(this[point.x + 1, point.y])
        }
        if ((point.y > 0) && canMoveTo(point, point.x, point.y - 1)) {
            neighbours.add(this[point.x, point.y - 1])
        }
        if ((point.y < height - 1) && canMoveTo(point, point.x, point.y + 1)) {
            neighbours.add(this[point.x, point.y + 1])
        }

        return neighbours
    }

    fun createShortestPath(): List<Node> {
        check(this[destination].predecessor != null) { "Shortest path not found yet" }
        val shortestPath = LinkedList<Node>()
        var current = this[destination]
        shortestPath.add(current)
        while (current.predecessor != null) {
            current = current.predecessor!!
            shortestPath.addFirst(current)
        }
        return shortestPath
    }

    private fun canMoveTo(start: Point, x: Int, y: Int) = this[start].canMoveTo(this[x, y])

    fun resetMap() {
        applyToAll {
            it.distance = Int.MAX_VALUE
            it.predecessor = null
            it.isVisited = false
        }
    }

    data class Node(
        val position: Point,
        val height: Int,
        var distance: Int = Int.MAX_VALUE,
        var predecessor: Node? = null,
        var isVisited: Boolean = false
    ) {
        fun canMoveTo(other: Node): Boolean = this.height + 1 >= other.height
    }

    data class Point(val x: Int, val y: Int)
}

fun parseHeightMap(input: List<String>): HeightMap  {
    val rows = Array(input.size) { mutableListOf<HeightMap.Node>() }
    var start: HeightMap.Point? = null
    var destination: HeightMap.Point? = null

    input.forEachIndexed { row, line ->
        line.toCharArray().forEachIndexed { col, char ->
            val position = HeightMap.Point(col, row)
            val height = if (char == 'S') {
                start = position
                'a'
            } else if (char == 'E') {
                destination = position
                'z'
            } else {
                char
            }
            rows[row].add(HeightMap.Node(position, height - 'a'))
        }
    }
    rows[start!!.y][start!!.x].distance = 0
    return HeightMap(destination!!, rows)
}

fun findShortestPath(map: HeightMap): List<HeightMap.Node> {
    val allNodes = mutableSetOf<HeightMap.Node>()
    map.applyToAll { allNodes.add(it) }

    while (map[map.destination].predecessor == null && allNodes.isNotEmpty()) {
        val current = allNodes.filter { node -> !node.isVisited }.minBy { node -> node.distance }
        val distanceToNext = current.distance + 1

        for (neighbour in map.neighboursOf(current.position)) {
            if (allNodes.contains(neighbour) && distanceToNext < neighbour.distance) {
                neighbour.distance = distanceToNext
                neighbour.predecessor = current
            }
        }
        current.isVisited = true
        allNodes.remove(current)
    }

    return map.createShortestPath()
}

fun main() {
    fun part1(input: List<String>): Int {
        val map = parseHeightMap(input)
        val shortestPath = findShortestPath(map)
//        println(shortestPath.map { node -> node.position })
        return shortestPath.size - 1
    }

    fun part2(input: List<String>): Int {
        val map = parseHeightMap(input)
        val lowestNodes = mutableListOf<HeightMap.Node>()
        map.applyToAll { if (it.height == 0) lowestNodes.add(it) }
//        println("Number of nodes with height 0=${lowestNodes.size}")
        var min = Int.MAX_VALUE
        while (lowestNodes.isNotEmpty()) {
            val node = lowestNodes.removeFirst()
            map.resetMap()
            map[node.position].distance = 0 // This defines the starting point!
            val shortestPath = findShortestPath(map)
            if (shortestPath.size - 1 < min) {
                min = shortestPath.size - 1
            }
//            println("Checked: $node, shortestPath=${shortestPath.size - 1}, remaining=${lowestNodes.size}")
        }
        return min
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    val steps = part1(testInput)
    check(steps == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    check(part1(input) == 380)
    check(part2(input) == 375)
}
