package org.flickit.assessment.kit.adapter.out.persistence.kitdsl.convertor;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.flickit.assessment.kit.adapter.out.persistence.kitdsl.convertor.ExcelToDslModelConverter.*;

public class QuestionnairesConvertor {

    private static final String NAME = "Name";
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";

    static final int HEADER_ROW_NUM = 0;
    static final int HEADER_START_COL = 0;
    static final int HEADER_END_COL = 2;
    static final int DATA_START_ROW = 1;

    static List<QuestionnaireDslModel> convert(Sheet sheet) {
        var columnMap = getSheetHeader(sheet, HEADER_ROW_NUM, HEADER_START_COL, HEADER_END_COL);

        return IntStream.rangeClosed(DATA_START_ROW, sheet.getLastRowNum())
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
