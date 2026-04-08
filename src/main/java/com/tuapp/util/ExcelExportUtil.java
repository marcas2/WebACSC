package com.tuapp.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class ExcelExportUtil {

    public static byte[] toExcel(long totalRegistros,
                                 Map<String, Long> normalStatusMap,
                                 Map<String, Long> categoriaMap,
                                 Map<String, Long> focoMap,
                                 Map<String, Long> institucionMap,
                                 Map<String, Long> timelineMap,
                                 Map<String, Long> valvulopathyAgeMap,
                                 Map<String, Long> valvulopathyGenderMap,
                                 Map<String, Long> valvulopathyDiseaseMap) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Reporte Sonidos Cardiacos");
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
                Row summaryTitle = sheet.createRow(rowNum++);
                Cell summaryTitleCell = summaryTitle.createCell(0);
                summaryTitleCell.setCellValue("Resumen general del sistema");
                summaryTitleCell.setCellStyle(titleStyle);

                Row summary = sheet.createRow(rowNum++);
                Cell label = summary.createCell(0);
                label.setCellValue("Total de registros de sonidos cardíacos");
                label.setCellStyle(headerStyle);
                Cell value = summary.createCell(1);
                value.setCellValue(totalRegistros);
                value.setCellStyle(boldStyle);

            rowNum++;
                rowNum = writeSection(sheet, rowNum, "Proporción de sonidos normales y anormales",
                    "Tipo de sonido", normalStatusMap, headerStyle, titleStyle, boldStyle);
            rowNum++;
                rowNum = writeSection(sheet, rowNum, "Registros por tipo de anomalía cardíaca",
                    "Tipo de anomalía", categoriaMap, headerStyle, titleStyle, boldStyle);
                rowNum++;
                rowNum = writeSection(sheet, rowNum, "Registros por foco de auscultación",
                    "Foco", focoMap, headerStyle, titleStyle, boldStyle);
                rowNum++;
                rowNum = writeSection(sheet, rowNum, "Registros por institución/hospital",
                    "Institución", institucionMap, headerStyle, titleStyle, boldStyle);
                rowNum++;
                rowNum = writeSection(sheet, rowNum, "Evolución mensual de registros",
                    "Periodo", timelineMap, headerStyle, titleStyle, boldStyle);
                rowNum++;
                rowNum = writeSection(sheet, rowNum, "Valvulopatías por rango de edad",
                    "Rango de edad", valvulopathyAgeMap, headerStyle, titleStyle, boldStyle);
                rowNum++;
                rowNum = writeSection(sheet, rowNum, "Valvulopatías por género",
                    "Género", valvulopathyGenderMap, headerStyle, titleStyle, boldStyle);
                rowNum++;
                writeSection(sheet, rowNum, "Valvulopatías y enfermedades de base",
                    "Condición", valvulopathyDiseaseMap, headerStyle, titleStyle, boldStyle);

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