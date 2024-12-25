package com.example.concertapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.concertapp.databinding.FragmentUserBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var festivalAdapter: FestivalAdapter
    private val festivalList = mutableListOf<DataClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar Ayarı
        val toolbar: Toolbar = binding.toolbarUser
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        // DrawerLayout ve NavigationView Ayarları
        val drawerLayout = binding.drawerLayoutUser
        val navView = binding.navViewUser

        // Hamburger Menü (Drawer Toggle) Ayarları
        drawerToggle = ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Navigation Drawer Tıklama Olayları
        navView.setNavigationItemSelectedListener(this)

        // RecyclerView Ayarı
        binding.festivalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        festivalAdapter = FestivalAdapter(festivalList)
        binding.festivalRecyclerView.adapter = festivalAdapter

        // Firebase'den festivalleri çek
        fetchFestivals()
    }

    private fun fetchFestivals() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Festival Data")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                festivalList.clear() // Eski verileri temizle
                for (festivalSnapshot in snapshot.children) {
                    val festival = festivalSnapshot.getValue(DataClass::class.java)
                    festival?.let {
                        it.key = festivalSnapshot.key // Benzersiz key değerini atayın
                        festivalList.add(it)
                    }
                }
                festivalAdapter.notifyDataSetChanged() // Adapteri güncelle
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch festivals: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> Toast.makeText(requireContext(), "Home clicked", Toast.LENGTH_SHORT).show()
            R.id.nav_profile -> Toast.makeText(requireContext(), "Profile clicked", Toast.LENGTH_SHORT).show()
            R.id.nav_logout -> Toast.makeText(requireContext(), "Logout clicked", Toast.LENGTH_SHORT).show()
        }
        binding.drawerLayoutUser.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}