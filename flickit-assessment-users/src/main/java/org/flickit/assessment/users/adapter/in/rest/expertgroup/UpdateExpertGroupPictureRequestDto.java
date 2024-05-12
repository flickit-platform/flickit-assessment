package org.flickit.assessment.users.adapter.in.rest.expertgroup;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UpdateExpertGroupPictureRequestDto (MultipartFile picture, UUID cu){
}
