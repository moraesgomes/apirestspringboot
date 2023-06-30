package curso.apirest.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import curso.apirest.ObjetoErro;
import curso.apirest.model.Usuario;
import curso.apirest.repository.UsuarioRepository;
import curso.apirest.service.ServiceEnviaEmail;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServiceEnviaEmail serviceEnviaEmail;
	
	@ResponseBody
	@PostMapping(value = "/")
	public ResponseEntity<ObjetoErro>recuperar(@RequestBody Usuario login) throws Exception{
		
		ObjetoErro objetoErro = new ObjetoErro();
		
		Usuario user = usuarioRepository.findUserByLogin(login.getLogin());
		
		if(user == null) {
			
			objetoErro.setCode("404");
			objetoErro.setError("Usuário não localizado");
				
		}else {
			
			/*Rotina de envio de e-mail*/
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String senhaNova = dateFormat.format(Calendar.getInstance().getTime());
			
			String senhaCriptografada = new BCryptPasswordEncoder().encode(senhaNova);
			usuarioRepository.updateSenha(senhaCriptografada, user.getId());
			
			serviceEnviaEmail.enviarEmail("Recuperação de Login", user.getLogin(), "Sua nova senha é : " + senhaNova);
			
			objetoErro.setCode("200");
			objetoErro.setError("Acesso enviado para o seu e-mail!");
			
		}
		
		return new ResponseEntity<ObjetoErro>(objetoErro,HttpStatus.OK);
		
		
		
	}

}
