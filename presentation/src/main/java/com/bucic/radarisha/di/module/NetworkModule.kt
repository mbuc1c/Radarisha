package com.bucic.radarisha.di.module

import com.bucic.data.network.firestore.UserFireStore
import com.bucic.data.network.firestore.UserFireStoreImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideUserFirestoreImpl(
        db: FirebaseFirestore
    ): UserFireStore {
        return UserFireStoreImpl(db)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }
}