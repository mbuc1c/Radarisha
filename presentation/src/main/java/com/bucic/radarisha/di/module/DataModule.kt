package com.bucic.radarisha.di.module

import com.bucic.data.network.firestore.UserFireStore
import com.bucic.data.repository.user.UserDataSource
import com.bucic.data.repository.user.UserRemoteDataSource
import com.bucic.data.repository.user.UserRepositoryImpl
import com.bucic.domain.repository.UserRepository
import com.bucic.domain.usecases.user.CreateUserUseCase
import com.bucic.domain.usecases.user.GetUserByUsernameAndPasswordUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideUserRepositoryImpl(
        remote: UserDataSource.Remote
    ): UserRepository {
        return UserRepositoryImpl(remote)
    }

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(
        userFireStore: UserFireStore
    ): UserDataSource.Remote {
        return UserRemoteDataSource(userFireStore)
    }

    @Provides
    fun provideCreateUserUseCase(userRepository: UserRepository): CreateUserUseCase {
        return CreateUserUseCase(userRepository)
    }

    @Provides
    fun provideGetUserByUsernameUseCase(userRepository: UserRepository): GetUserByUsernameAndPasswordUseCase {
        return GetUserByUsernameAndPasswordUseCase(userRepository)
    }
}