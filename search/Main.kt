package search

import java.io.File

class Database(val entries: List<String>, val invertedIdx: Map<String, Set<Int>>);

fun readEntries(fileName: String): List<String> {
    val file = File(fileName)
    if (file.exists()){
        println(file.name)
        return file.readLines()
    }

    println("File doesn't exist.")
    return emptyList()
}

fun readMenuChoice(): Int {
    println(
            """
                ==== Menu ====
                1. Search information
                2. Print all data.
                0. Exit
            """.trimIndent()
    )
    return readInput("> ").toInt()

}

fun main(args: Array<String>) {
    val fileName = args[1]
    val entries = readEntries(fileName)
    val database = Database(entries, createInvertedIdx(entries))
    do {
        println()
        val choice = readMenuChoice()
        println()
        when (choice) {
            1 -> searchPeopleChoice(database)
            2 -> printDatabase(database.entries)
            0 -> break
            else -> println("Incorrect option! Try again.")
        }
    }while (true)
    println("Bye!")

}

fun createInvertedIdx(entries: List<String>): Map<String, Set<Int>> {
    val invertedIdx = mutableMapOf<String, MutableSet<Int>>()
    for (idx in entries.indices) {
        for (data in entries[idx].trim()
                .lowercase()
                .split(" ")) {
            invertedIdx.getOrPut(data) { mutableSetOf() } += idx
        }
    }
    return invertedIdx
}

fun printDatabase(database: List<String>) {
    println("==== List of people ===")
    println(database.joinToString("\n"))
}

fun searchPeopleChoice(database: Database) {
    val searchStrategy = readInput("Select a matching strategy: ALL, ANY, NONE:\n> ")
    println()
    val searchQueries = readInput("Enter a name or email to search all suitable people:\n> ")
            .lowercase()
            .split(Regex("\\s+"))
    print(searchQueries)
    val results = searchDatabase(database, searchStrategy, searchQueries)

    if (results.isNotEmpty()){
        println()
        println("People found: \n${results.joinToString ("\n" ){database.entries[it]}}")
    }
    else println("No matching people found.")
}

fun searchDatabase(database: Database, searchStrategy: String, searchQueries: List<String>) : Collection<Int> {
    return when (searchStrategy) {
        "ANY" -> searchAny(database, searchQueries)
        "ALL" -> searchAll(database, searchQueries)
        "NONE" -> searchNone(database, searchQueries)
        else -> {
            println("Invalid Entry")
            emptyList()
        }
    }
}

fun searchNone(database: Database, searchQueries: List<String>): Collection<Int> {
    var result = database.entries.indices.toSet()
    for (query in searchQueries){
        result = result.subtract(database.invertedIdx.getOrDefault(query, emptySet()))
    }
    return  result
}

fun searchAll(database: Database, searchQueries: List<String>): Collection<Int> {
    var result = database.entries.indices.toSet()
    for (query in searchQueries){
        result = result.intersect(database.invertedIdx.getOrElse(query) { return emptySet() })
    }
    return  result
}

fun searchAny(database: Database, searchQueries: List<String>): Collection<Int> {
    var result = emptySet<Int>()
    for (query in searchQueries){
        result = result.union(database.invertedIdx.getOrDefault(query, emptySet()))
    }
    return  result
}

fun readInput(prompt: String): String{
    print(prompt)
    return readLine()!!
}
