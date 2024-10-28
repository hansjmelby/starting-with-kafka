package org.example

import java.util.Collections

fun main() {
    println("Hello World!")
    val list1 = listOf<String>("c","b","a")
    val list2 = listOf<String>("a","b","c")
    val list3 = listOf<String>("a","b","c","d")
 
    print(list1.sorted() == list2.sorted())
}