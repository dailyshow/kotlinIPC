package com.cis.kotlinipc

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import kotlinx.android.synthetic.main.activity_main.*

// IPC : 현재 실행되고 있는 서비스에 접속할 수 있는 개념
class MainActivity : AppCompatActivity() {
    var ipcService : IPCService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, IPCService::class.java)
        if (isServiceRunning("com.cis.kotlinipc.IPCService") == false) {
            startService(intent)
        }

        // Context.BIND_AUTO_CREATE 만 써도 서비스가 실행되고 연결이 되지만 앱이 종료된 이후에 서비스가 죽게 되어서
        // 추후에 앱을 실행했을 때 처리해줘야 하는 절차가 복잡해지게 된다.
        // 그렇기 때문에 위에서 실행한 것처럼 서비스를 먼저 실행시켜주고 아래 Context.BIND_AUTO_CREATE 를 해주는 것이 좋다.
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

        btn.setOnClickListener { view ->
            val value = ipcService?.getNumber()
            tv.text = "value : ${value}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mConnection)
    }

    fun isServiceRunning(name: String): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // manager.getRunningServices(Int.MAX_VALUE) : 현재 동작중인 모든 서비스에 대한 정보가 받아진다.
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Int.MAX_VALUE)) {
            if (service.service.className.equals(name)) {
                return true
            }
        }

        return false
    }

    private val mConnection = object : ServiceConnection {
        // 서비스에 접속했을 때 호출되는 메소드
        // IPCService.kt 에 onBind() 메소드가 반환하는 객체의 주소값이 service: IBinder? 로 들어오게 된다.
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as IPCService.LocalBinder
            ipcService = binder.getService()
        }

        // 서비스에 해제 되었을 때 호출되는 메소드
       override fun onServiceDisconnected(name: ComponentName?) {
            ipcService = null
        }

    }
}
