package birt.report.client

import birt.report.client.util.Dates
import java.time.LocalDate
import java.util.*
import kotlin.collections.LinkedHashMap

class ReportParameters
    private constructor(val parameterString: String) {

    data class Builder(private var iniParams: MutableMap<String, Any?>) {
        constructor(pair: Pair<String, Any?>) : this(LinkedHashMap<String, Any?>(mapOf(pair)))
        constructor() : this(LinkedHashMap<String, Any?>())

        fun add(name: String, value: Any?) = apply {
            this.iniParams[name] = value
        }

        fun add(pair: Pair<String, Any?>) = apply {
            this.iniParams[pair.first] = pair.second
        }

        private fun convertValueAsString(value: Any?): String {
            return when (value) {
                is Date -> Dates.toDateString(value)
                is LocalDate -> Dates.toDateString(value)
                is IntArray -> value.joinToString(separator = ",")
                is Array<*> -> value.joinToString(separator = ",")
                else -> value?.toString().orEmpty()
            }
        }

        fun build() =
                ReportParameters(iniParams.map { it.key + "=" + convertValueAsString(it.value) }.joinToString(separator = "&"))
    }

    fun parameterString() = parameterString
    override fun toString() = parameterString


    companion object {
        val EMPTY = Builder().build()
    }
}