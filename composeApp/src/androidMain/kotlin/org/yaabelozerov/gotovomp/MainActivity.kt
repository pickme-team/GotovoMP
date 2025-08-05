package org.yaabelozerov.gotovomp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.russhwolf.settings.BuildConfig
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.yaabelozerov.gotovomp.util.KoinModule
import org.yaabelozerov.gotovomp.util.androidModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        activity = this

        startKoin {
            androidContext(this@MainActivity.applicationContext)
            modules(KoinModule.network, KoinModule.viewModels, KoinModule.domain, androidModule)
        }

        if (BuildConfig.DEBUG) Napier.base(DebugAntilog())

        setContent {
            App()
        }
    }
    companion object {
        lateinit var activity: ComponentActivity
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}