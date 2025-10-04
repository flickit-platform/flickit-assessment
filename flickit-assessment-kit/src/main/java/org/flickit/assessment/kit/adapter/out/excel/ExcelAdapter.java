package org.flickit.assessment.kit.adapter.out.excel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.adapter.out.excel.converter.ExcelToDslModelConverter;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.port.out.kitdsl.ConvertExcelToDslModelPort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ExcelAdapter implements ConvertExcelToDslModelPort {

    @Override
    public AssessmentKitDslModel convert(MultipartFile excelFile) {
        return ExcelToDslModelConverter.convert(excelFile);
    }
}
