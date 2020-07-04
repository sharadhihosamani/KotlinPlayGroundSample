package com.example.kotlinplayground.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinplayground.R
import com.example.kotlinplayground.model.NewArrivals
import com.example.kotlinplayground.user.AdapterUser
import com.example.kotlinplayground.user.MainActivityViewModel
import com.example.kotlinplayground.user.MainActivityViewModelFactory
import com.example.kotlinplayground.user.UIEventManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), UIEventManager {

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    setContent("Home")
                    true
                }
                R.id.menu_profile -> {
                    setContent("Profile")
                    true
                }
                R.id.menu_search -> {
                    setContent("Menu")
                    true
                }
                else -> false
            }
        }

        val viewModelFactory = MainActivityViewModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)

        init()
    }

    private fun setDataToRecyclerView(userList: List<NewArrivals>) {
        recyclerViewMain.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = AdapterUser(userList)
        }
    }

    private fun init() {
        btn_clear.setOnClickListener {
            setDataToRecyclerView(listOf())
        }

        btn_load_data.setOnClickListener {
            loadData()
        }
    }

    private fun loadData() {
        viewModel.loadDataFromWeb().observe(this, Observer {
            setDataToRecyclerView(it)
        })
    }

    override fun showToast(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    override fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }

    private fun setContent(content: String) {
        setTitle(content)
        tvLabel.text = content
    }
}