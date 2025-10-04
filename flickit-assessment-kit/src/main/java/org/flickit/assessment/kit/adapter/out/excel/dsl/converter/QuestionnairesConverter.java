package org.flickit.assessment.kit.adapter.out.excel.dsl.converter;


import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.flickit.assessment.common.util.ExcelUtils.*;

@UtilityClass
public class QuestionnairesConverter {

    private static final int HEADER_ROW_INDEX = 0;
    private static final int HEADER_START_COLUMN_INDEX = 0;

    private static final String NAME = "Name";
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";

    public static List<QuestionnaireDslModel> convert(Sheet sheet) {
        var columnMap = getSheetHeaderWithoutFormula(sheet, HEADER_ROW_INDEX, HEADER_START_COLUMN_INDEX);

        return IntStream.rangeClosed(HEADER_ROW_INDEX + 1, sheet.getLastRowNum())
            .mapToObj(i -> {
                Row row = sheet.getRow(i);
                if (isBlankRow(row)) return null;

                return QuestionnaireDslModel.builder()
                    .code(getCellString(row, columnMap.get(NAME)))
                    .title(getCellString(row, columnMap.get(TITLE)))
                    .description(getCellString(row, columnMap.get(DESCRIPTION)))
                    .index(i)
                    .build();
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
