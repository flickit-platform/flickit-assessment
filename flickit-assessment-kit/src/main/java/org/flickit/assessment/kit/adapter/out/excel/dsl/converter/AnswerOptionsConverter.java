package org.flickit.assessment.kit.adapter.out.excel.dsl.converter;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;

import java.util.*;

import static org.flickit.assessment.common.util.ExcelUtils.*;

@UtilityClass
public class AnswerOptionsConverter {

    private static final int HEADER_ROW_INDEX = 0;

    private static final String RANGE_NAME = "Range Name";
    private static final String TITLE = "Option Title";
    private static final String VALUE = "Option Value";

    public static Map<String, List<AnswerOptionDslModel>> convert(Sheet sheet) {
        var columnMap = getSheetHeaderWithoutFormula(sheet, HEADER_ROW_INDEX);
        var rangeCodeToOptionsMap = new LinkedHashMap<String, List<AnswerOptionDslModel>>();

        String currentRange = null;
        int index = 1;

        for (Row row : sheet) {
            if (row.getRowNum() == HEADER_ROW_INDEX) continue;
            var rangeCode = getCellString(row, columnMap.get(RANGE_NAME));
            var title = getCellString(row, columnMap.get(TITLE));

            if (isNotBlank(rangeCode)) {
                currentRange = rangeCode.trim();
                rangeCodeToOptionsMap.putIfAbsent(currentRange, new ArrayList<>());
                index = 1;
            }

            if (isNotBlank(currentRange) && isNotBlank(title))
                rangeCodeToOptionsMap.get(currentRange).add(
                    AnswerOptionDslModel.builder()
                        .index(index++)
                        .caption(title.trim())
                        .value(getCellDouble(row, columnMap.get(VALUE)))
                        .build()
                );
        }

        return rangeCodeToOptionsMap;
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }
}
