package com.ahmetkaan.uygulamakapatma

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ahmetkaan.uygulamakapatma.data.local.OnboardingPreferences
import com.ahmetkaan.uygulamakapatma.data.repository.AppLimitRepository
import com.ahmetkaan.uygulamakapatma.data.repository.ParentSettingsRepository
import com.ahmetkaan.uygulamakapatma.ui.dashboard.DashboardScreen
import com.ahmetkaan.uygulamakapatma.ui.onboarding.FirstRuleScreen
import com.ahmetkaan.uygulamakapatma.ui.onboarding.OnboardingPinScreen
import com.ahmetkaan.uygulamakapatma.ui.onboarding.SetupCompleteScreen
import com.ahmetkaan.uygulamakapatma.ui.onboarding.WelcomeScreen
import com.ahmetkaan.uygulamakapatma.ui.permission.PermissionScreen
import com.ahmetkaan.uygulamakapatma.ui.pin.PinLoginScreen
import com.ahmetkaan.uygulamakapatma.ui.theme.AppColors
import com.ahmetkaan.uygulamakapatma.ui.theme.ParentalControlTheme
import com.ahmetkaan.uygulamakapatma.util.PermissionChecker
import kotlinx.coroutines.launch

private enum class AppNavDestination {
    LOADING,
    WELCOME,
    PIN_SETUP,
    PERMISSIONS,
    FIRST_RULE,
    SETUP_COMPLETE,
    PIN_LOGIN,
    DASHBOARD
}

/**
 * Ana Activity ve Gezinme Yöneticisi.
 * 2027 Onboarding Akışı:
 * Karşılama -> PIN Belirleme -> İzin Merkezi -> İlk Kural -> Kurulum Tamamlandı -> Dashboard.
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as ParentalControlApp
        val parentRepo = ParentSettingsRepository(app.database.parentSettingsDao())
        val limitRepo = AppLimitRepository(app.database.appLimitDao())

        setContent {
            ParentalControlTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppColors.BackgroundDark
                ) {
                    MainNavigation(
                        parentRepo = parentRepo,
                        limitRepo = limitRepo,
                        onCheckPermissions = { PermissionChecker.areCorePermissionsGranted(this) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainNavigation(
    parentRepo: ParentSettingsRepository,
    limitRepo: AppLimitRepository,
    onCheckPermissions: () -> Boolean
) {
    val context = LocalContext.current
    val onboardingPrefs = remember { OnboardingPreferences(context) }

    var destination by remember { mutableStateOf(AppNavDestination.LOADING) }
    var isParentAuthenticated by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    fun evaluateNavigation() {
        scope.launch {
            val isPinConfigured = parentRepo.isPinSet()
            val hasPermissions = onCheckPermissions()
            val isCompleted = onboardingPrefs.isOnboardingCompleted

            if (!isCompleted) {
                // İlk Kurulum Sıralaması: PIN -> İzinler -> İlk Kural -> Tamamlandı
                if (!isPinConfigured) {
                    if (destination != AppNavDestination.PIN_SETUP) {
                        destination = AppNavDestination.WELCOME
                    }
                } else if (!hasPermissions) {
                    destination = AppNavDestination.PERMISSIONS
                } else {
                    // PIN var, izinler tamam ama onboarding bitmediyse ilk kural ekranı
                    if (destination != AppNavDestination.SETUP_COMPLETE) {
                        destination = AppNavDestination.FIRST_RULE
                    }
                }
            } else {
                // Kurulum daha önce tamamlanmış:
                if (!isParentAuthenticated) {
                    destination = AppNavDestination.PIN_LOGIN
                } else if (!hasPermissions) {
                    // Ebeveyn giriş yaptı ama sistemden izin kapatılmışsa İzin Merkezi'ne yönlendir
                    destination = AppNavDestination.PERMISSIONS
                } else {
                    destination = AppNavDestination.DASHBOARD
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                evaluateNavigation()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        evaluateNavigation()
    }

    Crossfade(targetState = destination, label = "navigation") { dest ->
        when (dest) {
            AppNavDestination.LOADING -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.PrimaryIndigo)
                }
            }

            AppNavDestination.WELCOME -> {
                WelcomeScreen(
                    onStart = {
                        destination = AppNavDestination.PIN_SETUP
                    }
                )
            }

            AppNavDestination.PIN_SETUP -> {
                OnboardingPinScreen(
                    parentRepo = parentRepo,
                    onPinConfigured = {
                        isParentAuthenticated = true
                        destination = AppNavDestination.PERMISSIONS
                    }
                )
            }

            AppNavDestination.PERMISSIONS -> {
                PermissionScreen(
                    onAllPermissionsGranted = {
                        if (!onboardingPrefs.isOnboardingCompleted) {
                            destination = AppNavDestination.FIRST_RULE
                        } else {
                            evaluateNavigation()
                        }
                    }
                )
            }

            AppNavDestination.FIRST_RULE -> {
                FirstRuleScreen(
                    limitRepo = limitRepo,
                    onComplete = {
                        destination = AppNavDestination.SETUP_COMPLETE
                    }
                )
            }

            AppNavDestination.SETUP_COMPLETE -> {
                SetupCompleteScreen(
                    onNavigateToDashboard = {
                        onboardingPrefs.isOnboardingCompleted = true
                        isParentAuthenticated = true
                        destination = AppNavDestination.DASHBOARD
                    }
                )
            }

            AppNavDestination.PIN_LOGIN -> {
                PinLoginScreen(
                    parentRepo = parentRepo,
                    onLoginSuccess = {
                        isParentAuthenticated = true
                        evaluateNavigation()
                    }
                )
            }

            AppNavDestination.DASHBOARD -> {
                DashboardScreen(parentRepo = parentRepo)
            }
        }
    }
}
