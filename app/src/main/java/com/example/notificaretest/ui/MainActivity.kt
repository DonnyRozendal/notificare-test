package com.example.notificaretest.ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.notificaretest.R
import com.example.notificaretest.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import re.notifica.Notificare
import re.notifica.models.NotificareApplication
import re.notifica.models.NotificareNotification
import re.notifica.push.ktx.INTENT_ACTION_NOTIFICATION_OPENED
import re.notifica.push.ktx.push
import re.notifica.push.ui.ktx.pushUI

class MainActivity : AppCompatActivity(), Notificare.Listener {

    private lateinit var binding: ActivityMainBinding
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        Notificare.addListener(this)

        if (SDK_INT >= TIRAMISU) {
            askForNotificationPermission()
        }

        if (intent != null) {
            handleIntent(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Notificare.removeListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            handleIntent(intent)
        }
    }

    override fun onReady(application: NotificareApplication) {
        Notificare.push().enableRemoteNotifications()
    }

    @Suppress("DEPRECATION")
    private fun handleIntent(intent: Intent) {
        if (Notificare.push().handleTrampolineIntent(intent)) return

        if (intent.action == Notificare.INTENT_ACTION_NOTIFICATION_OPENED) {
            intent.getParcelableExtra<NotificareNotification>(Notificare.INTENT_EXTRA_NOTIFICATION)?.also {
                Notificare.pushUI().presentNotification(this, it)
            }
        }
    }

    @RequiresApi(33)
    private fun askForNotificationPermission() {
        val permissionNotGranted =
            checkSelfPermission(this, POST_NOTIFICATIONS) == PERMISSION_DENIED
        val permissionNotDeniedBefore = !shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)

        if (permissionNotGranted && permissionNotDeniedBefore) {
            requestPermissionLauncher.launch(POST_NOTIFICATIONS)
        }
    }

}