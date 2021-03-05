package birt.report.client

import java.io.IOException
import javax.servlet.http.HttpServletResponse

interface ReportService {
    @Throws(IOException::class)
    fun run(response: HttpServletResponse, reportName: String, params: ReportParameters,
            format: String? = ReportFormat.PDF
    )

    fun healthCheck(): String?
}
