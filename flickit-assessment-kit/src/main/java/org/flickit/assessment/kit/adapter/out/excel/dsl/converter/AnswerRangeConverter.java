package org.flickit.assessment.kit.adapter.out.excel.dsl.converter;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AnswerRangeDslModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.flickit.assessment.common.util.ExcelUtils.*;

@UtilityClass
public class AnswerRangeConverter {

    private static final int HEADER_ROW_INDEX = 0;

    private static final String RANGE_NAME = "Range Name";
    private static final String OPTION_TITLE = "Option Title";
    private static final String OPTION_VALUE = "Option Value";

    public static List<AnswerRangeDslModel> convert(Sheet sheet) {
        var columnMap = getSheetHeaderWithoutFormula(sheet, HEADER_ROW_INDEX);
        var rangeCodeToOptionsMap = new LinkedHashMap<String, List<AnswerOptionDslModel>>();

        String currentRange = null;
        int index = 1;

        for (int i = HEADER_ROW_INDEX + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            var rangeCode = getCellString(row, columnMap.get(RANGE_NAME));
            var title = getCellString(row, columnMap.get(OPTION_TITLE));

            if (isNotBlank(rangeCode)) {
                currentRange = rangeCode.trim();
                rangeCodeToOptionsMap.putIfAbsent(currentRange, new ArrayList<>());
                index = 1;
            }

            if (isNotBlank(currentRange) && isNotBlank(title)) {
                rangeCodeToOptionsMap.get(currentRange).add(
                    AnswerOptionDslModel.builder()
                        .index(index++)
                        .caption(title.trim())
                        .value(getCellDouble(row, columnMap.get(OPTION_VALUE)))
                        .build()
                );
            }
        }

        var entries = new ArrayList<>(rangeCodeToOptionsMap.entrySet());
        return IntStream.range(0, entries.size())
            .mapToObj(i -> {
                var entry = entries.get(i);
                return AnswerRangeDslModel.builder()
                    .index(i + 1)
                    .code(entry.getKey())
                    .answerOptions(entry.getValue())
                    .build();
            })
            .collect(Collectors.toList());
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }
}
