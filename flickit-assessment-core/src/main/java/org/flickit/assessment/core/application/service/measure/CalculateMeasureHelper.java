package org.flickit.assessment.core.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.util.MathUtils;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.Measure;
import org.flickit.assessment.core.application.port.out.measure.LoadMeasuresPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateMeasureHelper {


	private final LoadMeasuresPort loadMeasuresPort;

	public List<MeasureDto> calculateMeasures(UUID assessmentId, Collection<QuestionDto> questions) {
		var attributeMaxPossibleScore = questions.stream()
			.mapToDouble(QuestionDto::weight)
			.sum();

		var measureIdToQuestions = questions.stream()
			.collect(groupingBy(QuestionDto::measureId, LinkedHashMap::new, toList()));

		var measureIdToMaxPossibleScore = questions.stream()
			.collect(groupingBy(
				QuestionDto::measureId,
				summingDouble(QuestionDto::weight) // Sum weights for each measureId
			));

		var measureIds = measureIdToQuestions.keySet().stream()
			.toList();

		var idToMeasureMap = loadMeasuresPort.loadAll(measureIds, assessmentId)
			.stream().collect(toMap(Measure::getId, Function.identity()));

		return measureIdToQuestions.entrySet().stream()
			.map(entry -> buildMeasure(
				idToMeasureMap.get(entry.getKey()),
				entry.getValue(),
				measureIdToMaxPossibleScore.get(entry.getKey()),
				attributeMaxPossibleScore))
			.toList();
	}

	private MeasureDto buildMeasure(Measure measure,
									List<QuestionDto> questions,
									double measureMaxPossibleScore,
									double attributeMaxPossibleScore) {
		assert measureMaxPossibleScore != 0.0;
		var impactPercentage = attributeMaxPossibleScore != 0
			? (measureMaxPossibleScore / attributeMaxPossibleScore) * 100
			: 0.0;

		var gainedScore = questions.stream()
			.mapToDouble(q -> (q.answer() != null && q.answer().getSelectedOption() != null)
				? q.answer().getSelectedOption().getValue() * q.weight()
				: 0.0)
			.sum();

		var missedScore = measureMaxPossibleScore - gainedScore;
		var gainedScorePercentage = attributeMaxPossibleScore != 0 ? gainedScore / attributeMaxPossibleScore : 1;
		var missedScorePercentage = attributeMaxPossibleScore != 0 ? missedScore / attributeMaxPossibleScore : 1;

		return new MeasureDto(measure.getTitle(),
			MathUtils.round(impactPercentage, 2),
			MathUtils.round(measureMaxPossibleScore, 2),
			MathUtils.round(gainedScore, 2),
			MathUtils.round(missedScore, 2),
			MathUtils.round(gainedScorePercentage * 100, 2),
			MathUtils.round(missedScorePercentage * 100, 2)
		);
	}

	public record QuestionDto(long id,
							  double weight,
							  long measureId,
							  Answer answer) {
	}

	public record MeasureDto(String title,
							 Double impactPercentage,
							 Double maxPossibleScore,
							 Double gainedScore,
							 Double missedScore,
							 Double gainedScorePercentage,
							 Double missedScorePercentage) {
	}
}
