package cn.qingyuyu.byestudenttime

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.bluetooth.BluetoothAdapter

import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialoglayout.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {
    lateinit var bluetoothAdapter: BluetoothAdapter
    var running=true
    //lateinit 是延迟初始化的意思

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread(Runnable {
            var socket:Socket
            while (running)
            {
                try {
                    Log.e("request", "to server")
                    socket = Socket("wjy.qingyuyu.cn", 2333)
                    val out = socket.getOutputStream()
                    out.write("{\"token\":\"你的token\",\"need\":\"get\"}\n".toByteArray())
                    out.flush()
                    socket.shutdownOutput()
                    val i = socket.getInputStream()
                    val s = BufferedReader(InputStreamReader(i)).readLine()
                    val json = JSONObject(s)
                    runOnUiThread {
                        text.text = String(json.getString("data").toByteArray(), Charset.forName("utf-8"))
                    }
                    Thread.sleep(5000)
                }
                catch (e:Exception)
                {
                    Log.e("error", e.toString())
                }
            }
        }).start()

    }
    //重写onCreateOptionMenu(Menu menu)方法，当菜单第一次被加载时调用
        override fun   onCreateOptionsMenu(menu: Menu):Boolean {
              // Inflate the menu; this adds items to the action bar if it is present.
                //填充选项菜单（读取XML文件、解析、加载到Menu组件上）
               menuInflater.inflate(R.menu.mainmenu, menu)
                return true
            }

         //重写OptionsItemSelected(MenuItem item)来响应菜单项(MenuItem)的点击事件（根据id来区分是哪个item）
       override fun onOptionsItemSelected(item: MenuItem):Boolean {

              when (item.itemId) {
               R.id.setting->
               {
                   Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show()
                   onSetMenuClick()
               }

              else-> Toast.makeText(this, "...", Toast.LENGTH_SHORT).show()

         }
         return super.onOptionsItemSelected(item)
   }
    override fun onDestroy() {
        super.onDestroy()
        running=false

    }

    fun onSetMenuClick() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "你的手机不支持蓝牙，应用即将退出！", Toast.LENGTH_LONG).show()
            Handler().postDelayed({ this.finish() }, 1000)
        } else {
            if (!bluetoothAdapter.isEnabled) {
                bluetoothAdapter.enable()
            }//确保蓝牙已经打开

            val pairedDevices = bluetoothAdapter.bondedDevices
            val deviceName: MutableList<String> = mutableListOf()
            for (device in pairedDevices) {
                deviceName.add(device.name)
                Log.e("name:",device.name)
            }
            val aa = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, deviceName)
            val md = MyDialog(this)
            md.show()
            md.setTitleTxt(" select a device")
            md.setOkButtonTxt("ok")

            md.showListView()
            md.setListAdapter(aa)
            md.setListViewListener { _, _, i, _ ->
                val intent = Intent()
                intent.putExtra("address", pairedDevices.elementAt(i).address)
                intent.setClass(this@MainActivity, SettingActivity::class.java)
                startActivity(intent)
                md.dismiss()
            }

        }
    }
}
