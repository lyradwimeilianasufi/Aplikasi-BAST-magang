package com.example.aplikasibast

import android.app.Application
import androidx.room.Room
import com.example.aplikasibast.data.repository.KehadiranRepositoryImpl
import com.example.aplikasibast.data.repository.PengajuanRepositoryImpl
import com.example.aplikasibast.domain.repository.IKehadiranRepository
import com.example.aplikasibast.domain.repository.IPengajuanRepository
import com.example.aplikasibast.domain.usecase.*
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
            modules(appModule, useCaseModule)
        }
    }
}

val useCaseModule = module {
    // Pengajuan Use Cases
    factory { GetPengajuanByStatusUseCase(get()) }
    factory { GetPengajuanByIdUseCase(get()) }
    factory { SubmitPengajuanUseCase(get()) }
    factory { UpdatePengajuanUseCase(get()) }

    // Kehadiran Use Cases
    factory { GetAllKehadiranUseCase(get()) }
    factory { GetKehadiranByIdUseCase(get()) }
    factory { InsertKehadiranUseCase(get()) }
    factory { UpdateKehadiranUseCase(get()) }
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

    // Repositories
    single<IPengajuanRepository> { PengajuanRepositoryImpl(get()) }
    single<IKehadiranRepository> { KehadiranRepositoryImpl(get()) }

    // ViewModel
    viewModel { MainViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
