package med.voll.api.domain.consulta;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.validacoes.agendamento.ValidadorAgendamentoDeConsulta;
import med.voll.api.domain.consulta.validacoes.cancelamento.ValidadorCancelamentoDeConsulta;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;

@Service
public class AgendaDeConsultas {
	
    @Autowired
    private ConsultaRepository consultaRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private List<ValidadorAgendamentoDeConsulta> validadores;
    @Autowired
    private List<ValidadorCancelamentoDeConsulta> validadoresCancelamento;
	
	public DadosDetalhamentoConsulta agendar(DadosAgendamentoConsulta dados) {
		//As linhas abaixo são para garantir a integridade dos dados
		if(!pacienteRepository.existsById(dados.idPaciente())) {
			throw new ValidacaoException("Id do paciente não existe");
		}
		if(dados.idMedico()!=null && !medicoRepository.existsById(dados.idMedico())) {
			throw new ValidacaoException("Id do médico não existe");
		}
		
		validadores.forEach(v -> v.validar(dados));
		
		//As linhas abaixo são necessarias para persistir na base
//		var paciente = pacienteRepository.findById(dados.idPaciente()).get();
		var paciente = pacienteRepository.getReferenceById(dados.idPaciente());
		var medico = escolherMedico(dados);
		if(medico ==null) {
			throw new ValidacaoException("Não existe médico disponível nessa data");
		}
		var consulta = new Consulta(null, medico, paciente, dados.data(),null);
		consultaRepository.save(consulta);
		
		return new DadosDetalhamentoConsulta(consulta);
	}
	
	public void cancelar(DadosCancelamentoConsulta dados) {
        if (!consultaRepository.existsById(dados.idConsulta())) {
            throw new ValidacaoException("Id da consulta informado não existe!");
        }

        validadoresCancelamento.forEach(v -> v.validar(dados));

        var consulta = consultaRepository.getReferenceById(dados.idConsulta());
        consulta.cancelar(dados.motivo());
    }

	private Medico escolherMedico(DadosAgendamentoConsulta dados) {
		
		if(dados.idMedico()!= null) {
			return medicoRepository.getReferenceById(dados.idMedico());
		}
		
		if(dados.especialidade()== null) {
			throw new ValidacaoException("Especialidade é obrigatória quando o médico não for escolhido");
		}
				
		
		return medicoRepository.escolherMedicoAleatorioLivreNaData(dados.especialidade(), dados.data());
	}
}
