package org.flickit.assessment.kit.adapter.out.persistence.kitdsl.convertor;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;

import java.util.*;

import static org.flickit.assessment.kit.adapter.out.persistence.kitdsl.convertor.ExcelToDslModelConverter.getCellDouble;
import static org.flickit.assessment.kit.adapter.out.persistence.kitdsl.convertor.ExcelToDslModelConverter.getCellString;

public class AnswerOptionsConvertor {

    static final int START_ROW = 1;
    static final int RANGE_NAME_COL = 0;
    static final int CAPTION_COL = 1;
    static final int VALUE_COL = 2;

    static Map<String, List<AnswerOptionDslModel>> convert(Sheet sheet) {
        Map<String, List<AnswerOptionDslModel>> result = new LinkedHashMap<>();
        String currentRange = null;
        int index = 1;

        for (int i = START_ROW; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String rangeName = getCellString(row, RANGE_NAME_COL);
            String caption = getCellString(row, CAPTION_COL);
            Double value = getCellDouble(row, VALUE_COL);

            if (rangeName != null && !rangeName.isBlank()) {
                currentRange = rangeName.trim();
                result.putIfAbsent(currentRange, new ArrayList<>());
                index = 1;
            }

            if (currentRange != null && caption != null && !caption.isBlank())
                result.get(currentRange).add(
                    AnswerOptionDslModel.builder()
                        .index(index++)
                        .caption(caption.trim())
                        .value(value)
                        .build()
                );
            }

        return result;
    }
}
