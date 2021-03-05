package birt.report.client.dto

class BirtReportRequestDto {
    constructor()
    constructor(tenant: String?, reportName: String?, reportParameters: String?, format: String?) {
        this.tenant = tenant
        this.reportName = reportName
        this.reportParameters = reportParameters
        this.format = format
    }

    var tenant: String? = null
    var reportName: String? = null
    var reportParameters: String? = null
    var format: String? = null

    fun asJsonString(): String =
        """{"tenant":"$tenant","reportName":"$reportName","reportParameters":"?${reportParameters}","format":"$format"}
        """.trimMargin()

    override fun toString(): String {
        return "BirtReportRequestDto(tenant=$tenant, reportName=$reportName, reportParameters=$reportParameters, format=$format)"
    }
}