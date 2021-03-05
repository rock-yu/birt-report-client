package birt.report.client.util

object StringUtil {
    fun camelCase(s: String): String {
        return s.replace(String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])").toRegex(), " ")
    }
}