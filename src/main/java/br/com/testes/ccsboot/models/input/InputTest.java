package br.com.testes.ccsboot.models.input;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record InputTest(OffsetDateTime dataHora, String mensagem, BigDecimal valor, Boolean aBoolean) {
}
