package ceton.roun.notes.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ceton.roun.notes.databinding.ActivityGameBinding
import com.google.android.material.tabs.TabLayoutMediator


class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        APP = this
        initial()
    }

    private fun initial() {
        binding.viewPager.adapter = PagerAdapter(this)
        binding.tabLayout.tabIconTint = null
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
    }
}
