package payroll.common.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ExcelRow {
    private int rowNum;
    private List<String> values;
}
