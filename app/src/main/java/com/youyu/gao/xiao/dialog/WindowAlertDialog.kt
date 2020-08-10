package com.youyu.gao.xiao.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.*
import android.widget.TextView
import com.youyu.gao.xiao.R
import com.youyu.gao.xiao.applicatioin.MainApplication
import com.youyu.gao.xiao.utils.LogUtil


@SuppressLint("StaticFieldLeak")
object WindowAlertDialog : View.OnClickListener {

    private val TAG = "WindowAlertDialog"

    var isShowing: Boolean = false
    private var sContext: Context? = null
    private var sWindowManager: WindowManager? = null
    private lateinit var sView: ViewGroup

    lateinit var tv_content_title: TextView
    lateinit var tv_content: TextView
    lateinit var tv_agree: TextView


    fun showWindowDialog() {
        sContext = MainApplication.getInstance().applicationContext
        sWindowManager = sContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val inflater = sContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        sView = inflater.inflate(R.layout.window_tips_layout, null, false) as ViewGroup

        tv_content_title = sView!!.findViewById<View>(R.id.tv_content_title) as TextView
        tv_content = sView!!.findViewById<View>(R.id.tv_content) as TextView
        tv_agree = sView!!.findViewById<View>(R.id.tv_agree) as TextView


        tv_agree.setOnClickListener(this)

        val params = getLayoutParams(true)

        if (sView.parent != null) {
            sWindowManager!!.removeViewImmediate(sView)
        }

        try {
            LogUtil.showDLog(TAG, "sWindowManager!!.addView(sView, params)")
            sWindowManager!!.addView(sView, params)
            isShowing = true
        } catch (e: Exception) {
            isShowing = false
            LogUtil.showDLog(TAG, "sWindowManager!!.addView(sView, params) exception: ${e.message}")
        }
    }

    val shape = GradientDrawable().apply {
        cornerRadius = 30f
        setColor(Color.parseColor("#e5e5e5"))
    }

    private fun getLayoutParams(hasFocus: Boolean): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }

        if (hasFocus) {
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
        } else {
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
        }

        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT

        params.format = PixelFormat.TRANSLUCENT

        params.gravity = Gravity.CENTER
        return params
    }

    /**
     * 隐藏Dialog
     */
    fun hideWindowDialog() {
        //隐藏弹窗时候先隐藏蒙层
        if (isShowing && sView != null) {
            try {
                LogUtil.showDLog(TAG, "hideWindowDialog removeView")
                removeView()
                isShowing = false
            } catch (e: Exception) {
                LogUtil.showDLog(TAG, "hideWindowDialog  error1 $e.localizedMessage, ${e.message}")
                try {
                    removeView()
                    isShowing = false
                } catch (e: Exception) {
                    LogUtil.showDLog(TAG, "hideWindowDialog  error2 $e.localizedMessage, ${e.message}")
                }
            }
        }
    }

    /**
     * sWindowManager?.removeView(sView)执行后，发现有未remove掉的现象
     */
    private fun removeView() {
        if (sWindowManager == null) {
            LogUtil.showDLog(TAG, "sWindowManager == null")
            sWindowManager = sContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
        sWindowManager!!.removeView(sView)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_agree -> {
                LogUtil.showDLog(TAG, "R.id.tv_agree")
                hideWindowDialog()
                return
            }
        }
    }


}
