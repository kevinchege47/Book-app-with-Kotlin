package com.bookskotlin.bookapp

class ModelCategory {
//    variables, must match in firebase
    var id:String = ""
    var category:String = ""
    var timestamp:Long = 0
    var uid:String = ""

    //    empty constructor, equired by firebase
    constructor()
    constructor(id: String, category: String, timestamp: Long, uid: String) {
        this.id = id
        this.category = category
        this.timestamp = timestamp
        this.uid = uid
    }
//    parameterized constructor

}