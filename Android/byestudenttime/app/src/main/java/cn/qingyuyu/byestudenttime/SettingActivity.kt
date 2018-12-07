package cn.qingyuyu.byestudenttime


import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.beardedhen.androidbootstrap.TypefaceProvider
import android.util.Log
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_setting.*;
import java.io.OutputStream
import java.util.*

class SettingActivity : AppCompatActivity() {
    var MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"
    private var blueSocket: BluetoothSocket? = null
    private var out: OutputStream? = null
    private var running = true
    lateinit var bluetoothAdapter: BluetoothAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TypefaceProvider.registerDefaultIconSets()
        setContentView(R.layout.activity_setting)

        val ab = AlertDialog.Builder(this)
        val link = TextView(this)
        link.text = "正在连接蓝牙。。。"
        val pb = ProgressBar(this)
        val ll = LinearLayout(this)
        ll.orientation = LinearLayout.VERTICAL
        ll.addView(link)
        ll.addView(pb)
        ab.setView(ll)
        val ad = ab.create()
        ad.setCancelable(false)//不可以取消
        ad.setCanceledOnTouchOutside(false) //点击外面区域不会让dialog消失
        ad.show()

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        Thread(Runnable {
            try {
                val i = intent
                val address: String = i.getStringExtra("address")
                val btDev: BluetoothDevice
                btDev = bluetoothAdapter!!.getRemoteDevice(address)
                Thread.sleep(500)
                val uuid = UUID.fromString(MY_UUID)
                blueSocket = btDev.createRfcommSocketToServiceRecord(uuid)
                if (blueSocket == null)
                    blueSocket!!.connect()
                Thread.sleep(500)

                runOnUiThread {
                    if (!blueSocket!!.isConnected)
                        blueSocket!!.connect()
                    if (out == null) {
                        out = blueSocket!!.outputStream

                        Toast.makeText(applicationContext, "蓝牙连接成功", Toast.LENGTH_SHORT).show()
                        ad.cancel()
                    }

                }




            } catch (e: Exception) {
                Log.e("error", e.toString())
                runOnUiThread {
                    Toast.makeText(applicationContext, "蓝牙连接失败", Toast.LENGTH_SHORT).show()
                    ad.cancel()
                }

            }
        }).start()

        button.setOnClickListener {
            Toast.makeText(this,"send",Toast.LENGTH_SHORT).show()
            val s=tokenText.text.toString()+","+wifiText.text+","+passText.text
            out!!.write(s.toByteArray())
            out!!.flush()
        }



    }

    override fun onStart() {
        super.onStart()

        }

    override fun onDestroy() {
        super.onDestroy()
        running = false
        if (blueSocket != null) {
            blueSocket!!.close()
        }
    }

}
