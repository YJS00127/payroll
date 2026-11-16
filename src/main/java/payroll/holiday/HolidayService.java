package payroll.holiday;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class HolidayService {

    @Value("${holiday.api-key}")
    private String apiKey;

    private final CacheManager cacheManager;

    private final RestClient restClient = RestClient.create("https://apis.data.go.kr");

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public boolean isHoliday(LocalDate date){
        JsonNode holidays = getHolidays(date.getYear(), date.getMonthValue());

        JsonNode items = holidays
                .path("response")
                .path("body")
                .path("items")
                .path("item");

        for(JsonNode item : items){
            LocalDate holidayDate = LocalDate.parse(
                    item.path("locdate").asString(),
                    DateTimeFormatter.BASIC_ISO_DATE);

            if(holidayDate.equals(date) && item.path("isHoliday").asString().equals("Y")){
                return true;
            }
        }
        return false;
    }
}