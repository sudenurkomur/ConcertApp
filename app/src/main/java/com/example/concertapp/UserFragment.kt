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
import com.example.concertapp.api.SupabaseClient
import com.example.concertapp.databinding.FragmentUserBinding
import com.example.concertapp.models.DataClass
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        // Festivalleri Supabase'den çek
        fetchFestivals()
    }

    private fun fetchFestivals() {
        SupabaseClient.supabaseService.getFestivals().enqueue(object : Callback<List<DataClass>> {
            override fun onResponse(
                call: Call<List<DataClass>>,
                response: Response<List<DataClass>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { festivals ->
                        festivalList.clear()
                        festivalList.addAll(festivals)
                        festivalAdapter.notifyDataSetChanged()
                    } ?: run {
                        Toast.makeText(requireContext(), "No festivals found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch festivals: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<DataClass>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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