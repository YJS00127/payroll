package payroll.holiday;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
public class HolidayApiService {

    @Value("${holiday.api-key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create("https://apis.data.go.kr");

    @Cacheable(value = "holidays", key = "#year + '-' + #month")
    public JsonNode getHolidays(int year, int month){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
                        .queryParam("serviceKey", apiKey)
                        .queryParam("solYear", year)
                        .queryParam("solMonth", String.format("%02d", month))
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .body(JsonNode.class);
    }
}
