package payroll.common.excel;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {
    // 엑셀 양식 다운로드
    public byte[] downloadExcel(String sheetName, List<String> headers)
            throws IOException {
        try(Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream()){

            Sheet sheet = workbook.createSheet(sheetName);
            Row headerRow = sheet.createRow(0);

            for(int i=0; i<headers.size(); i++){
                headerRow.createCell(i).setCellValue(headers.get(i));
                sheet.setColumnWidth(i, 20 * 256);
            }

            workbook.write(out);

            return out.toByteArray();
        }
    }

    // 엑셀 업로드
    public List<Row> uploadExcel(MultipartFile file, List<String> headers) throws IOException{
        List<ExcelRow> rows = new ArrayList<>();

        try(InputStream in = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(in)){

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            Row headerRow = sheet.getRow(0);

            if(headerRow == null ||
                    headerRow.getLastCellNum() != headers.size()) {
                throw new IllegalArgumentException("엑셀 양식이 일치하지 않습니다");
            }

            for(int i=0; i< headers.size(); i++){
                String value = formatter.formatCellValue(headerRow.getCell(i));

                if(!headers.get(i).equals(value)){
                    throw new IllegalArgumentException("엑셀 헤더가 일치하지 않습니다: " + headers.get(i));
                }
            }

            // 데이터 읽기
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                List<String> values = new ArrayList<>();

                for (int j = 0; j < headers.size(); j++) {

                    String value = formatter.formatCellValue(
                            row.getCell(j));

                    values.add(value);
                }

                // 모든 셀이 비어 있는 행은 제외
                if (values.stream().allMatch(String::isBlank)) {
                    continue;
                }

                rows.add(new ExcelRow(i + 1, values));
            }
        }

        return rows;
    }
