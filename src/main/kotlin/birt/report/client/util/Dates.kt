package birt.report.client.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object Dates {

    fun toDateString(date: LocalDate?): String {
        return if (date == null) "" else date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    fun toDateString(date: Date?): String {
        return if (date == null) ""  else SimpleDateFormat("dd/MM/yyyy").format(date)
    }

    fun toDate(date: LocalDate?): Date? {
        return if (date == null) null else Date(getTime(date))
    }

    fun date(year: Int, month: Int, day: Int): Date {
        val localDate = LocalDate.of(year, month, day)
        return toDate(localDate)!!
    }

    private fun getTime(date: LocalDate): Long {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
