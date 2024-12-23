package com.example.navdrawerkotpractice

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.drawerlayout.widget.DrawerLayout
import com.example.concertapp.R
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.example.concertapp.AddActivity
import com.example.concertapp.AddFragment
import com.example.concertapp.LoginFragment
import com.example.concertapp.UploadActivity

class AdminFragment : Fragment(R.layout.fragment_admin), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DrawerLayout ve diğer bileşenleri fragment içinde başlatıyoruz
        drawerLayout = view.findViewById(R.id.drawer_layout)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        val navigationView = view.findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            requireActivity(), drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                // Kullanıcı LogOut'a bastığında LoginFragment'e yönlendirilir
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment()) // `fragment_container` ana container ID'si
                    .addToBackStack(null) // Geri tuşu ile önceki fragment'a dönmek için
                    .commit()
            }
            R.id.homeFragment -> {
                Toast.makeText(requireContext(), "Home Selected", Toast.LENGTH_SHORT).show()
            }
            R.id.addFragment -> {
                // Add seçildiğinde UploadActivity'ye yönlendir
                val intent = Intent(requireContext(), AddActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            requireActivity().onBackPressed() // Activity üzerinden onBackPressed çağrılır
        }
    }
}
