package org.flickit.assessment.kit.adapter.out.excel.dsl.converter;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.flickit.assessment.common.util.ExcelUtils.*;

@UtilityClass
public class MaturityLevelsConverter {

    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";

    private static final int HEADER_ROW_NUM = 1;
    private static final int HEADER_START_COL = 1;
    private static final int DATA_START_COL = 3;

    public static List<MaturityLevelDslModel> convert(Sheet sheet) {
        var columnMap = getSheetHeaderWithoutFormula(sheet, HEADER_ROW_NUM, HEADER_START_COL);

        List<String> levels = IntStream.range(HEADER_ROW_NUM + 1, sheet.getLastRowNum() + 1)
            .mapToObj(sheet::getRow)
            .filter(row -> !isBlankRow(row))
            .map(row -> row.getCell(0).toString().trim())
            .toList();

        return IntStream.range(0, levels.size())
            .mapToObj(idx -> {
                Row row = sheet.getRow(HEADER_ROW_NUM + 1 + idx);
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
