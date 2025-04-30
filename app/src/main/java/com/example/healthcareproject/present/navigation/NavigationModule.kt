package com.example.healthcareproject.present.navigation

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.healthcareproject.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
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
    @Provides
    fun provideNavController(@ActivityContext context: Context): NavController {
        return (context as FragmentActivity).findNavController(R.id.nav_host_fragment)
    }
}