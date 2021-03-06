package com.epizootia.controller;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

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
	public ResponseEntity<Response<List<MetodoCaptura>>> listaTodos() {
		Response<List<MetodoCaptura>> response = new Response<List<MetodoCaptura>>();

		List<MetodoCaptura> metodosCaptura = service.findAll();

		if (metodosCaptura.isEmpty()) {

			log.error("Não há Metodos de Captura cadastrados");
			response.getErrors().add("Não há Metodos de Captura cadastrados");

			return ResponseEntity.badRequest().body(response);
		}

		response.setData(metodosCaptura);

		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * Consulta todas Metodos de Captura por id
	 * 
	 * @return List<MetodoCapturaDTO>
	 */

	@GetMapping(value = "/{id}")
	public ResponseEntity<Response<MetodoCaptura>> consulta(@PathVariable("id") int id) {

		Response<MetodoCaptura> response = new Response<MetodoCaptura>();
		Optional<MetodoCaptura> metodoCaptura = service.findById(id);

		if (!metodoCaptura.isPresent()) {
			log.error("Id de Metodos de Captura não cadastrado na base de dados");
			response.getErrors().add("Id  de Metodos de Captura não cadastrado na base de dados");
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(metodoCaptura.get());

		log.info("Consulta de Metodos de Captura {}", metodoCaptura);

		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * Cadastra novo Metodo de Captura na base de dados
	 *	
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<MetodoCaptura>> cadastrar(@Valid @RequestBody MetodoCaptura metodoCaptura,
			BindingResult result) throws NoSuchAlgorithmException {
		log.info("Cadastrando Metodo de Captura do animal {}", metodoCaptura.toString());

		Response<MetodoCaptura> response = new Response<>();
		validaSeExiste(metodoCaptura, result);

		if (result.hasErrors()) {
			log.error("Erro ao validar informações: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.service.persistir(metodoCaptura);
		response.setData(metodoCaptura);
		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * Deleta Metodo de Captura da base de dados
	 */
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Response<MetodoCaptura>> apagar(@PathVariable("id") int id) {

		Response<MetodoCaptura> response = new Response<MetodoCaptura>();
		Optional<MetodoCaptura> metodoCaptura = service.findById(id);

		if (!metodoCaptura.isPresent()) {
			log.error("Id de Metodo de Captura não cadastrado na base de dados");
			response.getErrors().add("Id de Metodo de Captura não cadastrado na base de dados");
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(metodoCaptura.get());
		service.apagar(metodoCaptura.get());
		log.info("Deletando Recomendação Vacinal do Animal {}", metodoCaptura);

		return ResponseEntity.ok(response);
	}
	/**
	 * 
	 * Valida se o Metodo de Captura ja existe na base de dados
	 * 
	 * @param result
	 */
	private void validaSeExiste(MetodoCaptura metodoCaptura, BindingResult result) {
		this.service.findById(metodoCaptura.getId()).ifPresent(mcp -> result
				.addError(new ObjectError("Metodo de Captura", metodoCaptura.getMetodoCaptura() + "já existe")));
	}

}