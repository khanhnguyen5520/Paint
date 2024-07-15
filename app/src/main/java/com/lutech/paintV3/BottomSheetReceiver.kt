package com.lutech.paintV3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lutech.paintV3.UI.Activity.MainActivity

class BottomSheetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == MainActivity.ACTION_SHOW_BOTTOM_SHEET) {
            val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
                action = MainActivity.ACTION_SHOW_BOTTOM_SHEET
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context?.startActivity(mainActivityIntent)
        }
    }
}
