package com.example.seniorlauncher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.seniorlauncher.ui.theme.SeniorLauncherTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toBitmap
import com.example.seniorlauncher.ui.theme.Typography

data class AppInfo (
    val appName: String,
    val packageName: String,
    val className: String,
    val icon: android.graphics.drawable.Drawable?
)

class MainActivity : ComponentActivity() {

    private lateinit var appList: List<AppInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadApps()
        setContent {
            SeniorLauncherTheme {
                BackHandler(enabled = true) {}
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = Color.Transparent
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        items(items=appList) { item ->
                            AppRow(appInfo = item)
                        }
                    }
                }
            }
        }
    }

    private fun loadApps() {
        val appList = mutableListOf<AppInfo>()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resolveInfoList) {
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val packageName = resolveInfo.activityInfo.packageName
            val className = resolveInfo.activityInfo.name
            val appIcon = resolveInfo.loadIcon(packageManager)
            val appInfo = AppInfo(appName, packageName, className, appIcon)
            appList.add(appInfo)
        }

        appList.add(AppInfo("App Settings", "", "SettingsActivity", null))

        this.appList = appList
    }
}

fun launchApp(context: Context, appInfo: AppInfo) {
    if (appInfo.className == "SettingsActivity") {
        val settingsIntent = Intent(context, SettingsActivity::class.java)
        context.startActivity(settingsIntent)
        return
    }
    val launchIntent = Intent(Intent.ACTION_MAIN)
    launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
    launchIntent.component = ComponentName(appInfo.packageName, appInfo.className)
    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(launchIntent)
}

@Composable
fun AppRow(appInfo: AppInfo) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(0.dp, 4.dp)
            .clickable {
                launchApp(context = context, appInfo = appInfo)
            },
    ) {
        if (appInfo.icon != null) {
            val appIconBitmap = appInfo.icon?.toBitmap()?.asImageBitmap()
            if (appIconBitmap != null) {
                Image(
                    bitmap = appIconBitmap,
                    contentDescription = appInfo.appName,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        Text (
            appInfo.appName,
            modifier = Modifier.padding(0.dp, 8.dp),
            style = Typography.titleLarge.merge(color= Color.White)
        )
    }
}