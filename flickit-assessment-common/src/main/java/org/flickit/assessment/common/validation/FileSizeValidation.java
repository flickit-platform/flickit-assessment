package org.flickit.assessment.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeValidation implements
    ConstraintValidator<FileSize, MultipartFile> {

    private long maxSize;

    @Override
    public void initialize(FileSize constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile,
                           ConstraintValidatorContext cxt) {
        return multipartFile == null || multipartFile.getSize() <=
            maxSize;
    }
}
