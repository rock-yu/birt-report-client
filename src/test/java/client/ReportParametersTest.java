package client;

import birt.report.client.ReportParameters;
import birt.report.client.util.Dates;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportParametersTest {

    @Test
    void buildWithInitialMap() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userID", 123);
        params.put("date", "01/05/2018");

        assertEquals("userID=123&date=01/05/2018", new ReportParameters.Builder(params).build().getParameterString());
    }

    @Test
    void buildWithInitialMapWithDateTime() {
        assertEquals("userID=123&date=01/05/2018", new ReportParameters
                        .Builder()
                        .add("userID", 123)
                        .add("date", LocalDate.of(2018, 5, 1))
                        .build().getParameterString());
    }

    @Test
    void buildEmptyParam() {
        String emptyJoinedParameters = new ReportParameters.Builder().build().getParameterString();
        assertEquals("", emptyJoinedParameters);
    }

    @Test
    void buildWithIntAndDateParam() {
        ReportParameters parameters =
                new ReportParameters
                    .Builder()
                    .add("userID", 123)
                    .add("date", Dates.INSTANCE.date(2018, 5, 1))
                    .build();

        assertEquals("userID=123&date=01/05/2018", parameters.getParameterString());
        assertEquals("userID=123&date=01/05/2018", parameters.toString());
    }

    @Test
    void buildWithIntAndLocalDate() {
        assertEquals("userID=123&date=01/05/2018", new ReportParameters
                        .Builder()
                        .add("userID", 123)
                        .add("date", LocalDate.of(2018, 5, 1))
                        .build().getParameterString());
    }

    @Test
    void buildWithNullDateParam() {
        assertEquals("userID=123&date=", new ReportParameters
                        .Builder()
                        .add("userID", 123)
                        .add("date", null)
                        .build().getParameterString());
    }

    @Test
    void buildWithIntArrayParam() {
        assertEquals("enrolledByUserId=11,12,13", new ReportParameters
                        .Builder()
                        .add("enrolledByUserId", new int[] {11, 12, 13})
                        .build().getParameterString());
    }

    @Test
    void birtWithStringArray() {
        assertEquals("names=a,b,c", new ReportParameters
                        .Builder()
                        .add("names", new String[] {"a", "b", "c"})
                        .build().getParameterString());
    }
}