package co.atrasvida.example

import android.app.Application
import co.atrasvida.avidawebapi.database.AvidaAppDatabases

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AvidaAppDatabases.newInstance(this)
    }
}