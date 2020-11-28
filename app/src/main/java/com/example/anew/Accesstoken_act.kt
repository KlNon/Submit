package com.example.anew

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_accesstokenact.*
import kotlinx.android.synthetic.main.activity_findpassword.*
import kotlinx.android.synthetic.main.activity_register.*

class Accesstoken_act : AppCompatActivity() {
    fun click(v: View?) {//问号表示当前对象可以为空,!!表示当前对象不为空的情况下执行
        when (v?.id) {
            R.id.back_act->{
                finish()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accesstokenact)
        val extraData=intent.getStringExtra("extra_data")
        accesstokentext.text = "AccessToken为:\n $extraData"
    }
}