package adameapps.speedplayer.ui

import adameapps.speedplayer.R
import adameapps.speedplayer.data.DataManager
import adameapps.speedplayer.model.MusicLibrary
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MusicLibrary.init(this)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_setup, R.id.navigation_player
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        if (DataManager.readAnyTrackSelectedForEachState(this)) {
            navView.selectedItemId = R.id.navigation_player
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
