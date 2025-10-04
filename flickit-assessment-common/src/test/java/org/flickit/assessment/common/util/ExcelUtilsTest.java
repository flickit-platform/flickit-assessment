package org.flickit.assessment.common.util;

import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExcelUtilsTest {

    private Sheet createSheet() throws IOException {
        try (Workbook wb = WorkbookFactory.create(false)) {
            return wb.createSheet();
        }
    }

    @Test
    void testExcelUtils_IsBlankRow() throws IOException {
        Sheet sheet = createSheet();

        assertTrue(ExcelUtils.isBlankRow(null));

        Row emptyRow = sheet.createRow(0);
        assertTrue(ExcelUtils.isBlankRow(emptyRow));

        Row blankCellsRow = sheet.createRow(1);
        blankCellsRow.createCell(0, CellType.BLANK);
        blankCellsRow.createCell(1, CellType.BLANK);
        assertTrue(ExcelUtils.isBlankRow(blankCellsRow));

        Row nonBlankRow = sheet.createRow(2);
        nonBlankRow.createCell(0, CellType.STRING).setCellValue("   ");
        nonBlankRow.createCell(1, CellType.STRING).setCellValue("data");
        assertFalse(ExcelUtils.isBlankRow(nonBlankRow));

        Row mixedRow = sheet.createRow(3);
        mixedRow.createCell(0, CellType.BLANK);
        mixedRow.createCell(1, CellType.STRING).setCellValue(" ");
        mixedRow.createCell(2, CellType.NUMERIC).setCellValue(0);
        assertFalse(ExcelUtils.isBlankRow(mixedRow));
    }

    @Test
    void testExcelUtils_GetCellString() throws IOException {
        Sheet sheet = createSheet();
        Row row = sheet.createRow(0);

        assertNull(ExcelUtils.getCellString(row, null));
        assertNull(ExcelUtils.getCellString(row, -1));

        row.createCell(0, CellType.STRING).setCellValue("  Hello  ");
        assertEquals("Hello", ExcelUtils.getCellString(row, 0));

        row.createCell(1, CellType.NUMERIC).setCellValue(123.45);
        assertEquals("123.45", ExcelUtils.getCellString(row, 1));

        row.createCell(2, CellType.BLANK);
        assertEquals("", ExcelUtils.getCellString(row, 2));

        assertNull(ExcelUtils.getCellString(row, 3));
    }

    @Test
    void testExcelUtils_GetCellInteger() throws IOException {
        Sheet sheet = createSheet();

        Row row = sheet.createRow(0);

        assertNull(ExcelUtils.getCellInteger(row, null));
        assertNull(ExcelUtils.getCellInteger(row, -1));

        assertNull(ExcelUtils.getCellInteger(row, 0));

        row.createCell(0, CellType.NUMERIC).setCellValue(42.0);
        assertEquals(42, ExcelUtils.getCellInteger(row, 0));

        row.createCell(1, CellType.STRING).setCellValue(" 123 ");
        assertEquals(123, ExcelUtils.getCellInteger(row, 1));

        row.createCell(2, CellType.STRING).setCellValue("abc");
        assertNull(ExcelUtils.getCellInteger(row, 2));

        row.createCell(3, CellType.BLANK);
        assertNull(ExcelUtils.getCellInteger(row, 3));

        row.createCell(4, CellType.BOOLEAN).setCellValue(true);
        assertNull(ExcelUtils.getCellInteger(row, 4));

        row.createCell(5, CellType.FORMULA).setCellValue(true);
        assertNull(ExcelUtils.getCellInteger(row, 5));
    }

    @Test
    void testExcelUtils_GetCellDouble() throws IOException {
        Sheet sheet = createSheet();
        Row row = sheet.createRow(0);

        assertNull(ExcelUtils.getCellDouble(null, 0));

        assertNull(ExcelUtils.getCellDouble(row, 0));

        row.createCell(0, CellType.NUMERIC).setCellValue(42.5);
        assertEquals(42.5, ExcelUtils.getCellDouble(row, 0));

        row.createCell(1, CellType.STRING).setCellValue(" 123.45 ");
        assertEquals(123.45, ExcelUtils.getCellDouble(row, 1));

        row.createCell(2, CellType.STRING).setCellValue("abc");
        assertNull(ExcelUtils.getCellDouble(row, 2));

        row.createCell(3, CellType.FORMULA).setCellFormula("1+1");
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateFormulaCell(row.getCell(3));
        assertEquals(2.0, ExcelUtils.getCellDouble(row, 3));

        row.createCell(4, CellType.BLANK);
        assertNull(ExcelUtils.getCellDouble(row, 4));

        row.createCell(5, CellType.BOOLEAN).setCellValue(true);
        assertNull(ExcelUtils.getCellDouble(row, 5));
    }

    @Test
    void testGetSheetHeader() throws IOException {
        Sheet sheet = createSheet();
        Row headerRow = sheet.createRow(0);

        headerRow.createCell(0, CellType.STRING).setCellValue(" Name ");
        headerRow.createCell(1, CellType.STRING).setCellValue("Age");
        headerRow.createCell(2, CellType.STRING).setCellValue("  Email");
        headerRow.createCell(3, CellType.STRING).setCellValue("Address");
        headerRow.createCell(4, CellType.BLANK);

        Map<String, Integer> headerMap = ExcelUtils.getSheetHeader(sheet, 0, 0, 3);

        assertEquals(4, headerMap.size());
        assertEquals(0, headerMap.get("Name"));
        assertEquals(1, headerMap.get("Age"));
        assertEquals(2, headerMap.get("Email"));
        assertEquals(3, headerMap.get("Address"));

        Map<String, Integer> subHeader = ExcelUtils.getSheetHeader(sheet, 0, 1, 2);
        assertEquals(2, subHeader.size());
        assertEquals(1, subHeader.get("Age"));
        assertEquals(2, subHeader.get("Email"));
    }
}



