package br.com.everrise.service;

import br.com.everrise.domain.entity.Guincho;
import br.com.everrise.domain.enums.GuinchoStatus;
import br.com.everrise.dto.request.ComandoRequest;
import br.com.everrise.dto.response.GuinchoStatusResponse;
import br.com.everrise.exception.ResourceNotFoundException;
import br.com.everrise.mapper.GuinchoMapper;
import br.com.everrise.repository.GuinchoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuinchoServiceTest {

    @Mock
    private GuinchoRepository guinchoRepository;

    @Mock
    private GuinchoMapper guinchoMapper;

    @Mock
    private MqttService mqttService;

    @InjectMocks
    private GuinchoServiceImpl guinchoService;

    private Guincho guincho;

    @BeforeEach
    void setUp() {
        guincho = Guincho.builder()
                .id(1L)
                .serialNumber("EVR-001")
                .status(GuinchoStatus.PRONTO)
                .isMoving(false)
                .battery(90)
                .connectionQuality(88)
                .build();
    }

    @Test
    void deveRetornarStatusQuandoGuinchoExiste() {
        GuinchoStatusResponse expected = new GuinchoStatusResponse(1L, GuinchoStatus.PRONTO, 90, 88, false);
        when(guinchoRepository.findById(1L)).thenReturn(Optional.of(guincho));
        when(guinchoMapper.toStatusResponse(guincho)).thenReturn(expected);

        GuinchoStatusResponse result = guinchoService.findStatus(1L);

        assertEquals(GuinchoStatus.PRONTO, result.status());
        assertEquals(1L, result.guinchoId());
        verify(guinchoRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoGuinchoNaoExiste() {
        when(guinchoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> guinchoService.findStatus(99L));
        verify(guinchoMapper, never()).toStatusResponse(any());
    }

    @Test
    void devePublicarComandoQuandoGuinchoExiste() {
        ComandoRequest request = new ComandoRequest("FRENTE", 35, "START");
        when(guinchoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(mqttService).publishCommand(1L, request);

        guinchoService.enviarComando(1L, request);

        verify(mqttService).publishCommand(1L, request);
    }
}

