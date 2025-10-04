package org.flickit.assessment.kit.adapter.out.excel.dsl.converter;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.flickit.assessment.common.util.ExcelUtils.*;

@UtilityClass
public class QuestionsConverter {

    private static final int HEADER_ROW_INDEX = 1;
    private static final int HEADER_START_COLUMN_INDEX = 0;

    private static final String TITLE = "Question";
    private static final String QUESTIONNAIRES = "Questionnaires";
    private static final String CODE = "Code";
    private static final String OPTIONS = "Options";
    private static final String DESCRIPTION = "Description";
    private static final String NOT_APPLICABLE = "Not Applicable";
    private static final String ADVISABLE = "Advisable";
    private static final String MATURITY = "Maturity";

    public static List<QuestionDslModel> convert(Sheet sheet,
                                                 Map<String, List<AnswerOptionDslModel>> answerRangeCodeToAnswerOptionsMap,
                                                 Map<String, MaturityLevelDslModel> maturityLevelCodeToMaturityLevelDslMap,
                                                 List<AttributeDslModel> attributeDslModels) {
        var columnMap = getSheetHeaderWithoutFormula(sheet, HEADER_ROW_INDEX, HEADER_START_COLUMN_INDEX);

        return IntStream.rangeClosed(HEADER_ROW_INDEX + 1, sheet.getLastRowNum())
            .filter(i -> !isBlankRow(sheet.getRow(i)))
            .mapToObj(i -> {
                Row row = sheet.getRow(i);
                List<QuestionImpactDslModel> questionImpacts = new ArrayList<>();
                var answerRangeCode = getCellString(row, columnMap.get(OPTIONS));
                var optionsIndexToValueMap = answerRangeCodeToAnswerOptionsMap.get(answerRangeCode)
                    .stream()
                    .collect(Collectors.toMap(AnswerOptionDslModel::getIndex, AnswerOptionDslModel::getValue));

                int attributesStartColumn = columnMap.get(MATURITY) + 1;
                for (int j = 0; j < attributeDslModels.size(); j++) {
                    Integer weight = getCellInteger(row, attributesStartColumn + j);
                    QuestionImpactDslModel questionImpact;
                    if (weight != null) {
                        MaturityLevelDslModel maturityLevelDslModel = maturityLevelCodeToMaturityLevelDslMap.get(getCellString(row, columnMap.get(MATURITY)));
                        var maturityLevel = MaturityLevelDslModel.builder()
                            .title(maturityLevelDslModel.getTitle())
                            .code(maturityLevelDslModel.getCode()).build();

                        String attributeCode = attributeDslModels.get(j).getCode();
                        questionImpact = QuestionImpactDslModel.builder()
                            .attributeCode(attributeCode)
                            .maturityLevel(maturityLevel)
                            .weight(weight)
                            .optionsIndextoValueMap(optionsIndexToValueMap)
                            .build();

                        if (questionImpact != null)
                            questionImpacts.add(questionImpact);
                    }
                }

                return QuestionDslModel.builder()
                    .title(getCellString(row, columnMap.get(TITLE)))
                    .questionnaireCode(getCellString(row, columnMap.get(QUESTIONNAIRES)))
                    .code(getCellString(row, columnMap.get(CODE)))
                    .answerRangeCode(answerRangeCode)
                    .answerOptions(answerRangeCodeToAnswerOptionsMap.get(answerRangeCode))
                    .index(i - 1)
                    .questionImpacts(questionImpacts)
                    .description(getCellString(row, columnMap.get(DESCRIPTION)))
                    .mayNotBeApplicable(getCellInteger(row, columnMap.get(NOT_APPLICABLE)) == 1)
                    .advisable(getCellInteger(row, columnMap.get(ADVISABLE)) == 1)
                    .build();
            })
            .collect(Collectors.toList());
    }
}
