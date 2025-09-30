package org.flickit.assessment.kit.adapter.out.persistence.kitdsl.converter;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.flickit.assessment.kit.adapter.out.persistence.kitdsl.converter.ExcelToDslModelConverter.*;

@UtilityClass
public class QualityAttributesConverter {

    private static final String SUBJECT_NAME = "Subject Name";
    private static final String SUBJECT_TITLE = "Subject Title";
    private static final String SUBJECT_WEIGHT = "Subject Weight";
    private static final String SUBJECT_DESCRIPTION = "Subject Description";

    private static final int SUBJECT_HEADER_ROW_NUM = 0;
    private static final int SUBJECT_HEADER_START_COL = 0;
    private static final int SUBJECT_HEADER_END_COL = 3;
    private static final int SUBJECT_DATA_START_ROW = 1;

    private static final String ATTRIBUTE_NAME = "Attribute Name";
    private static final String ATTRIBUTE_TITLE = "Attribute Title";
    private static final String ATTRIBUTE_WEIGHT = "Attribute Weight";
    private static final String ATTRIBUTE_DESCRIPTION = "Attribute Description";

    private static final int ATTRIBUTE_HEADER_ROW_NUM = 0;
    private static final int ATTRIBUTE_HEADER_START_COL = 0;
    private static final int ATTRIBUTE_HEADER_END_COL = 7;
    private static final int ATTRIBUTE_DATA_START_ROW = 1;

    static List<SubjectDslModel> convertSubjects(Sheet sheet) {
        var columnMap = getSheetHeader(sheet, SUBJECT_HEADER_ROW_NUM, SUBJECT_HEADER_START_COL, SUBJECT_HEADER_END_COL);

        List<Row> validRows = IntStream.range(SUBJECT_DATA_START_ROW, sheet.getLastRowNum() + SUBJECT_DATA_START_ROW)
            .mapToObj(sheet::getRow)
            .filter(row -> {
                String code = getCellString(row, columnMap.get(SUBJECT_NAME));
                return !ExcelToDslModelConverter.isBlankRow(row) && code != null && !code.isBlank();
            })
            .toList();

        return IntStream.range(0, validRows.size())
            .mapToObj(idx -> {
                Row row = validRows.get(idx);
                return SubjectDslModel.builder()
                    .code(getCellString(row, columnMap.get(SUBJECT_NAME)))
                    .title(getCellString(row, columnMap.get(SUBJECT_TITLE)))
                    .weight(getCellInteger(row, columnMap.get(SUBJECT_WEIGHT)))
                    .description(getCellString(row, columnMap.get(SUBJECT_DESCRIPTION)))
                    .index(idx + 1)
                    .build();
            })
            .collect(Collectors.toList());
    }

    static List<AttributeDslModel> convertAttributes(Sheet sheet) {
        var columnMap = getSheetHeader(sheet, ATTRIBUTE_HEADER_ROW_NUM, ATTRIBUTE_HEADER_START_COL, ATTRIBUTE_HEADER_END_COL);

        return IntStream.rangeClosed(ATTRIBUTE_DATA_START_ROW, sheet.getLastRowNum())
            .mapToObj(sheet::getRow)
            .filter(row -> !ExcelToDslModelConverter.isBlankRow(row))
            .collect(ArrayList::new, (list, row) -> {
                String subjectCode = Optional.ofNullable(getCellString(row, columnMap.get(SUBJECT_NAME)))
                    .filter(c -> !c.isBlank())
                    .orElseGet(() -> !list.isEmpty() ? list.getLast().getSubjectCode() : null);

                AttributeDslModel attribute = AttributeDslModel.builder()
                    .subjectCode(subjectCode)
                    .code(getCellString(row, columnMap.get(ATTRIBUTE_NAME)))
                    .weight(getCellInteger(row, columnMap.get(ATTRIBUTE_WEIGHT)))
                    .title(getCellString(row, columnMap.get(ATTRIBUTE_TITLE)))
                    .description(getCellString(row, columnMap.get(ATTRIBUTE_DESCRIPTION)))
                    .index(list.size() + 1)
                    .build();

                list.add(attribute);
            }, List::addAll);
    }
}
