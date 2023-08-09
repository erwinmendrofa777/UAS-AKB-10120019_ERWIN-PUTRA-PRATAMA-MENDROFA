package dev.erwin.todo.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import dev.erwin.todo.R
import dev.erwin.todo.databinding.ActivityMainBinding
import dev.erwin.todo.databinding.DrawerHeaderBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerHeaderBinding: DrawerHeaderBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val headerView = binding.navView.getHeaderView(0) as MaterialCardView
        drawerHeaderBinding = DrawerHeaderBinding.bind(headerView)

        setContentView(binding.root)
        setUpDrawerHeader()
        setUpDrawerNavigation()
        onDrawerChanged()
        onSelectedDrawerItem()
    }

    private fun setUpDrawerHeader(): Unit = with(drawerHeaderBinding) {
        fullName.text = mainViewModel.studentProfile.fullName
        profilePicture.setImageDrawable(mainViewModel.studentProfile.profilePicture)
        email.text = mainViewModel.studentProfile.emailAddress
    }

    private fun setUpDrawerNavigation(): Unit = with(binding) {
        supportActionBar?.hide()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.main_navigation)
        lifecycleScope.launch {
            delay(timeMillis = LOADING_TIME)
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mainViewModel.isAlreadyLogin
                    .filterNotNull()
                    .distinctUntilChanged()
                    .collectLatest { isAlreadyLogin ->
                        val currentDestinationId = navController.currentDestination?.id
                        when {
                            !isAlreadyLogin && currentDestinationId != R.id.loginFragment -> {
                                navController.navigate(
                                    R.id.action_global_authentication
                                )
                            }

                            isAlreadyLogin && currentDestinationId != R.id.homeFragment -> {
                                navController.navigate(
                                    R.id.action_global_home
                                )
                            }
                        }
                    }
            }
        }
        navController.graph = navGraph
        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.aboutFragment), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.isDrawerOpen.collectLatest { isOpen ->
                    if (!isOpen) drawerLayout.close()
                    else drawerLayout.open()
                }
            }
        }
    }

    private fun onDrawerChanged() = with(binding) {
        drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                mainViewModel.updaterDrawerState(isOpen = false)
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    private fun onSelectedDrawerItem() = with(binding) {
        navView.setNavigationItemSelectedListener { menuItem ->
            val itemId = menuItem.itemId
            val currentDestinationId = navController.currentDestination?.id

            when (itemId) {
                R.id.logout -> {
                    drawerLayout.close()
                    mainViewModel.logout()
                    true
                }

                R.id.home -> {
                    drawerLayout.close()
                    if (currentDestinationId != R.id.homeFragment)
                        navController.navigate(R.id.action_global_home)
                    true
                }

                R.id.favorite -> {
                    drawerLayout.close()
                    if (currentDestinationId != R.id.favoriteFragment)
                        navController.navigate(R.id.action_global_favorite)
                    true
                }

                R.id.about -> {
                    drawerLayout.close()
                    if (currentDestinationId != R.id.aboutFragment)
                        navController.navigate(R.id.action_global_about)
                    true
                }

                else -> true
            }
        }
    }

    private companion object {
        const val LOADING_TIME = 1000L
    }
}