package com.bucic.radarisha.di.module

import com.bucic.data.network.firestore.radar.RadarFireStore
import com.bucic.data.network.firestore.radar.RadarFireStoreImpl
import com.bucic.data.network.firestore.user.UserFireStore
import com.bucic.data.network.firestore.user.UserFireStoreImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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
}