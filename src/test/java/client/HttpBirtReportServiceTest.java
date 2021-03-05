package client;

import birt.report.client.HttpBirtReportService;
import birt.report.client.ReportFormat;
import birt.report.client.ReportParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpBirtReportServiceTest {

    private HttpBirtReportService service;
    private HttpClient mockHttpClient = mock(HttpClient.class);
    private HttpResponse mockHttpResponse = mock(HttpResponse.class);
    private byte[] data = "report binaries".getBytes();

    @BeforeEach
    public void setUp() {
        this.service = new HttpBirtReportService(
                "http://localhost:9999/report-service",
                "vit", mockHttpClient);
    }

    @Test
    void testRunReport() throws IOException, InterruptedException {
        when(mockHttpResponse.body()).thenReturn(data);
        when(mockHttpClient.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);

        MockHttpServletResponse response = new MockHttpServletResponse();

        service.run(response, "EndOfDayCollectionReport", ReportParameters.Companion.getEMPTY(), ReportFormat.PDF);

        assertEquals("application/pdf", response.getContentType());
        assertEquals(data.length, response.getContentLength());
        assertEquals("inline; filename=EndOfDayCollectionReport.pdf", response.getHeader("Content-disposition"));
        assertArrayEquals(data, response.getContentAsByteArray());
    }
}