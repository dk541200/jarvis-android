package com.jarvis.android

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class JarvisAccessibilityService:AccessibilityService(){
    override fun onAccessibilityEvent(event:AccessibilityEvent?){ }
    override fun onInterrupt(){ }
    fun visibleText():String{
        val root:AccessibilityNodeInfo=rootInActiveWindow?:return ""
        val out=StringBuilder()
        fun walk(n:AccessibilityNodeInfo){
            n.text?.toString()?.takeIf{it.isNotBlank()}?.let{out.append(it).append('\n')}
            for(i in 0 until n.childCount)n.getChild(i)?.let{walk(it)}
        }
        walk(root)
        return out.toString().take(6000)
    }
}
