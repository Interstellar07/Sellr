package com.example.sellr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.sellr.fragment.AboutUsFragment
import com.example.sellr.fragment.OnSaleFragment
import com.example.sellr.fragment.ReportUsFragment
import com.example.sellr.fragment.SoldFragment

class MainFragmentHolder : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_fragment_holder)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val intent: Intent = intent
        if(intent.hasExtra("sold"))
        {
            title="Sold items"
            fragmentLoad(SoldFragment())
        }
        else if(intent.hasExtra("onSale"))
        {
            title="Item on sale"
            fragmentLoad(OnSaleFragment())
        }
        else if(intent.hasExtra("aboutUs"))
        {
                title="About us"
            fragmentLoad(AboutUsFragment())
        }
        else if(intent.hasExtra("reportUs"))
        {
            title="Report an issue"
            fragmentLoad(ReportUsFragment())
        }
    }
    private fun fragmentLoad(fragment : Fragment)
    {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction= fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainAlterFrameLayout,fragment)
        fragmentTransaction.commit()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}