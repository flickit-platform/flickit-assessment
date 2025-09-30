package org.flickit.assessment.kit.adapter.out.persistence.kitdsl.convertor;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.flickit.assessment.kit.adapter.out.persistence.kitdsl.convertor.ExcelToDslModelConverter.*;

public class MaturityLevelsConverter {

    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";

    private final static int HEADER_ROW_NUM = 1;
    private final static int HEADER_START_COL = 1;
    private final static int HEADER_END_COL = 2;
    private final static int DATA_START_ROW = 2;
    private final static int DATA_START_COL = 3;

    static List<MaturityLevelDslModel> convert(Sheet sheet) {
        var columnMap = getSheetHeader(sheet, HEADER_ROW_NUM, HEADER_START_COL, HEADER_END_COL);

        List<String> levels = IntStream.range(DATA_START_ROW, sheet.getLastRowNum() + 1)
            .mapToObj(sheet::getRow)
            .filter(row -> !ExcelToDslModelConverter.isBlankRow(row))
            .map(row -> row.getCell(0).toString().trim())
            .toList();

        return IntStream.range(0, levels.size())
            .mapToObj(idx -> {
                Row row = sheet.getRow(DATA_START_ROW + idx);
                Map<String, Integer> competence = buildCompetenceMap(row, levels);

                int index = idx + 1;
                return MaturityLevelDslModel.builder()
                    .code(row.getCell(0).toString().trim())
                    .index(index)
                    .title(getCellString(row, columnMap.get(TITLE)))
                    .description(getCellString(row, columnMap.get(DESCRIPTION)))
                    .value(index)
                    .competencesCodeToValueMap(competence.isEmpty() ? null  : competence)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private static Map<String, Integer> buildCompetenceMap(Row row, List<String> levels) {
        return IntStream.range(0, levels.size())
            .mapToObj(j -> Map.entry(levels.get(j), getCellInteger(row, DATA_START_COL + j)))
            .filter(entry -> entry.getValue() != 0)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
