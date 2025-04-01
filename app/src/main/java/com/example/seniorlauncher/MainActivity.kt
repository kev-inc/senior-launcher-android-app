package com.example.seniorlauncher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seniorlauncher.ui.theme.SeniorLauncherTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
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
                    DisplayAppsList(appList)
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


fun launchSettings(context: Context) {
    val settingsIntent = Intent(context, SettingsActivity::class.java)
    context.startActivity(settingsIntent)
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
fun DisplayAppsList(appList: List<AppInfo>) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState())

    ) {
        repeat(appList.size) {
            Row(
                modifier = Modifier
                    .padding(0.dp, 4.dp)
                    .clickable {
                        launchApp(context = context, appInfo = appList[it])
                    },
            ) {
                if (appList[it].icon != null) {
                    val appIconBitmap = appList[it].icon?.toBitmap()?.asImageBitmap()
                    if (appIconBitmap != null) {
                        Image(
                            bitmap = appIconBitmap,
                            contentDescription = appList[it].appName,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                }
                Text (
                    appList[it].appName,
                    modifier = Modifier.padding(0.dp, 8.dp),
                    style = Typography.titleLarge.merge(color= Color.White)
                )
            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SeniorLauncherTheme {
        Greeting("Android")
    }
}