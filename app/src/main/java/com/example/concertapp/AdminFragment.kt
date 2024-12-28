package com.example.concertapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.concertapp.databinding.FragmentAdminBinding
import com.google.android.material.navigation.NavigationView

class AdminFragment : Fragment(R.layout.fragment_admin), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminBinding.bind(view)

        // Toolbar ve DrawerLayout ayarları
        val toolbar: Toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        val drawerLayout = binding.drawerLayout
        val navView = binding.navView

        drawerToggle = ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // NavigationView tıklama olayları
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homeFragment -> {
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)
            }
            R.id.festivalsFragment -> {
                val intent = Intent(requireContext(), FestivalListActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                val intent = Intent(requireContext(), LoginFragment::class.java)
                startActivity(intent)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}