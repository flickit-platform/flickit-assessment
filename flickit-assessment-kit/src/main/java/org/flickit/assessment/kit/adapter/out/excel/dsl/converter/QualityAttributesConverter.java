package org.flickit.assessment.kit.adapter.out.excel.dsl.converter;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.SubjectDslModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.flickit.assessment.common.util.ExcelUtils.*;

@UtilityClass
public class QualityAttributesConverter {

    private static final int HEADER_ROW_INDEX = 0;

    private static final String SUBJECT_CODE = "Subject Name";
    private static final String SUBJECT_TITLE = "Subject Title";
    private static final String SUBJECT_WEIGHT = "Subject Weight";
    private static final String SUBJECT_DESCRIPTION = "Subject Description";

    private static final String ATTRIBUTE_CODE = "Attribute Name";
    private static final String ATTRIBUTE_TITLE = "Attribute Title";
    private static final String ATTRIBUTE_WEIGHT = "Attribute Weight";
    private static final String ATTRIBUTE_DESCRIPTION = "Attribute Description";

    public static List<SubjectDslModel> convertSubjects(Sheet sheet) {
        var columnMap = getSheetHeaderWithoutFormula(sheet, HEADER_ROW_INDEX);

        List<Row> validRows = IntStream.range(HEADER_ROW_INDEX + 1, sheet.getLastRowNum() + HEADER_ROW_INDEX + 1)
            .mapToObj(sheet::getRow)
            .filter(row -> {
                String code = getCellString(row, columnMap.get(SUBJECT_CODE));
                return !isBlankRow(row) && code != null && !code.isBlank();
            })
            .toList();

        return IntStream.range(0, validRows.size())
            .mapToObj(idx -> {
                Row row = validRows.get(idx);
                return SubjectDslModel.builder()
                    .code(getCellString(row, columnMap.get(SUBJECT_CODE)))
                    .title(getCellString(row, columnMap.get(SUBJECT_TITLE)))
                    .weight(getCellInteger(row, columnMap.get(SUBJECT_WEIGHT)))
                    .description(getCellString(row, columnMap.get(SUBJECT_DESCRIPTION)))
                    .index(idx + 1)
                    .build();
            })
            .collect(Collectors.toList());
    }

    public static List<AttributeDslModel> convertAttributes(Sheet sheet) {
        var columnMap = getSheetHeaderWithoutFormula(sheet, HEADER_ROW_INDEX);

        List<AttributeDslModel> attributes = new ArrayList<>();
        String subjectCode = null;
        int attributeIndex = 0;

        for (int i = HEADER_ROW_INDEX + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (isBlankRow(row)) continue;

            String newSubjectCode = getCellString(row, columnMap.get(SUBJECT_CODE));
            if (newSubjectCode != null && !newSubjectCode.isBlank()) {
                subjectCode = newSubjectCode;
                attributeIndex = 1;
            }

            attributes.add(AttributeDslModel.builder()
                .subjectCode(subjectCode)
                .code(getCellString(row, columnMap.get(ATTRIBUTE_CODE)))
                .weight(getCellInteger(row, columnMap.get(ATTRIBUTE_WEIGHT)))
                .title(getCellString(row, columnMap.get(ATTRIBUTE_TITLE)))
                .description(getCellString(row, columnMap.get(ATTRIBUTE_DESCRIPTION)))
                .index(attributeIndex++)
                .build());
        }

        return attributes;
    }
}
