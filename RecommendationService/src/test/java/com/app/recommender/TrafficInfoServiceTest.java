package com.app.recommender;

import com.app.recommender.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

class TrafficInfoServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private TrafficInfoService trafficInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        trafficInfoService = new TrafficInfoService(restTemplate);
    }

    @Test
    void getTrafficInfo_SuccessfulResponse() throws IOException {
        TrafficApiResponse trafficApiResponse = new TrafficApiResponse();
        trafficApiResponse.setStatus("OK");
        TrafficApiResponseRow row = new TrafficApiResponseRow();
        TrafficApiResponseElement element = new TrafficApiResponseElement();
        element.setDistance(new TrafficApiResponseDistance("10", 10000));
        element.setDuration(new TrafficApiResponseDuration("30", 30));
        element.setDurationInTraffic(new TrafficApiResponseDuration("30", 30));
        row.setElements(Collections.singletonList(element));
        trafficApiResponse.setRows(Collections.singletonList(row));

        when(restTemplate.getForObject(anyString(), any())).thenReturn(trafficApiResponse);

        Location origin = new Location("37.7749", "-122.4194");
        Location destination = new Location("34.0522", "-118.2437");
        TrafficData result = trafficInfoService.getTrafficInfo(origin, destination);

        assertEquals(10000, result.getDistance());
        assertEquals(30, result.getDuration());
        assertEquals(30, result.getDurationInTraffic());
        assertEquals("Low Traffic", result.getTrafficDetails());
    }

    @Test
    void getTrafficInfo_UnsuccessfulResponse() throws IOException {
        TrafficApiResponse trafficApiResponse = new TrafficApiResponse();
        trafficApiResponse.setStatus("INVALID_STATUS");

        when(restTemplate.getForObject(anyString(), any())).thenReturn(trafficApiResponse);

        Location origin = new Location("37.7749", "-122.4194");
        Location destination = new Location("34.0522", "-118.2437");
        TrafficData result = trafficInfoService.getTrafficInfo(origin, destination);

        assertEquals(null, result);
    }
}
