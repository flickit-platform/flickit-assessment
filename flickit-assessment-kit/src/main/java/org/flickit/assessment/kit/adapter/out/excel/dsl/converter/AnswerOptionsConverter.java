package org.flickit.assessment.kit.adapter.out.excel.dsl.converter;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;

import java.util.*;

import static org.flickit.assessment.common.util.ExcelUtils.*;

@UtilityClass
public class AnswerOptionsConverter {

    private static final int HEADER_ROW_NUM = 0;
    private static final int HEADER_START_COL = 0;

    private static final String RANGE_NAME = "Range Name";
    private static final String TITLE = "Option Title";
    private static final String VALUE = "Option Value";

    public static Map<String, List<AnswerOptionDslModel>> convert(Sheet sheet) {
        Map<String, List<AnswerOptionDslModel>> rangeCodeToOptionsMap = new LinkedHashMap<>();
        var columnMap = getSheetHeaderWithoutFormula(sheet, HEADER_ROW_NUM, HEADER_START_COL);
        int index = 1;

        for (int i = HEADER_ROW_NUM + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String rangeName = Optional.ofNullable(getCellString(row, columnMap.get(RANGE_NAME)))
                .map(String::trim)
                .orElse("");
            if (!rangeName.isBlank()) {
                rangeCodeToOptionsMap.putIfAbsent(rangeName, new ArrayList<>());

                rangeCodeToOptionsMap.get(rangeName).add(
                    AnswerOptionDslModel.builder()
                        .index(index++)
                        .caption(getCellString(row, columnMap.get(TITLE)))
                        .value(getCellDouble(row, columnMap.get(VALUE)))
                        .build()
                );
            }
        }

        return rangeCodeToOptionsMap;
    }
}
