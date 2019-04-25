package com.digizen.loadingdemo

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), TextWatcher {

    private var mViewColor: Int = Color.WHITE
    private var mBgColor: Int = Color.parseColor("#17b08a")

    private var mLoadingAdapter: LoadingAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager = GridLayoutManager(this, 4)
        rv.layoutManager = layoutManager
        mLoadingAdapter = LoadingAdapter(mViewColor)
        rv.adapter = mLoadingAdapter
        rv.setBackgroundColor(mBgColor)

        ed_view_color.addTextChangedListener(this)
        ed_bg_color.addTextChangedListener(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    private fun changeColor() {
        mLoadingAdapter?.setColor(mViewColor)
        rv.setBackgroundColor(mBgColor)
    }

    private fun checkColor(s: CharSequence): String? {
        try {
            var str = ""
            if (s.startsWith("#") && s.length == 7) {
                str = s.toString()
            } else if (!s.startsWith("#") && s.length == 6) {
                str = String.format("#%s", s)
            }
            Color.parseColor(str)
            return str
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun afterTextChanged(s: Editable?) {
        var changed = false
        val viewColorString = checkColor(ed_view_color.text)
        if (!TextUtils.isEmpty(viewColorString)) {
            val viewColor = Color.parseColor(viewColorString)
            if (viewColor != mViewColor) {
                mViewColor = viewColor
                changed = true
            }
        }
        val bjColorString = checkColor(ed_bg_color.text)
        if (!TextUtils.isEmpty(bjColorString)) {
            val bgColor = Color.parseColor(bjColorString)
            if (bgColor != mBgColor) {
                mBgColor = bgColor
                changed = true
            }
        }
        if (changed) {
            changeColor()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

}
