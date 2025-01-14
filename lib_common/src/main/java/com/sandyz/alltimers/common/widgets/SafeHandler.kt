package com.sandyz.alltimers.common.widgets

import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

//安全的handler，持有T的弱引用，可以随便使用不会内存泄漏

interface SafeHandlerCallback<T> {
    fun doSomething(msg: Message, weakReference: WeakReference<T>)
}

class SafeHandler<T>(private val weakReference: WeakReference<T>, private val safeHandlerCallback: SafeHandlerCallback<T>) : Handler() {


    class MyHandler<T>(private val weakReference: WeakReference<T>, private val callback: SafeHandlerCallback<T>) : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            callback.doSomething(msg, weakReference)
        }
    }

    fun make(): MyHandler<T> {
        return MyHandler(weakReference, safeHandlerCallback)
    }

}