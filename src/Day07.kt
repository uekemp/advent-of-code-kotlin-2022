sealed class FileSystemItem {

    abstract val name: String
}

class Directory(val parent: Directory?, override val name: String) : FileSystemItem() {

    val children = mutableListOf<FileSystemItem>()

    var size = 0L

    fun add(item: FileSystemItem) = children.add(item)

    fun findDirectory(name: String): Directory? {
        return children.find { item ->  item is Directory && item.name == name } as Directory?
    }

    fun traverseDirectories(visitor: (Directory) -> Unit) {
        if (parent == null) {
            visitor(this)
        }
        children.filterIsInstance<Directory>().forEach { directory ->
            visitor(directory)
            directory.traverseDirectories(visitor)
        }
    }

    fun computeSize(): Directory {
        children.forEach { child ->
            size += when (child) {
                is Directory -> { child.computeSize().size }
                is File -> { child.size }
            }
        }
        return this
    }

    override fun toString(): String {
        return "$name (dir, size=$size)"
    }
}

class File(override val name: String, val size: Long) : FileSystemItem() {

    override fun toString(): String {
        return "$name (file, size=$size)"
    }
}

fun parseTree(input: List<String>): Directory {
    val root = Directory(null, "/")
    var currentDirectory = root

    input.forEach { line ->
        when {
            line.startsWith("$ ls") -> {
                // ignore
            }
            line.startsWith("$ cd /") -> {
                currentDirectory = root
            }
            line.startsWith("$ cd ..") -> {
                currentDirectory = currentDirectory.parent ?: root
            }
            line.startsWith("$ cd ") -> {
                val (_, _, name) = line.split(" ")
                currentDirectory = currentDirectory.findDirectory(name) ?: error("Changing to unknown directory")
            }
            line.startsWith("dir ") -> {
                val name = line.substring(4)
                currentDirectory.add(Directory(currentDirectory, name))
            }
            else -> {
                val (size, name) = line.split(" ")
                currentDirectory.add(File(name, size.toLong()))
            }
        }
    }
    return root
}

fun printTree(directory: Directory, indent: String = "") {
    println("$indent- $directory")

    directory.children.forEach {
        when (it) {
            is Directory -> { printTree(it, "$indent  ") }
            is File -> println("$indent  - $it")
        }
    }
}

fun main() {
    fun part1(input: List<String>): Long {
        val root = parseTree(input).computeSize()
//        printTree(root)

        val result = mutableListOf<Directory>()
        root.traverseDirectories { dir -> if (dir.size <= 100000) result.add(dir) }
        return result.sumOf { it.size }
    }

    fun part2(input: List<String>): Long {
        val root = parseTree(input).computeSize()
        val result = mutableListOf<Directory>()
        val required = 30_000_000 - (70_000_000 - root.size)
        root.traverseDirectories { dir -> if (dir.size >= required) result.add(dir) }
        result.sortBy(Directory::size)
        return result.first().size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437L)
    check(part2(testInput) == 24933642L)

    val input = readInput("Day07")
    check(part1(input) == 1723892L)
    check(part2(input) == 8474158L)
}
