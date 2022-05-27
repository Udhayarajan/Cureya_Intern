package com.cureya.intern

import com.google.firebase.firestore.DocumentSnapshot

data class UserDetail(
    val id: String,
    val name: String,
    val email: String,
    val age: String,
    val city: String
){
    companion object{
        fun from(result: DocumentSnapshot):UserDetail{
            return UserDetail(
                result.getString("id")!!,
                result.getString("name")?:"-NA-",
                result.getString("mail")?:"-NA-",
                result.getString("age")?:"-NA-",
                result.getString("city")?:"-NA-"
            )
        }

    }
}

