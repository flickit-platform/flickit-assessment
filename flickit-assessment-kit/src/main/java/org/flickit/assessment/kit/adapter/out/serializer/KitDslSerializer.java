package org.flickit.assessment.kit.adapter.out.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.flickit.assessment.kit.application.domain.dsl.*;
import org.flickit.assessment.kit.application.port.out.kitdsl.ConvertAssessmentKitDslModelPort;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Component
public class KitDslSerializer implements ConvertAssessmentKitDslModelPort {

    private static final String SUBJECTS_FILE_NAME = "subjects.ak";
    private static final String QUALITY_ATTRIBUTES_FILE_NAME = "quality-attribute.ak";
    private static final String LEVELS_FILE_NAME = "levels.ak";
    private static final String QUESTIONNAIRES_FILE_NAME = "questionnaires.ak";
    private static final String QUESTIONS_FILE_NAME_PATTERN = "questions_%s.ak";
    public static final String ANSWER_RANGES_FILE_NAME = "answer_ranges.ak";

    @Override
    public Map<String, String> toDsl(AssessmentKitDslModel dslModel) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(MaturityLevelDslModel.class, new MaturityLevelDslSerializer());
        module.addSerializer(AttributeDslModel.class, new AttributeDslSerializer());
        module.addSerializer(SubjectDslModel.class, new SubjectDslSerializer());
        module.addSerializer(QuestionnaireDslModel.class, new QuestionnaireDslSerializer());
        module.addSerializer(QuestionDslModel.class, new QuestionDslSerializer());
        module.addSerializer(AnswerRangeDslModel.class, new AnswerRangeDslSerializer());
        mapper.registerModule(module);

        Function<List<?>, String> listToDsl = list -> {
            if (list == null || list.isEmpty()) return "";
            StringBuilder sb = new StringBuilder();
            for (Object item : list) {
                try {
                    sb.append(mapper.writeValueAsString(item)).append("\n");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return sb.toString();
        };

        Map<String, String> dslFiles = new LinkedHashMap<>();
        dslFiles.put(SUBJECTS_FILE_NAME, listToDsl.apply(dslModel.getSubjects()));
        dslFiles.put(QUALITY_ATTRIBUTES_FILE_NAME, listToDsl.apply(dslModel.getAttributes()));
        dslFiles.put(LEVELS_FILE_NAME, listToDsl.apply(dslModel.getMaturityLevels()));
        dslFiles.put(QUESTIONNAIRES_FILE_NAME, listToDsl.apply(dslModel.getQuestionnaires()));
        dslFiles.put(ANSWER_RANGES_FILE_NAME, listToDsl.apply(dslModel.getAnswerRanges()));
        var questionnaireCodeToQuestionDslModel = dslModel.getQuestions().stream()
            .collect(groupingBy(QuestionDslModel::getQuestionnaireCode));

        var questionnaireCodeToFileName = questionnaireCodeToQuestionDslModel.keySet().stream()
            .collect(Collectors.toMap(
                e -> e,
                e -> String.format(QUESTIONS_FILE_NAME_PATTERN, toSnakeCase(e))
            ));

        questionnaireCodeToQuestionDslModel.
            forEach((key, value) -> dslFiles.put(questionnaireCodeToFileName.get(key), listToDsl.apply(value)));

        return dslFiles;
    }

    private static String toSnakeCase(String input) {
        return input
            .replaceAll("([a-z])([A-Z])", "$1_$2")
            .replaceAll("([A-Z])([A-Z][a-z])", "$1_$2")
            .toLowerCase();
    }
}
