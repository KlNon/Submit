package com.example.anew

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_findpassword.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL


class MainActivity : AppCompatActivity() {
    private val TAG="MainActivity"

    fun click(v: View?){//问号表示当前对象可以为空,!!表示当前对象不为空的情况下执行
        when(v?.id){
            R.id.button1 -> {
                //显式
//                  val intent=Intent(this , login::class.java)//将Button_1和SecondActivity.java对应的界面关联//var是一个可变变量，这是一个可以通过重新分配来更改为另一个值的变量。
//                  startActivity(intent)//启动所关联的活动项,通过startActivity()来执行这个intent
                //或
                //隐式
                val usernamein = editTextusername.text.toString()
                val passwordin = editTextTextPassword.text.toString()

                //查重
                val dbHelper = MyDatabaseHelper(this, "userdata.db", 1)
                val db2 = dbHelper.writableDatabase
                val cursor = db2.query(
                        "DATA",
                        null,
                        "username=?",
                        arrayOf(usernamein),
                        null,
                        null,
                        null
                )
                if(accesstoken.isChecked){
                    val Account = editTextusername.text.toString()
                    val Password = editTextTextPassword.text.toString()
                    if (Account == "" || Password == "") {
                        Toast.makeText(applicationContext, "用户名或密码为空", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        val thread = Thread {
                            init_api(Account, Password)
                        }
                        thread.start()
                    }
                }else {
                    if (cursor.moveToFirst()) {
                        val username = cursor.getString(cursor.getColumnIndex("username"))
                        val password = cursor.getString(cursor.getColumnIndex("password"))
                        val editor1 = cursor.getInt(cursor.getColumnIndex("remember_password"))
                        if (usernamein == username) {
                            if (usernamein == username && passwordin == password) {
                                val db = dbHelper.writableDatabase
                                val editor =
                                    getSharedPreferences("MYdata", Context.MODE_PRIVATE).edit()
//                            Log.d(TAG, "${remember_password.isChecked}")
                                if (remember_password.isChecked) {
                                    editor.putString("username", username)
                                    editor.putString("password", password)
                                    editor.putBoolean("remember_password", true)//记住密码_是
                                    editor.apply()
                                    val values1 = ContentValues().apply {
                                        put("remember_password", 1)
                                    }
                                    db.insert("DATA", null, values1)
                                } else {
                                    editor.putBoolean("remember_password", false)//记住密码_否
                                    editor.apply()
                                    val values1 = ContentValues().apply {
                                        put("remember_password", 0)
                                    }
                                    db.insert("DATA", null, values1)
                                }
                                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show()
                                val intent =
                                    Intent("com.example.activitytest.ACTION_log");//只有AndroidManifest.xml文件中同时满足action android:name="com.example.myapplication.ACTION_START"和category附加条件的才会反馈
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("欢迎")
                                builder.setMessage("欢迎您登录,${editTextusername.text.toString()}")
                                builder.setPositiveButton("OK") { _, _ ->
                                    startActivity(intent)
                                    finish()
                                }
                                val alert = builder.create()
                                alert.show()
                            } else {
                                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "账号错误", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "账号不存在", Toast.LENGTH_SHORT).show()
                    }
                    //查重
                }
            }
            R.id.button2 -> {
                Toast.makeText(this, "注册", Toast.LENGTH_SHORT).show()
//                val intent=Intent(this , register::class.java)//将Button_1和SecondActivity.java对应的界面关联//var是一个可变变量，这是一个可以通过重新分配来更改为另一个值的变量。
//                startActivity(intent)//启动所关联的项,通过startActivity()来执行这个intent
                val intent =
                        Intent("com.example.activitytest.ACTION_reg")//只有AndroidManifest.xml文件中同时满足action android:name="com.example.myapplication.ACTION_START"和category附加条件的才会反馈
                intent.addCategory("com.example.activitytest.MY_CATEGORY")
                startActivityForResult(intent, 1);
            }
            R.id.button3 -> {
                Toast.makeText(this, "更改密码", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Find_password::class.java)
                startActivity(intent)//启动所关联的项,通过startActivity()来执行这个intent
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu);//第一个参数用于指定我们通过哪一个资源文件来创建菜单,第二个参数用于指定我们的菜单项将添加到哪一个Menu对象当中.
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val dbHelper =MyDatabaseHelper(this, "userdata.db", 1)
        when(item.itemId){
            R.id.hello_item -> Toast.makeText(this, "选项一", Toast.LENGTH_SHORT).show()
            R.id.world_item -> Toast.makeText(this, "选项二", Toast.LENGTH_SHORT).show()
            R.id.baidu -> {
                Toast.makeText(this, "打开百度", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://www.baidu.com")
                startActivity(intent)
            }
            R.id.create -> {
                dbHelper.writableDatabase
            }
            R.id.destroy -> {
                val db = dbHelper.writableDatabase
                db.delete("DATA", "id>?", arrayOf("0"))
            }
            R.id.connect -> {
                Toast.makeText(this,"已废弃",Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    private fun init_api(account: String, pwd: String) {
        val urlpath = "http://api.nlecloud.com/Users/Login"
        val url: URL
        try {
            url = URL(urlpath)
            val jsonObject = JSONObject()
            jsonObject.put("Account", account)
            jsonObject.put("Password", pwd)
            //将参数put到json
            val content = jsonObject.toString()
            //开启连接
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.setConnectTimeout(5000)
            conn.setDoOutput(true) //允许写出
            conn.setDoInput(true) //允许读入
            conn.setRequestMethod("POST") //提交方式
            conn.setRequestProperty("Content-Type", "application/json") //设置参数类型是json格式
            //写输出流，将要转的参数写入流
            val os: OutputStream = conn.getOutputStream()
            os.write(content.toByteArray())
            os.close()
            val code: Int = conn.getResponseCode()
            if (code == 200) {
                //读取返回的json
                val inputStream: InputStream = conn.getInputStream()
                //调用NetUtils() 将流转成String类型
                val json: String = NetUtils.readString(inputStream)
                val gson = Gson()
                val typeof1=object :TypeToken<bean>(){}.type
                var b = gson.fromJson<bean>(json, typeof1)
                Log.d(TAG, "$b")
                if (b.Status === 0) {
                    val intent = Intent(this, Accesstoken_act::class.java)//将Button_1和SecondActivity.java对应的界面关联//var是一个可变变量，这是一个可以通过重新分配来更改为另一个值的变量。
                    intent.putExtra("extra_data", b.ResultObj.AccessToken)
                    startActivity(intent)//启动所关联的活动项,通过startActivity()来执行这个intent
                    } else {
                        //当Toast在子线程中使用时，必须加上Looper.prepare()和Looper.loop()
                        Looper.prepare()
                        Toast.makeText(applicationContext, "${b.Msg}", Toast.LENGTH_SHORT).show()
                        Looper.loop()
                    }
//                ACCESSTOKEN.setText("${b.ResultObj.AccessToken}")
            } else {
                Toast.makeText(applicationContext, "数据提交失败", Toast.LENGTH_SHORT).show()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1 -> if (resultCode == RESULT_OK) {
                val returnedData = data?.getStringExtra("data_return")
//                Toast.makeText(this,returnedData,Toast.LENGTH_SHORT).show()
                Log.v("Main_activity", "returned data is $returnedData")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {//用于进程销毁后恢复数据
        super.onSaveInstanceState(outState, outPersistentState)
        val tempData="Something you just typed"
        outState.putString("data_key", tempData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)//引入xml视图
        if (savedInstanceState !=null){
            val tempData=savedInstanceState.getString("data_key")
            Log.d(TAG, "tempData")
        }

        val prefs = getSharedPreferences("MYdata", Context.MODE_PRIVATE)
        val isremember=prefs.getBoolean("remember_password", false)
        val username = prefs.getString("username", "")
        val password = prefs.getString("password", "")
//        Log.d(TAG, "$isremember")
        if(isremember){
            editTextusername.setText(username)
            editTextTextPassword.setText(password)
            remember_password.isChecked=true
        }
        Log.d(TAG, "onCreate:创造进程")
//        Log.d("Main_activity", "Task id is $taskId")
    }

    override fun onStart() {
        super.onStart()
//        Log.d(TAG, "onStart:可见进程")
    }

    override fun onResume() {
        super.onResume()
//        Log.d(TAG, "onResume:准备交互进程")
    }

    override fun onPause() {
        super.onPause()
//        Log.d(TAG, "onPause:暂停进程")
    }

    override fun onStop() {
        super.onStop()
//        Log.d(TAG, "onStop:暂停进程,与onPause基本相同,但如果进程为对话框则不会执行")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy:销毁进程")
    }

    override fun onRestart() {
        super.onRestart()
//        Log.d(TAG, "onRestart:重启进程")
    }
}

object NetUtils {
    fun readBytes(`is`: InputStream): ByteArray? {
        try {
            val buffer = ByteArray(1024)
            var len = -1
            val baos = ByteArrayOutputStream()
            while (`is`.read(buffer).also { len = it } != -1) {
                baos.write(buffer, 0, len)
            }
            baos.close()
            return baos.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun readString(`is`: InputStream): String {
        return String(readBytes(`is`)!!)
    }
}