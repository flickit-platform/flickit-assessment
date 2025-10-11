package org.flickit.assessment.kit.adapter.out.excel.dsl.converter;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.util.ExcelUtils.*;

@UtilityClass
public class QuestionsConverter {

    private static final int HEADER_ROW_INDEX = 1;

    private static final String TITLE = "Question";
    private static final String QUESTIONNAIRE = "Questionnaires";
    private static final String CODE = "Code";
    private static final String ANSWER_RANGE = "Options";
    private static final String DESCRIPTION = "Description";
    private static final String NOT_APPLICABLE = "Not Applicable";
    private static final String ADVISABLE = "Advisable";
    private static final String MATURITY_LEVEL = "Maturity";

    public static List<QuestionDslModel> convert(Sheet sheet,
                                                 FormulaEvaluator formulaEvaluator,
                                                 Map<String, List<AnswerOptionDslModel>> answerRangeCodeToAnswerOptionsMap,
                                                 Map<String, MaturityLevelDslModel> maturityLevelCodeToMaturityLevelDslMap,
                                                 List<AttributeDslModel> attributeDslModels) {
        var columnMap = getSheetHeaderWithFormula(sheet, formulaEvaluator, HEADER_ROW_INDEX);

        return IntStream.rangeClosed(HEADER_ROW_INDEX + 1, sheet.getLastRowNum())
            .filter(i -> !isBlankRow(sheet.getRow(i)))
            .mapToObj(i -> {
                Row row = sheet.getRow(i);
                var answerRangeCode = getCellString(row, columnMap.get(ANSWER_RANGE));
                var answerOptions = answerRangeCodeToAnswerOptionsMap.get(answerRangeCode);

                List<QuestionImpactDslModel> questionImpacts = createImpacts(row,
                        columnMap,
                        answerOptions,
                        attributeDslModels,
                        maturityLevelCodeToMaturityLevelDslMap.get(getCellString(row, columnMap.get(MATURITY_LEVEL))));

                return QuestionDslModel.builder()
                    .title(getCellString(row, columnMap.get(TITLE)))
                    .questionnaireCode(getCellString(row, columnMap.get(QUESTIONNAIRE)))
                    .code(getCellString(row, columnMap.get(CODE)))
                    .answerRangeCode(answerRangeCode)
                    .index(i - 1)
                    .questionImpacts(questionImpacts)
                    .description(getCellString(row, columnMap.get(DESCRIPTION)))
                    .mayNotBeApplicable(getCellInteger(row, columnMap.get(NOT_APPLICABLE)) == 1)
                    .advisable(getCellInteger(row, columnMap.get(ADVISABLE)) == 1)
                    .build();
            })
            .collect(Collectors.toList());
    }

    List<QuestionImpactDslModel> createImpacts(Row row,
                                               Map<String, Integer> columnMap,
                                               List<AnswerOptionDslModel> answerOptions,
                                               List<AttributeDslModel> attributeDslModels,
                                               MaturityLevelDslModel maturityLevelDslModel) {
        var optionsIndexToValueMap = answerOptions.stream()
                .collect(toMap(AnswerOptionDslModel::getIndex, AnswerOptionDslModel::getValue));

        List<QuestionImpactDslModel> questionImpacts = new ArrayList<>();

        for (AttributeDslModel attribute : attributeDslModels) {
            String attributeCode = attribute.getCode();
            int attributeColumn = columnMap.get(attributeCode);
            Integer weight = getCellInteger(row, attributeColumn);
            if (weight != null) {
                var maturityLevel = MaturityLevelDslModel.builder()
                        .title(maturityLevelDslModel.getTitle())
                        .code(maturityLevelDslModel.getCode()).build();

                QuestionImpactDslModel questionImpact = QuestionImpactDslModel.builder()
                        .attributeCode(attributeCode)
                        .maturityLevel(maturityLevel)
                        .weight(weight)
                        .optionsIndextoValueMap(optionsIndexToValueMap)
                        .build();

                questionImpacts.add(questionImpact);
            }
        }
        return questionImpacts;
    }
}
