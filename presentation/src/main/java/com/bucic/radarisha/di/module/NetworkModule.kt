package com.bucic.radarisha.di.module

import android.content.Context
import com.bucic.data.network.firestore.radar.RadarFireStore
import com.bucic.data.network.firestore.radar.RadarFireStoreImpl
import com.bucic.data.network.firestore.user.UserFireStore
import com.bucic.data.network.firestore.user.UserFireStoreImpl
import com.bucic.data.util.AndroidNetworkConnectivityChecker
import com.bucic.data.util.NetworkConnectivityChecker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    fun provideUserFirestoreImpl(
        db: FirebaseFirestore
    ): UserFireStore {
        return UserFireStoreImpl(db)
    }

    @Provides
    fun provideRadarFirestoreImpl(
        db: FirebaseFirestore
    ) : RadarFireStore {
        return RadarFireStoreImpl(db)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityChecker(@ApplicationContext context: Context): NetworkConnectivityChecker {
        return AndroidNetworkConnectivityChecker(context)
    }
}