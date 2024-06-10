package com.bucic.radarisha.di.module

import com.bucic.data.database.user.dao.UserDao
import com.bucic.data.network.firestore.UserFireStore
import com.bucic.data.repository.user.UserDataSource
import com.bucic.data.repository.user.UserLocalDataSource
import com.bucic.data.repository.user.UserRemoteDataSource
import com.bucic.data.repository.user.UserRepositoryImpl
import com.bucic.domain.repository.UserRepository
import com.bucic.domain.usecases.user.CreateUserUseCase
import com.bucic.domain.usecases.user.GetCurrentUserUseCase
import com.bucic.domain.usecases.user.GetUserByUsernameAndPasswordUseCase
import com.bucic.domain.usecases.user.RemoveCurrentUserUseCase
import com.bucic.domain.usecases.user.SaveCurrentUserUseCase
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
        remote: UserDataSource.Remote,
        local: UserDataSource.Local
    ): UserRepository {
        return UserRepositoryImpl(remote, local)
    }

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(
        userFireStore: UserFireStore
    ): UserDataSource.Remote {
        return UserRemoteDataSource(userFireStore)
    }

    @Provides
    @Singleton
    fun provideUserLocalDataSource(
        userDao: UserDao
    ): UserDataSource.Local {
        return UserLocalDataSource(userDao)
    }

    @Provides
    fun provideCreateUserUseCase(userRepository: UserRepository): CreateUserUseCase {
        return CreateUserUseCase(userRepository)
    }

    @Provides
    fun provideGetUserByUsernameUseCase(userRepository: UserRepository): GetUserByUsernameAndPasswordUseCase {
        return GetUserByUsernameAndPasswordUseCase(userRepository)
    }

    @Provides
    fun provideGetCurrentUserUseCase(userRepository: UserRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(userRepository)
    }

    @Provides
    fun provideSaveCurrentUserUseCase(userRepository: UserRepository): SaveCurrentUserUseCase {
        return SaveCurrentUserUseCase(userRepository)
    }

    @Provides
    fun provideRemoveCurrentUserUseCase(userRepository: UserRepository): RemoveCurrentUserUseCase {
        return RemoveCurrentUserUseCase(userRepository)
    }

}