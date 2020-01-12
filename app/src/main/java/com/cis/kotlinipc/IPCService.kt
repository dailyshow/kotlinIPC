package com.cis.kotlinipc

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import android.util.Log

class IPCService : Service() {
    var value = 0
    var thread: ThreadClass? = null
    var binder : IBinder = LocalBinder()

    // 외부에서 서비스에 접속하게 되면 onBind 메소드가 호출되고 이 메소드가 리턴하는 IBinder 클래스 타입의 객체를 접속하는 activity쪽에서 받아낼 수 있다.
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        thread = ThreadClass()
        thread?.start()

        return super.onStartCommand(intent, flags, startId)
    }

    inner class ThreadClass : Thread() {
        override fun run() {
            while (true) {
                SystemClock.sleep(1000)
                Log.d("test", "value : ${value}")
                value++
            }
            Log.d("test", "service 종료")
        }
    }

    // Binder는 activity와 service 의 중간 매개체 역할을 해준다.
    inner class LocalBinder : Binder() {
        fun getService() : IPCService {
            return this@IPCService
        }
    }

    fun getNumber() : Int {
        return value
    }
}
