package com.hackthenorth.pennapps.investorbuddy

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dragHeader = supportFragmentManager.findFragmentById(R.id.dragHeaderFragment) as ItemFragment;
        dragHeader.init(ItemData(false, "UNTITLED P...", 0f, -1f, "0 HOLDINGS"))

    }


    fun onFinish() {
    }

}
