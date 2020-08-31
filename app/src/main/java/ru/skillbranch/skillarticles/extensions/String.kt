package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    this ?: return emptyList()

    val indexesList = mutableListOf<Int>()
    var currentIndex = 0
    var str = this!!

    while (str.contains(substr, ignoreCase)) {
        currentIndex += str.substringBefore(substr).length
        indexesList.add(currentIndex)
        str = str.substringAfter(substr)
    }

    return indexesList
}