package adameapps.speedplayer.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat

object PermissionsManager {
    private const val FINE_LOCATION_PERMISSION_CODE = 8001
    private const val READ_EXTERNAL_MEMORY_PERMISSION_CODE = 8002
    private const val WRITE_EXTERNAL_MEMORY_PERMISSION_CODE = 8003

    fun hasPermission(context: Context, permission: Permission): Boolean {
        return ContextCompat.checkSelfPermission(
            context, permission.manifestPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermission(activity: Activity, permission: Permission) {
        requestPermissions(activity, arrayOf(permission.manifestPermission), permission.code)
    }

    fun onPermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
        permissionResultListener: PermissionResultListener
    ) {
        Permission.values().iterator().forEach {
            if (it.code == requestCode) {
                permissionResultListener.onPermissionResult(
                    it, grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED
                )
            }
        }
    }

    enum class Permission(val code: Int, val manifestPermission: String) {
        LOCATION(FINE_LOCATION_PERMISSION_CODE, Manifest.permission.ACCESS_FINE_LOCATION),
        FILE_READ(READ_EXTERNAL_MEMORY_PERMISSION_CODE, Manifest.permission.READ_EXTERNAL_STORAGE),
        FILE_WRITE(
            WRITE_EXTERNAL_MEMORY_PERMISSION_CODE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}
