package com.tuapp.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class ExcelExportUtil {

    public static byte[] toExcel(Map<String, Long> ageMap,
                                 Map<String, Long> genderMap,
                                 Map<String, Long> diseaseMap) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Reporte Valvulopatías");
            sheet.setColumnWidth(0, 9000);
            sheet.setColumnWidth(1, 5000);

            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Font hFont = wb.createFont();
            hFont.setColor(IndexedColors.WHITE.getIndex());
            hFont.setBold(true);
            headerStyle.setFont(hFont);

            CellStyle titleStyle = wb.createCellStyle();
            Font tFont = wb.createFont();
            tFont.setBold(true);
            tFont.setFontHeightInPoints((short) 12);
            titleStyle.setFont(tFont);

            CellStyle boldStyle = wb.createCellStyle();
            Font boldFont = wb.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            int rowNum = 0;
            rowNum = writeSection(sheet, rowNum, "Valvulopatías por rango de edad",
                    "Rango de edad", ageMap, headerStyle, titleStyle, boldStyle);
            rowNum++;
            rowNum = writeSection(sheet, rowNum, "Valvulopatías por género",
                    "Género", genderMap, headerStyle, titleStyle, boldStyle);
            rowNum++;
            writeSection(sheet, rowNum, "Valvulopatías y enfermedades de base",
                    "Condición", diseaseMap, headerStyle, titleStyle, boldStyle);

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel", e);
        }
    }

    private static int writeSection(Sheet sheet, int startRow, String title,
                                    String colLabel, Map<String, Long> data,
                                    CellStyle headerStyle, CellStyle titleStyle,
                                    CellStyle boldStyle) {
        Row titleRow = sheet.createRow(startRow++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(titleStyle);

        Row header = sheet.createRow(startRow++);
        Cell h1 = header.createCell(0);
        h1.setCellValue(colLabel);
        h1.setCellStyle(headerStyle);
        Cell h2 = header.createCell(1);
        h2.setCellValue("Cantidad de pacientes");
        h2.setCellStyle(headerStyle);

        long total = 0;
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            Row row = sheet.createRow(startRow++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
            total += entry.getValue();
        }

        Row totalRow = sheet.createRow(startRow++);
        Cell tLabel = totalRow.createCell(0);
        tLabel.setCellValue("TOTAL");
        tLabel.setCellStyle(boldStyle);
        Cell tVal = totalRow.createCell(1);
        tVal.setCellValue(total);
        tVal.setCellStyle(boldStyle);

        return startRow;
    }
}