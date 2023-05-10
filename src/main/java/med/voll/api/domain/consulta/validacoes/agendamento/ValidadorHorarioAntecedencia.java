package med.voll.api.domain.consulta.validacoes.agendamento;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;

@Component
public class ValidadorHorarioAntecedencia implements ValidadorAgendamentoDeConsulta{
	public void validar(DadosAgendamentoConsulta dados) {
		var dataConsulta = dados.data();
		var agora = LocalDateTime.now();
		var diferencaEntreMinutos = Duration.between(agora, dataConsulta).toMinutes();
		
		if(diferencaEntreMinutos < 30) {
			throw new ValidacaoException("A Consulta deve ser agendada com 30 minutos de antecedencia");
		}
	}

}
