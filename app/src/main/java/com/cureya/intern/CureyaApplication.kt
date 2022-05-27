package com.cureya.intern

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

/**
 * @author Udhaya
 * Created on 27-05-2022
 */

class CureyaApplication : Application() {

    override fun onCreate() {
        FirebaseApp.initializeApp(applicationContext)
        super.onCreate()
    }
}