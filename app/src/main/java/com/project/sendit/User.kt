package com.project.sendit

class User {

    var profilepic: String? = null
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    //var lastmessage: String? = null

    constructor(){}

    constructor(name: String?, email: String?, uid: String?){
        this.name = name
        this.email = email
        this.uid = uid
    }

    /*constructor(name: String?, email: String?, uid: String?, lastmessage: String?){
        this.name = name
        this.email = email
        this.uid = uid
        this.lastmessage = lastmessage
    }*/


}