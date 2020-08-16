package adameapps.speedplayer.util

interface PermissionResultListener {
    fun onPermissionResult(permission: PermissionsManager.Permission, granted: Boolean)
}
