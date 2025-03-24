package com.example.seniorlauncher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.seniorlauncher.ui.theme.SeniorLauncherTheme
import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap

data class AppInfo (
    val appName: String,
    val packageName: String,
    val className: String,
    val icon: android.graphics.drawable.Drawable?
)

class MainActivity : ComponentActivity() {

    private lateinit var appList: List<AppInfo>
    private val READ_STORAGE_PERMISSION_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadApps()
        // Register ActivityResult handler
        val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            // Handle permission requests results
            // See the permission example in the Android platform samples: https://github.com/android/platform-samples
        }

// Permission request logic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }

        setContent {
            SeniorLauncherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

//                    WallpaperBackground()
                    DisplayAppsList(appList)
                }
            }
        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_PERMISSION_REQUEST)
//        } else {
//
//        }

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

        this.appList = appList
    }
}

fun launchApp(context: Context, appInfo: AppInfo) {
    val launchIntent = Intent(Intent.ACTION_MAIN)
    launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
    launchIntent.component = ComponentName(appInfo.packageName, appInfo.className)
    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(launchIntent)
}

@Composable
fun WallpaperBackground() {
    val context = LocalContext.current
    val wallpaperManager = android.app.WallpaperManager.getInstance(context)

    val wallpaperBitmap = remember {
        (wallpaperManager.drawable as? BitmapDrawable)?.bitmap?.asImageBitmap()
    }

    if (wallpaperBitmap != null) {
        Image(
            bitmap = wallpaperBitmap,
            contentDescription = "SystemWallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
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
            Row() {
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
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .clickable {
                            launchApp(context = context, appInfo = appList[it])
                        }
                    ,
                    style = TextStyle(fontSize = 24.sp)
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