package com.madfooatcom.mytestlibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.posprinter.posprinterface.IMyBinder
import net.posprinter.posprinterface.UiExecute
import net.posprinter.service.PosprinterService
import net.posprinter.utils.DataForSendToPrinterPos76
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*


class MainActivity : AppCompatActivity() {
    val ENABLE_BLUETOOTH = 1
    private var binder: IMyBinder? = null // IMyBinder接口，所有可供调用的连接和发送数据的方法都封装在这个接口内

    private var isConnect // 用来标识连接状态的一个boolean值
            = false

    private var blueadapter: BluetoothAdapter? = null
    private val deviceList_bonded =
        ArrayList<String>()

    private val data: List<Map<String, Any>> =
        ArrayList()

    private var booth: String? = null

    // bindService的参数conn
    var conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {}
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // 绑定成功
            binder = service as IMyBinder
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 绑定service，获取ImyBinder对象
        // 绑定service，获取ImyBinder对象
        val intent = Intent(this, PosprinterService::class.java)
        bindService(intent, conn, Context.BIND_AUTO_CREATE)
        // 初始化控件
        // 初始化控件


        // 给控件添加监听事件
        // 给控件添加监听事件
        connectBLE()


        webview!!.settings.javaScriptEnabled = true
        webview!!.addJavascriptInterface(WebAppInterface(this), "android")
        webview!!.loadUrl("file:///android_asset/page.html")

    }
    private inner class WebAppInterface internal constructor(var mContext: Context) {
        @JavascriptInterface
        fun print(toast: String) {
            Handler().post { sendble() }
        }

    }
    protected fun printThremal() {
        if (isConnect) {
            binder!!.writeDataByYouself(object : UiExecute {
                override fun onsucess() {}
                override fun onfailed() {}
            }) { // TODO Auto-generated method stub
                val list: MutableList<ByteArray> =
                    ArrayList()

                // 创建一段我们想打印的文本,转换为byte[]类型，并添加到要发送的数据的集合list中

                /*需要打印的数据*/
                val array =
                    ArrayList<String>()
                array.add("Test Jehad")
                array.add("Do Test")
                array.add("wert")
                array.add("eryter")
                array.add("sdgfw")
                array.add("dcbdfg")
                array.add("dxzsfgb")
                array.add("sdfgsg")
                array.add("sdfh")
                /*需要打印的数据*/list.add(DataForSendToPrinterPos76.printAndFeedLine())
                list.add(DataForSendToPrinterPos76.printAndFeedLine())
                for (str in array) {
                    val data1: ByteArray? =
                        strTobytes(str)
                    data1?.let { list.add(it) }
                    list.add(DataForSendToPrinterPos76.printAndFeedLine())
                    list.add(DataForSendToPrinterPos76.printAndFeedLine())
                }
                list.add(DataForSendToPrinterPos76.printAndFeedLine())
                list
            }
        } else {
            Toast.makeText(applicationContext, "请先连接打印机！", Toast.LENGTH_SHORT)
                .show()
        }
    }

    protected fun connectBLE() {
        setbluetooth()
    }

    protected fun setbluetooth() {
        blueadapter = BluetoothAdapter.getDefaultAdapter()

        // 确认开启蓝牙
        if (blueadapter!!.isEnabled) {
            // 请求用户开启
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(
                intent,
                ENABLE_BLUETOOTH
            )
        } else {
            // 蓝牙已开启
            showblueboothlist()
        }
    }

    private fun showblueboothlist() {
        if (!blueadapter!!.isDiscovering) {
            blueadapter!!.startDiscovery()
        }
        findAvalibleDevice()
    }

    private fun findAvalibleDevice() {
        // 获取可配对蓝牙设备
        val device = blueadapter!!.bondedDevices
        deviceList_bonded.clear()
        if (device.size > 0) {
            var map: MutableMap<String?, Any?>? = null
            // 存在已经配对过的蓝牙设备
            val it: Iterator<BluetoothDevice> = device.iterator()
            while (it.hasNext()) {
                val btd = it.next()
                deviceList_bonded.add(
                    """
                        ${btd.name}
                        ${btd.address}
                        """.trimIndent()
                )
                map = HashMap()
                map["name"] = btd.name
                map["address"] = btd.address
                booth = btd.address
            }

        } else { // 不存在已经配对过的蓝牙设备
            Toast.makeText(this, "不能匹配使用蓝牙", Toast.LENGTH_SHORT).show()
        }
    }

    public fun sendble() {
        if (booth != null) {
            binder!!.connectBtPort(booth, object : UiExecute {
                override fun onsucess() {
                    // TODO Auto-generated method stub
                    // 连接成功后在UI线程中的执行
                    isConnect = true
                    Toast.makeText(
                        applicationContext,
                        "连接成功", Toast.LENGTH_SHORT
                    ).show()

                    binder!!.acceptdatafromprinter(object : UiExecute {
                        override fun onsucess() {
                            printThremal()
                        }

                        override fun onfailed() {
                            isConnect = false
                            Toast.makeText(applicationContext, "连接已断开", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                }

                override fun onfailed() {
                    // 连接失败后在UI线程中的执行
                    isConnect = false
                    Toast.makeText(applicationContext, "连接失败", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "请先选择蓝牙设备", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 字符串转byte数组
     */
    fun strTobytes(str: String): ByteArray? {
        var b: ByteArray? = null
        var data: ByteArray? = null
        try {
            b = str.toByteArray(charset("utf-8"))
            data = String(b, Charset.forName("utf-8")).toByteArray(charset("gbk"))
        } catch (e: UnsupportedEncodingException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return data
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ENABLE_BLUETOOTH && resultCode == Activity.RESULT_OK) {
            showblueboothlist()
        }
    }


    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
        if (isConnect == true) {
            binder!!.disconnectCurrentPort(object : UiExecute {
                override fun onsucess() {}
                override fun onfailed() {}
            })
            unbindService(conn)
        }
    }
/*    inner class WebAppInterface(private var context: Context) {

        */
    /** Show a toast from the web page *//*
        @JavascriptInterface
        fun nextScreen(id: String) {
            Toast. makeText (context, "Login clicked" , Toast. LENGTH_SHORT ).show() ;
        }
    }*/
}
