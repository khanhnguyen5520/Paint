package com.lutech.paintV3.model

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar

@SuppressLint("AppCompatCustomView")
class BrightnessSlideBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SeekBar(context, attrs, defStyleAttr) {
    init {
        max = 100
    }

    interface OnSlideBarChangeListener {
        fun onProgressChanged(slideBar: BrightnessSlideBar?, progress: Int, fromUser: Boolean)
        fun onStartTrackingTouch(slideBar: BrightnessSlideBar?)
        fun onStopTrackingTouch(slideBar: BrightnessSlideBar?)
    }
}
