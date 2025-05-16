package com.example.healthcareproject.presentation.navigation

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.healthcareproject.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object NavigationModule {

    @Provides
    @ActivityScoped
    fun provideNavController(@ActivityContext context: Context): NavController {
        return (context as FragmentActivity).findNavController(R.id.nav_host_fragment)
    }

    @Provides
    @ActivityScoped
    fun provideMainNavigator(navController: NavController): MainNavigator {
        return MainNavigatorImpl(navController)
    }
}