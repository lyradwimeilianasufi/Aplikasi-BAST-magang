package com.example.aplikasibast

import android.app.Application
import androidx.room.Room
import com.example.aplikasibast.core.session.SessionManager
import com.example.aplikasibast.features.attendance.data.repository.KehadiranRepositoryImpl
import com.example.aplikasibast.features.attendance.domain.repository.IKehadiranRepository
import com.example.aplikasibast.features.attendance.presentation.viewmodel.AttendanceViewModel
import com.example.aplikasibast.features.permission.data.repository.PengajuanRepositoryImpl
import com.example.aplikasibast.features.permission.domain.repository.IPengajuanRepository
import com.example.aplikasibast.features.attendance.domain.usecase.*
import com.example.aplikasibast.features.permission.domain.usecase.*
import com.example.aplikasibast.features.permission.presentation.viewmodel.PermissionViewModel
import com.example.aplikasibast.features.approval.presentation.viewmodel.ApprovalViewModel
import com.example.aplikasibast.features.home.presentation.viewmodel.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.module.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val appModule = module {
            // 1. Database & DAO
            single {
                Room.databaseBuilder(
                    androidContext(),
                    AppDatabase::class.java,
                    AppDatabase.DATABASE_NAME
                ).fallbackToDestructiveMigration().build()
            }
            single { get<AppDatabase>().kehadiranDao() }
            single { get<AppDatabase>().pengajuanIzinDao() }

            // 2. Repository
            single<IKehadiranRepository> { KehadiranRepositoryImpl(get()) }
            single<IPengajuanRepository> { PengajuanRepositoryImpl(get()) }

            // 3. Session
            single { SessionManager(androidContext()) }

            // 4. Use Cases
            factory { GetPengajuanByStatusUseCase(get()) }
            factory { GetPengajuanByIdUseCase(get()) }
            factory { SubmitPengajuanUseCase(get()) }
            factory { UpdatePengajuanUseCase(get()) }
            factory { GetAllKehadiranUseCase(get()) }
            factory { GetKehadiranByIdUseCase(get()) }
            factory { GetKehadiranByTanggalUseCase(get()) }
            factory { InsertKehadiranUseCase(get()) }
            factory { UpdateKehadiranUseCase(get()) }

            // 5. ViewModels
            viewModel { AttendanceViewModel(get(), get(), get(), get(), get(), get()) }
            viewModel { PermissionViewModel(get(), get(), get(), get(), get(), get()) }
            viewModel { ApprovalViewModel(get(), get(), get()) }
            viewModel { HomeViewModel(get(), get(), get()) }
        }

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
