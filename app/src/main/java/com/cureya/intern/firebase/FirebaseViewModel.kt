package com.cureya.intern.firebase

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.cureya.intern.UserDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.regex.Pattern

/**
 * @author Udhaya
 * Created on 27-05-2022
 */

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = "FirebaseViewModel"

    fun isLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    private fun getFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    fun getCurrentProfile() = flow {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            getProfile(it.uid).collect{ details->
                emit(details)
            }
        }
    }

    fun signUp(mailId: String, password: String) = flow {
        try {
            val result =
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(mailId, password).await()
            if (result.user != null) {
                emit(Result.success(FirebaseAuth.getInstance().currentUser))
            } else {
                Log.e(TAG, "createUserWithEmail:failure")
                emit(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "createUserWithEmail:failure", e);
            emit(Result.failure(e))
        }
    }

    fun saveUser(id: String, mail: String, name: String, city: String, age: String) = flow {
        val user = mutableMapOf<String, String>()
        user["id"] = id
        user["mail"] = mail
        user["name"] = name
        user["city"] = city
        user["age"] = age
        val result = getFirestore()
            .collection("users").document(id)
            .set(user)
            .addOnCanceledListener {
                Log.e(TAG, "saveUser:success")
            }
            .addOnFailureListener {
                Log.e(TAG, "saveUser:failure", it)
            }
            .await()
        emit(result != null)
    }

    fun login(mail: String, password: String) = flow {
        try {
            val result =
                FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, password).await()
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun isEmailFormedCorrectly(mail: String): Boolean {
        val matcher =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
                .matcher(mail)
        return matcher.find()
    }

    fun signOut() = flow {
        try {
            FirebaseAuth.getInstance().signOut()
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getCollection() = flow {
        val result = getFirestore().collection("users")
            .get().await()
        val list = mutableListOf<UserDetail>()
        result.documents.forEach {
            list.add(UserDetail.from(it))
        }
        emit(list as List<UserDetail>)
    }

    fun getProfile(it: String) = flow {
        val result = getFirestore().collection("users")
            .document(it).get()
            .await()
        val detail = UserDetail.from(result)
        emit(detail)
    }
}
