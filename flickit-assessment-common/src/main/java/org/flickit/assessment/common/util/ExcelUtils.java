package org.flickit.assessment.common.util;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@UtilityClass
public class ExcelUtils {

    public static boolean isBlankRow(Row row) {
        return Optional.ofNullable(row)
            .map(r -> StreamSupport.stream(r.spliterator(), false)
                .allMatch(cell -> cell == null
                    || cell.getCellType() == CellType.BLANK
                    || cell.toString().trim().isEmpty()))
            .orElse(true);
    }

    public static String getCellString(Row row, Integer idx) {
        if (idx == null || idx < 0) return null;

        var result = getCellStringValue(row.getCell(idx));
        if (result == null || result.isEmpty())
            return null;
        return result.trim();
    }

    public static Integer getCellInteger(Row row, Integer idx) {
        if (idx == null || idx < 0) return null;
        Cell cell = row.getCell(idx);
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    public static Double getCellDouble(Row row, int columnIndex) {
        if (row == null) return null;
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (IllegalStateException e) {
                    return null;
                }
            case BLANK:
            default:
                return null;
        }
    }

    public static Map<String, Integer> getSheetHeaderWithoutFormula(Sheet sheet, int rowNum) {
        Row headerRow = sheet.getRow(rowNum);

        Predicate<Cell> cellIsValid = cell ->
            cell != null
                && cell.getCellType() != CellType.BLANK
                && cell.getCellType() != CellType.FORMULA;

        int last = headerRow.getLastCellNum();
        return IntStream.range(0, last)
            .mapToObj(headerRow::getCell)
            .filter(cellIsValid)
            .collect(Collectors.toMap(
                cell -> getCellStringValue(cell).trim(),
                Cell::getColumnIndex
            ));
    }

    public static String getCellStringValue(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case FORMULA -> switch (cell.getCachedFormulaResultType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                default -> null;
            };
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }
}
