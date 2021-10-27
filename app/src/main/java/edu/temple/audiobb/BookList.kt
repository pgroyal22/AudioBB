package edu.temple.audiobb
class BookList(var array: Array<Book>) {
    var arrayList : ArrayList<Book> = arrayListOf()
    init{
        arrayList.addAll(array)
    }
    fun add(element: Book) {
        arrayList.add(element)
    }

    fun remove(element: Book) {
        arrayList.remove(element)
    }

    fun get(index: Int): Book {
        return arrayList.get(index)
    }
    fun size() : Int{
        return arrayList.size
    }
}