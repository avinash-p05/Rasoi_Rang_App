package com.example.recipesharingappxml

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.recipesharingappxml.home.Feed
import com.example.recipesharingappxml.post.Upload
import com.example.recipesharingappxml.profile.Profile
import com.example.recipesharingappxml.saved.Saved
import com.example.recipesharingappxml.trending.Home
import com.google.android.material.bottomnavigation.BottomNavigationView



class MainActivity : AppCompatActivity() {

    private lateinit var bottomBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomBar = findViewById(R.id.bnView)
        loadFrag(Feed())
        bottomBar.setOnItemSelectedListener{
            menuItem ->
            when(menuItem.itemId){
                R.id.nav_home ->{
                    loadFrag(Feed())
                    true
                }
                R.id.nav_feed->{
                    loadFrag(Home())
                    true
                }
                R.id.nav_upload -> {
                    loadFrag(Upload())
                    true
                }
                R.id.nav_saved -> {
                    loadFrag(Saved())
                    true
                }
                R.id.nav_profile -> {
                    loadFrag((Profile()))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFrag(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }
}