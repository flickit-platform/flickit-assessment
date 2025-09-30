package org.flickit.assessment.kit.adapter.out.persistence.kitdsl.converter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.flickit.assessment.kit.application.domain.dsl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.flickit.assessment.kit.adapter.out.persistence.kitdsl.converter.ExcelToDslModelConverter.*;

public class QuestionsConverter {

    private static final int HEADER_ROW_NUM = 1;
    private static final int HEADER_START_COL = 0;
    private static final int HEADER_END_COL = 8;
    private static final int DATA_START_ROW = 2;

    private static final String QUESTION_QUESTION = "Question";
    private static final String QUESTION_QUESTIONNAIRES = "Questionnaires";
    private static final String QUESTION_CODE = "Code";
    private static final String QUESTION_OPTIONS = "Options";
    private static final String QUESTION_DESCRIPTION = "Description";
    private static final String QUESTION_NOT_APPLICABLE = "Not Applicable";
    private static final String QUESTION_ADVISABLE = "Advisable";
    private static final String QUESTION_MATURITY = "Maturity";

    static List<QuestionDslModel> convert(Sheet sheet, Map<String, List<AnswerOptionDslModel>> answerRangeCodeToAnswerOptionsMap,
                                          Map<String, MaturityLevelDslModel> maturityLevelCodeToMaturityLevelDslMap, List<AttributeDslModel> attributeDslModels) {
        var columnMap = getSheetHeader(sheet, HEADER_ROW_NUM, HEADER_START_COL, HEADER_END_COL);

        return IntStream.rangeClosed(DATA_START_ROW, sheet.getLastRowNum())
            .filter(i -> !isBlankRow(sheet.getRow(i)))
            .mapToObj(i -> {
                Row row = sheet.getRow(i);
                String options = getCellString(row, columnMap.get(QUESTION_OPTIONS));
                List<QuestionImpactDslModel> questionImpacts = new ArrayList<>();
                var answerRangeCode = getCellString(row, columnMap.get(QUESTION_OPTIONS));
                var optionsIndexToValueMap = answerRangeCodeToAnswerOptionsMap.get(answerRangeCode)
                    .stream()
                    .collect(Collectors.toMap(AnswerOptionDslModel::getIndex, AnswerOptionDslModel::getValue));

                for (int j = 0; j < attributeDslModels.size(); j++) {
                    Integer weight = getCellInteger(row, HEADER_END_COL + j);
                    QuestionImpactDslModel questionImpact = null;
                    if (weight != null) {
                        MaturityLevelDslModel maturityLevelDslModel = maturityLevelCodeToMaturityLevelDslMap.get(getCellString(row, columnMap.get(QUESTION_MATURITY)));
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
                    }
                    if (questionImpact != null)
                        questionImpacts.add(questionImpact);
                }

                return QuestionDslModel.builder()
                    .title(getCellString(row, columnMap.get(QUESTION_QUESTION)))
                    .questionnaireCode(getCellString(row, columnMap.get(QUESTION_QUESTIONNAIRES)))
                    .code(getCellString(row, columnMap.get(QUESTION_CODE)))
                    .answerRangeCode(answerRangeCode)
                    .answerOptions(answerRangeCodeToAnswerOptionsMap.get(options))
                    .index(i - 1)
                    .questionImpacts(questionImpacts)
                    .description(getCellString(row, columnMap.get(QUESTION_DESCRIPTION)))
                    .mayNotBeApplicable(getCellInteger(row, columnMap.get(QUESTION_NOT_APPLICABLE)) == 1)
                    .advisable(getCellInteger(row, columnMap.get(QUESTION_ADVISABLE)) == 1)
                    .build();
            })
            .collect(Collectors.toList());
    }
}
