package com.epizootia.controller;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epizootia.dto.MetodoCapturaDTO;
import com.epizootia.entities.MetodoCaptura;
import com.epizootia.response.Response;
import com.epizootia.services.MetodoCapturaService;

@RestController
@RequestMapping("/api/metodoCaptura")
public class MetodoCapturaController {

	private static final Logger log = LoggerFactory.getLogger(MetodoCapturaController.class);

	@Autowired
	private MetodoCapturaService service;

	/**
	 * 
	 * Consulta todos os Metodos de Captura
	 * 
	 * @return List<MetodoCapturaDTO>
	 */
	@GetMapping
	public ResponseEntity<Response<List<MetodoCapturaDTO>>> listaTodos() {
		Response<List<MetodoCapturaDTO>> response = new Response<List<MetodoCapturaDTO>>();

		List<MetodoCapturaDTO> metodoCapturaDTOS = service.findAll().stream()
				.map(this::converteEntityParaDTO).collect(Collectors.toList());

		if (metodoCapturaDTOS.isEmpty()) {

			log.error("Não há Metodos de Captura cadastrados");
			response.getErrors().add("Não há Metodos de Captura cadastrados");

			return ResponseEntity.badRequest().body(response);
		}

		response.setData(metodoCapturaDTOS);

		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * Consulta todas Metodos de Captura por id
	 * 
	 * @return List<MetodoCapturaDTO>
	 */

	@GetMapping(value = "/{id}")
	public ResponseEntity<Response<MetodoCapturaDTO>> consulta(@PathVariable("id") int id) {

		Response<MetodoCapturaDTO> response = new Response<MetodoCapturaDTO>();
		Optional<MetodoCaptura> metodoCaptura = service.findById(id);

		if (!metodoCaptura.isPresent()) {
			log.error("Id de Metodos de Captura não cadastrado na base de dados");
			response.getErrors().add("Id  de Metodos de Captura não cadastrado na base de dados");
			return ResponseEntity.badRequest().body(response);
		}

		MetodoCapturaDTO metodoCapturaDTO = converteEntityParaDTO(metodoCaptura.get());

		response.setData(metodoCapturaDTO);

		log.info("Consulta de Metodos de Captura {}", metodoCapturaDTO);

		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * Cadastra novo Metodo de Captura na base de dados
	 * 
	 * @param DTO
	 * @param result
	 * @return MetodoCaptura
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<MetodoCapturaDTO>> cadastrar(@Valid @RequestBody MetodoCapturaDTO DTO,
			BindingResult result) throws NoSuchAlgorithmException {
		log.info("Cadastrando Metodo de Captura do animal {}", DTO.toString());

		Response<MetodoCapturaDTO> response = new Response<>();
		validaSeExiste(DTO, result);
		MetodoCaptura entity = this.converteDTOParaEntity(DTO);

		if (result.hasErrors()) {
			log.error("Erro ao validar informações: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.service.persistir(entity);
		response.setData(this.converteEntityParaDTO(entity));
		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * Deleta Metodo de Captura da base de dados
	 */
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Response<MetodoCapturaDTO>> apagar(@PathVariable("id") int id) {

		Response<MetodoCapturaDTO> response = new Response<MetodoCapturaDTO>();
		Optional<MetodoCaptura> metodoCaptura = service.findById(id);

		if (!metodoCaptura.isPresent()) {
			log.error("Id de Metodo de Captura não cadastrado na base de dados");
			response.getErrors().add("Id de Metodo de Captura não cadastrado na base de dados");
			return ResponseEntity.badRequest().body(response);
		}

		MetodoCapturaDTO metodoCapturaDTO = converteEntityParaDTO(metodoCaptura.get());

		response.setData(metodoCapturaDTO);
		service.apagar(metodoCaptura.get());
		log.info("Deletando Recomendação Vacinal do Animal {}", metodoCapturaDTO);

		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * Converte DTO para Entity
	 * 
	 * @param metodoCapturaDTO
	 * @return Entity
	 */

	public MetodoCaptura converteDTOParaEntity(MetodoCapturaDTO metodoCapturaDTO) {
		MetodoCaptura metodoCaptura = new MetodoCaptura();
		metodoCaptura.setId(metodoCapturaDTO.getId());
		metodoCaptura.setMetodoCaptura(metodoCapturaDTO.getMetodoCaptura());
		return metodoCaptura;
	}

	/**
	 * 
	 * Converte Entity em DTO
	 * 
	 * @param metodoCapturaDTO
	 * @return DTO
	 */

	public MetodoCapturaDTO converteEntityParaDTO(MetodoCaptura metodoCaptura) {
		MetodoCapturaDTO metodoCapturaDTO = new MetodoCapturaDTO();
		metodoCapturaDTO.setId(metodoCaptura.getId());
		metodoCapturaDTO.setMetodoCaptura(metodoCaptura.getMetodoCaptura());
		return metodoCapturaDTO;
	}

	/**
	 * 
	 * Valida se o Metodo de Captura ja existe na base de dados
	 * 
	 * @param DTO
	 * @param result
	 */
	private void validaSeExiste(MetodoCapturaDTO dTO, BindingResult result) {
		this.service.findById(dTO.getId()).ifPresent(rec -> result
				.addError(new ObjectError("Metodo de Captura", dTO.getMetodoCaptura() + "já existe")));
	}

}