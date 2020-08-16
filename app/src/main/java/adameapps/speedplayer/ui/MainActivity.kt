package adameapps.speedplayer.ui

import adameapps.speedplayer.R
import adameapps.speedplayer.util.PermissionResultListener
import adameapps.speedplayer.util.PermissionsManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), PermissionResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        PermissionsManager.onPermissionsResult(requestCode, grantResults, this)
    }

    override fun onPermissionResult(permission: PermissionsManager.Permission, granted: Boolean) {
        when(permission) {
            PermissionsManager.Permission.LOCATION -> {
                if(!granted) {
                    //TODO: Handle not granted permission result
                }
            }
            PermissionsManager.Permission.FILE_READ -> {
                if(!granted) {
                    //TODO: Handle not granted permission result
                }
            }
            PermissionsManager.Permission.FILE_WRITE -> {
                if(!granted) {
                    //TODO: Handle not granted permission result
                }
            }
        }
    }
}
