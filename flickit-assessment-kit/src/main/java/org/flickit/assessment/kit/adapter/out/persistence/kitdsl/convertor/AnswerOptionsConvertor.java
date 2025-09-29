package org.flickit.assessment.kit.adapter.out.persistence.kitdsl.convertor;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;

import java.util.*;
import java.util.stream.IntStream;

import static org.flickit.assessment.kit.adapter.out.persistence.kitdsl.convertor.ExcelToDslModelConverter.getCellDouble;
import static org.flickit.assessment.kit.adapter.out.persistence.kitdsl.convertor.ExcelToDslModelConverter.getCellString;

public class AnswerOptionsConvertor {

    static final int START_ROW = 1;
    static final int RANGE_NAME_COL = 0;
    static final int CAPTION_COL = 1;
    static final int VALUE_COL = 2;

    private record RowInfo(String rangeName, String caption, Double value) {}

    static Map<String, List<AnswerOptionDslModel>> convertAnswerOptions(Sheet sheet) {

        List<RowInfo> rows = IntStream.rangeClosed(START_ROW, sheet.getLastRowNum())
            .mapToObj(sheet::getRow)
            .filter(Objects::nonNull)
            .map(row -> new RowInfo(
                getCellString(row, RANGE_NAME_COL),
                getCellString(row, CAPTION_COL),
                getCellDouble(row, VALUE_COL)
            ))
            .toList();

        Map<String, List<AnswerOptionDslModel>> result = new LinkedHashMap<>();
        String currentRange = null;
        int index = 1;

        for (RowInfo row : rows) {
            if (row.rangeName() != null && !row.rangeName().isBlank()) {
                currentRange = row.rangeName().trim();
                result.putIfAbsent(currentRange, new ArrayList<>());
                index = 1;
            }

            if (currentRange != null && row.caption() != null && !row.caption().isBlank()) {
                result.get(currentRange).add(
                    AnswerOptionDslModel.builder()
                        .index(index++)
                        .caption(row.caption().trim())
                        .value(row.value())
                        .build()
                );
            }
        }

        return result;
    }
}
