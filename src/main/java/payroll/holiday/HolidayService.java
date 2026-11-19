package payroll.holiday;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayApiService holidayApiService;

    public JsonNode getHolidays(int year, int month){
        System.out.println(holidayApiService.getHolidays(year, month));
        return holidayApiService.getHolidays(year, month);
    }

    public boolean isHoliday(LocalDate date){
        JsonNode holidays = getHolidays(date.getYear(), date.getMonthValue());

        JsonNode items = holidays
                .path("response")
                .path("body")
                .path("items")
                .path("item");

        if(items.isObject()){
            return items.path("locdate").asString()
                    .equals(date.format(DateTimeFormatter.BASIC_ISO_DATE))
                    && items.path("isHoliday").asString().equals("Y");
        }

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