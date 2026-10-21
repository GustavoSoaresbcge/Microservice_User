package com.ms.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.ms.user.dtos.EmailDto;
import com.ms.user.models.UserModel;
import com.ms.user.producers.UserProducer;

@ExtendWith(MockitoExtension.class)
class UserProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private UserProducer userProducer;

    @Test
    void devePublicarEmailDtoCorretoComRoutingKeyCerta() {
        // Arrange
        ReflectionTestUtils.setField(userProducer, "routingKey", "default.email");

        UserModel userModel = new UserModel();
        userModel.setEmail("teste@example.com");
        userModel.setName("Teste");
        userModel.setUserId(UUID.randomUUID());

        // Act
        userProducer.publishMessageEmail(userModel);

        // Assert
        ArgumentCaptor<EmailDto> captor = ArgumentCaptor.forClass(EmailDto.class);
        
        // Verifica se o template foi chamado com os argumentos exatos (routing key) e captura o DTO
        verify(rabbitTemplate).convertAndSend(eq(""), eq("default.email"), captor.capture());

        // Analisa o que foi capturado
        EmailDto enviado = captor.getValue();
        assertEquals(userModel.getEmail(), enviado.getEmailTo());
        assertEquals("Cadastro realizado com sucesso!", enviado.getSubject());
        assertTrue(enviado.getText().contains(userModel.getName()));
    }
}