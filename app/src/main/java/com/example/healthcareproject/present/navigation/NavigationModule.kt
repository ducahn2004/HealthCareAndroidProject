package com.example.healthcareproject.present.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
object NavigationModule {

    @Provides
    @FragmentScoped
    fun provideMainNavigator(fragment: Fragment): MainNavigator {
        val navController: NavController = fragment.findNavController()
        return MainNavigatorImpl(navController)
    }
}