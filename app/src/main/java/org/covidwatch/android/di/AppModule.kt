package org.covidwatch.android.di

import android.content.Context
import okhttp3.OkHttpClient
import org.covidwatch.android.CovidWatchTcnManager
import org.covidwatch.android.NotificationFactory
import org.covidwatch.android.data.CovidWatchDatabase
import org.covidwatch.android.data.TestedRepositoryImpl
import org.covidwatch.android.data.UserFlowRepositoryImpl
import org.covidwatch.android.data.signedreport.firestore.SignedReportsUploader
import org.covidwatch.android.domain.NotifyAboutPossibleExposureUseCase
import org.covidwatch.android.domain.TestedRepository
import org.covidwatch.android.domain.UserFlowRepository
import org.covidwatch.android.presentation.home.HomeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.tcncoalition.tcnclient.TcnKeys

@Suppress("USELESS_CAST")
val appModule = module {

    factory {
        UserFlowRepositoryImpl(
            preferences = get()
        ) as UserFlowRepository
    }

    factory {
        val context = androidContext()

        context.getSharedPreferences(
            "org.covidwatch.android.PREFERENCE_FILE_KEY",
            Context.MODE_PRIVATE
        )
    }

    viewModel {
        HomeViewModel(
            userFlowRepository = get(),
            testedRepository = get(),
            tcnDao = get()
        )
    }

    single {
        CovidWatchDatabase.getInstance(androidContext())
    }

    single {
        val database: CovidWatchDatabase = get()

        database.contactEventDAO()
    }

    single {
        val database: CovidWatchDatabase = get()
        database.signedReportDAO()
    }

    single {
        val database: CovidWatchDatabase = get()
        database.temporaryContactNumberDAO()
    }

    single { TcnKeys(androidApplication()) }

    single { OkHttpClient() }

    single { SignedReportsUploader(okHttpClient = get(), signedReportDAO = get()) }

    factory {
        TestedRepositoryImpl(
            preferences = get(),
            covidWatchTcnManager = get()
        ) as TestedRepository
    }

    factory {
        NotifyAboutPossibleExposureUseCase(
            context = androidContext(),
            notificationFactory = get(),
            testedRepository = get(),
            contactEventDAO = get()
        )
    }

    factory {
        NotificationFactory(androidContext())
    }

    single {
        CovidWatchTcnManager(
            context = androidApplication(),
            tcnKeys = get(),
            tcnDao = get(),
            signedReportDAO = get()
        )
    }
}