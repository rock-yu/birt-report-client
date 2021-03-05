package birt.report.client

import birt.report.client.dto.BirtReportRequestDto
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import javax.servlet.http.HttpServletResponse


open class HttpBirtReportService(
    private val reportServiceUrl: String,
    private val activeProfile: String,
    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT_SECONDS)
        .build()) : ReportService {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    companion object {
        private val DEFAULT_CONNECTION_TIMEOUT_SECONDS: Duration = Duration.ofSeconds(30)
        private val DEFAULT_REQUEST_TIMEOUT_SECONDS: Duration = Duration.ofSeconds(90)
        private const val HTTP_CLIENT_ERROR = 4 // 4xx
        private const val HTTP_SERVER_ERROR = 5 // 5xx
    }

    override fun healthCheck(): String? {
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("$reportServiceUrl/actuator/health"))
            .build()

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body()
    }

    @Throws(IOException::class)
    override fun run(response: HttpServletResponse, reportName: String, params: ReportParameters, format: String?) {
        when (format) {
            ReportFormat.PDF ->
                runPdfReport(response, BirtReportRequestDto(this.activeProfile, reportName, params.parameterString(), "pdf"))
            ReportFormat.XLS ->
                runXlsReport(response, BirtReportRequestDto(this.activeProfile, reportName, params.parameterString(), "xls"))
            else -> throw IllegalArgumentException("invalid format: $format")
        }
    }

    private fun runPdfReport(response: HttpServletResponse, requestDto: BirtReportRequestDto) {
        val pdfData = runReport(requestDto)
        with(response) {
            contentType = "application/pdf"
            setHeader("Content-disposition", "inline; filename=${requestDto.reportName}.pdf")
            setContentLength(pdfData.size)
            outputStream.write(pdfData)
            outputStream.flush()
        }
    }

    private fun runXlsReport(response: HttpServletResponse, requestDto: BirtReportRequestDto) {
        val xlsData = runReport(requestDto)
        with(response) {
            contentType = "application/vnd.ms-excel"
            setHeader("Content-disposition", "inline; filename=${requestDto.reportName}.xlsx")
            setContentLength(xlsData.size)
            outputStream.write(xlsData)
            outputStream.flush()
        }
    }

    private fun runReport(requestDto: BirtReportRequestDto): ByteArray {
        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$reportServiceUrl/run"))
            .timeout(DEFAULT_REQUEST_TIMEOUT_SECONDS)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestDto.asJsonString()))
            .build()

        val response: HttpResponse<ByteArray> =
            httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray())
        val responseBody: ByteArray = response.body()

        when (response.statusCode() / 100) {
            HTTP_CLIENT_ERROR -> {
                val errorMessage = String(responseBody)
                logger.warn("failed to run report, bad request, requestBody: '$errorMessage'")
                throw IllegalArgumentException("Fail to generate report, bad request: $errorMessage")
            }
            HTTP_SERVER_ERROR -> {
                val errorMessage = String(responseBody)
                logger.error("failed to run report, server error, requestBody: '$errorMessage'")
                throw RuntimeException("Fail to generate report, server error: $errorMessage")
            }
            else -> {
                logger.info("requested:$requestDto, size:${responseBody!!.size}")
                return responseBody
            }
        }

    }
}

