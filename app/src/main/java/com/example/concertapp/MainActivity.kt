package com.example.concertapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentTransaction
import com.example.concertapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash Screen kurulumunu doğru şekilde yapıyoruz.
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false } // Gerekirse koşul belirtebilirsiniz.

        super.onCreate(savedInstanceState)

        // View Binding ile ana düzenimizi bağlıyoruz.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fragment'ı ekliyoruz
        if (savedInstanceState == null) {
            val loginFragment = LoginFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, loginFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }
}
