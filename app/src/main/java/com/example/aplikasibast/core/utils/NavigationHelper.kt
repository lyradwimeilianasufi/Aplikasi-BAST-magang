package com.example.aplikasibast.core.utils

import android.app.Activity
import android.content.Intent
import com.example.aplikasibast.R
import com.example.aplikasibast.features.attendance.presentation.activity.KehadiranActivity
import com.example.aplikasibast.features.attendance.presentation.activity.RiwayatKehadiranActivity

object NavigationHelper {
    fun handleBottomNavigation(activity: Activity, itemId: Int): Boolean {
        return when (itemId) {
            R.id.nav_beranda -> {
                if (activity !is MainActivity) {
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    activity.startActivity(intent)
                }
                true
            }
            R.id.nav_kehadiran -> {
                if (activity !is KehadiranActivity) {
                    val intent = Intent(activity, KehadiranActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    activity.startActivity(intent)
                }
                true
            }
            R.id.nav_riwayat -> {
                if (activity !is RiwayatKehadiranActivity) {
                    val intent = Intent(activity, RiwayatKehadiranActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    activity.startActivity(intent)
                }
                true
            }
            else -> false
        }
    }
}
