package com.example.aplikasibast

import android.app.Application
import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}

val appModule = module {
    // Session Manager
    single { SessionManager(androidContext()) }

    // Room Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    // DAOs
    single { get<AppDatabase>().kehadiranDao() }
    single { get<AppDatabase>().pengajuanIzinDao() }

    // Repository
    single { AppRepository(get(), get()) }

    // ViewModels - Menambahkan get() kedua untuk SessionManager
    viewModel { MainViewModel(get(), get()) }
}
