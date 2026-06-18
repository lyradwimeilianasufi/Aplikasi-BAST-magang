package com.example.aplikasibast.core.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private const val DB_FORMAT = "yyyy-MM-dd"
    // EEEE = Senin, dd = 08, MMM = Jun, yyyy = 2026
    private const val UI_FORMAT = "EEEE, dd MMM yyyy"

    fun getTodayDb(): String {
        return SimpleDateFormat(DB_FORMAT, Locale.US).format(Calendar.getInstance().time)
    }

    fun formatToUi(dateDb: String?): String {
        if (dateDb == null || dateDb == "-" || dateDb.isEmpty()) return "-"
        return try {
            val date = SimpleDateFormat(DB_FORMAT, Locale.US).parse(dateDb)
            // Locale("id", "ID") memastikan nama hari & bulan dalam Bahasa Indonesia
            SimpleDateFormat(UI_FORMAT, Locale("id", "ID")).format(date!!)
        } catch (e: Exception) {
            dateDb
        }
    }

    fun calculateDays(start: String, end: String): Long {
        return try {
            val sdf = SimpleDateFormat(DB_FORMAT, Locale.US)
            val d1 = sdf.parse(start)
            val d2 = sdf.parse(end)
            val diff = d2!!.time - d1!!.time
            if (diff < 0) return 0
            TimeUnit.MILLISECONDS.toDays(diff) + 1
        } catch (e: Exception) {
            1
        }
    }
}
