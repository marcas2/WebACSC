package com.tuapp.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class PdfExportUtil {

        public static byte[] toPdf(long totalRegistros,
                                                           Map<String, Long> normalStatusMap,
                                                           Map<String, Long> categoriaMap,
                                                           Map<String, Long> focoMap,
                                                           Map<String, Long> institucionMap,
                                                           Map<String, Long> timelineMap,
                                                           Map<String, Long> valvulopathyAgeMap,
                                                           Map<String, Long> valvulopathyGenderMap,
                                                           Map<String, Long> valvulopathyDiseaseMap) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4, 40, 40, 60, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            com.itextpdf.text.Font titleFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16,
                            new BaseColor(26, 26, 46));
            com.itextpdf.text.Font sectionFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12,
                            new BaseColor(50, 102, 173));
            com.itextpdf.text.Font headerFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10,
                            BaseColor.WHITE);
            com.itextpdf.text.Font bodyFont =
                    FontFactory.getFont(FontFactory.HELVETICA, 10,
                            new BaseColor(60, 60, 60));
            com.itextpdf.text.Font totalFont =
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10,
                            new BaseColor(26, 26, 46));

            Paragraph mainTitle = new Paragraph(
                    "Reporte estadístico — Sonidos cardíacos", titleFont);
            mainTitle.setAlignment(Element.ALIGN_CENTER);
            mainTitle.setSpacingAfter(20);
            doc.add(mainTitle);

            addSection(doc, "Resumen general del sistema",
                    "Métrica", Map.of("Total de registros", totalRegistros),
                    sectionFont, headerFont, bodyFont, totalFont);

            addSection(doc, "Proporción de sonidos normales y anormales",
                    "Tipo de sonido", normalStatusMap,
                    sectionFont, headerFont, bodyFont, totalFont);

            addSection(doc, "Registros por tipo de anomalía cardíaca",
                    "Tipo de anomalía", categoriaMap,
                    sectionFont, headerFont, bodyFont, totalFont);

            addSection(doc, "Registros por foco de auscultación",
                    "Foco", focoMap,
                    sectionFont, headerFont, bodyFont, totalFont);

            addSection(doc, "Registros por institución/hospital",
                    "Institución", institucionMap,
                    sectionFont, headerFont, bodyFont, totalFont);

            addSection(doc, "Evolución mensual de registros",
                    "Periodo", timelineMap,
                    sectionFont, headerFont, bodyFont, totalFont);

            addSection(doc, "Valvulopatías por rango de edad",
                    "Rango de edad", valvulopathyAgeMap,
                    sectionFont, headerFont, bodyFont, totalFont);

            addSection(doc, "Valvulopatías por género",
                    "Género", valvulopathyGenderMap,
                    sectionFont, headerFont, bodyFont, totalFont);

            addSection(doc, "Valvulopatías y enfermedades de base",
                    "Condición", valvulopathyDiseaseMap,
                    sectionFont, headerFont, bodyFont, totalFont);

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    private static void addSection(Document doc, String title, String colLabel,
                                   Map<String, Long> data,
                                   com.itextpdf.text.Font sectionFont,
                                   com.itextpdf.text.Font headerFont,
                                   com.itextpdf.text.Font bodyFont,
                                   com.itextpdf.text.Font totalFont)
            throws DocumentException {

        Paragraph sectionTitle = new Paragraph(title, sectionFont);
        sectionTitle.setSpacingBefore(16);
        sectionTitle.setSpacingAfter(8);
        doc.add(sectionTitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 2f});

        BaseColor headerBg = new BaseColor(50, 102, 173);
        addCell(table, colLabel, headerFont, headerBg, Element.ALIGN_LEFT);
        addCell(table, "Cantidad de pacientes", headerFont, headerBg, Element.ALIGN_CENTER);

        BaseColor rowEven = new BaseColor(243, 244, 248);
        int i = 0;
        long total = 0;
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            BaseColor bg = (i % 2 == 0) ? BaseColor.WHITE : rowEven;
            addCell(table, entry.getKey(), bodyFont, bg, Element.ALIGN_LEFT);
            addCell(table, String.valueOf(entry.getValue()), bodyFont, bg, Element.ALIGN_CENTER);
            total += entry.getValue();
            i++;
        }

        BaseColor totalBg = new BaseColor(234, 238, 245);
        addCell(table, "TOTAL", totalFont, totalBg, Element.ALIGN_LEFT);
        addCell(table, String.valueOf(total), totalFont, totalBg, Element.ALIGN_CENTER);

        doc.add(table);
    }

    private static void addCell(PdfPTable table, String text,
                                com.itextpdf.text.Font font,
                                BaseColor bg, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(7);
        cell.setBorderColor(new BaseColor(220, 224, 232));
        cell.setBorderWidth(0.5f);
        table.addCell(cell);
    }
}