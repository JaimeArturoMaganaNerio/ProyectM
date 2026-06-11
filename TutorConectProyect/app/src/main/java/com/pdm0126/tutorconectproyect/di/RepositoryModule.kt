package com.tutorconnect.di

import com.tutorconnect.data.api.ApiService
import com.tutorconnect.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    private const val BASE_URL = "http://192.168.0.53:8000/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: ApiService): AuthRepository {
        return DefaultAuthRepository(api)
    }

    @Provides
    @Singleton
    fun provideDashboardRepository(api: ApiService): DashboardRepository {
        return DefaultDashboardRepository(api)
    }

    @Provides
    @Singleton
    fun provideTutorRepository(api: ApiService): TutorRepository {
        return DefaultTutorRepository(api)
    }

    @Provides
    @Singleton
    fun provideChatRepository(api: ApiService): ChatRepository {
        return DefaultChatRepository(api)
    }

    @Provides
    @Singleton
    fun provideCalendarRepository(api: ApiService): CalendarRepository {
        return DefaultCalendarRepository(api)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(api: ApiService): BookingRepository {
        return DefaultBookingRepository(api)
    }

    @Provides
    @Singleton
    fun providePostRepository(api: ApiService): PostRepository {
        return DefaultPostRepository(api)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(api: ApiService): ProfileRepository {
        return DefaultProfileRepository(api)
    }
}