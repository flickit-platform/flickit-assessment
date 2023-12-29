package org.flickit.assessment.kit.application.domain;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;
import org.springframework.boot.actuate.endpoint.web.Link;
@NoArgsConstructor
@AllArgsConstructor
public class ExpertGroup {

    long id;
    String name;
    String bio;
    Link picture;
    GetKitUserListUseCase.UserListItem members;
    int membersCount;
    int publishedKitsCount;
    boolean editable;
    public ExpertGroup(Long id) {
        this.id = id;
    }
}
