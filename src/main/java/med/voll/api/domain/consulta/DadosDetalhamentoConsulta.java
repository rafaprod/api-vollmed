package med.voll.api.domain.consulta;

import java.time.LocalDateTime;

import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.paciente.Paciente;

public record DadosDetalhamentoConsulta(Long id, Long idMedico, Long idPaciente, LocalDateTime dataConsulta) {
	public DadosDetalhamentoConsulta(Consulta consulta) {
		this(consulta.getId(), consulta.getMedico().getId(), consulta.getPaciente().getId(), consulta.getData());
	}


	
	

}
