package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.users.application.port.in.user.CreateUserUseCase;
import org.flickit.assessment.users.application.port.out.user.CreateUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @InjectMocks
    private CreateUserService service;

    @Mock
    private CreateUserPort createUserPort;

    @Test
    void testCreateUserService_whenParametersAreValid_thenReturnValidResult() {
        var param = createParam(CreateUserUseCase.Param.ParamBuilder::build);
        var userId = UUID.randomUUID();

        when(createUserPort.persist(param.getUserId(), param.getDisplayName(), param.getEmail())).thenReturn(userId);

        var result = service.createUser(param);
        assertEquals(userId, result.userId());
    }

    private CreateUserUseCase.Param createParam(Consumer<CreateUserUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateUserUseCase.Param.ParamBuilder paramBuilder() {
        return CreateUserUseCase.Param.builder()
            .userId(UUID.randomUUID())
            .email("admin@flickit.com")
            .displayName("Display Name");
    }
}
