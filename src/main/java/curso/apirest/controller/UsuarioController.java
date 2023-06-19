package curso.apirest.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import curso.apirest.model.Profissao;
import curso.apirest.model.Telefone;
import curso.apirest.model.Usuario;
import curso.apirest.model.UsuarioDTO;
import curso.apirest.repository.ProfissaoRepository;
import curso.apirest.repository.TelefoneRepository;
import curso.apirest.repository.UsuarioRepository;
import curso.apirest.service.ImplementacaoUserDetailsService;

// Essa anotação permite o acesso de qualquer servidor
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private TelefoneRepository telefoneRepository;

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@Autowired
	private ProfissaoRepository profissaoRepository;

	@GetMapping(value = "/{id}", produces = "application/json")
	@CacheEvict(value = "cacheuser", allEntries = true)
	@CachePut("cacheuser")
	public ResponseEntity<UsuarioDTO> inicioV1(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		if (usuario.isPresent()) {
			List<Telefone> telefones = usuario.get().getTelefones();

			UsuarioDTO usuarioDTO = new UsuarioDTO(usuario.get(), telefones);

			return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(value = "/", produces = "application/json")
	// @CacheEvict(value="cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<UsuarioDTO>> usuario() {

		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));

		Page<Usuario> list = usuarioRepository.findAll(page);

		List<UsuarioDTO> listDtos = list.stream().map(usuario -> {

			List<Telefone> telefones = usuario.getTelefones();

			UsuarioDTO usuarioDTO = new UsuarioDTO(usuario, telefones);
			usuarioDTO.setLogin(usuario.getLogin());
			usuarioDTO.setNome(usuario.getNome());
			usuarioDTO.setCpf(usuario.getCpf());

			return usuarioDTO;
		})

				.collect(Collectors.toList());

		Page<UsuarioDTO> pageDtos = new PageImpl<>(listDtos, page, listDtos.size());

		return new ResponseEntity<>(pageDtos, HttpStatus.OK);
	}

	@GetMapping(value = "/page/{pagina}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<UsuarioDTO>> usuarioPagina(@PathVariable("pagina") int pagina) {

		PageRequest page = PageRequest.of(pagina, 3, Sort.by("nome"));

		Page<Usuario> list = usuarioRepository.findAll(page);

		List<UsuarioDTO> listDtos = list.stream().map(usuario -> {

			List<Telefone> telefones = usuario.getTelefones();

			UsuarioDTO usuarioDTO = new UsuarioDTO(usuario, telefones);
			usuarioDTO.setLogin(usuario.getLogin());
			usuarioDTO.setNome(usuario.getNome());
			usuarioDTO.setCpf(usuario.getCpf());

			return usuarioDTO;
		})

				.collect(Collectors.toList());

		Page<UsuarioDTO> pageDtos = new PageImpl<>(listDtos, page, listDtos.size());

		return new ResponseEntity<>(pageDtos, HttpStatus.OK);
	}

	@GetMapping(value = "/consultarnome/{nome}", produces = "application/json")
	// @CacheEvict(value="cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<UsuarioDTO>> usuarioPorNome(@PathVariable("nome") String nome) throws Exception {
		
		PageRequest page = null;
		Page<Usuario> list = null;
		
		if (nome == null || (nome != null && nome.trim().isEmpty())
				|| nome.equalsIgnoreCase("undefined")) {
			
			page = PageRequest.of(0, 3,Sort.by("nome"));
		    list = usuarioRepository.findAll(page);
			
		}else {
			
			page = PageRequest.of(0, 3,Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome,page);
		}

		
		List<UsuarioDTO> listDtos = list.stream().map(usuario -> {
			List<Telefone> telefones = usuario.getTelefones();

			UsuarioDTO usuarioDTO = new UsuarioDTO(usuario, telefones);

			usuarioDTO.setLogin(usuario.getLogin());
			usuarioDTO.setNome(usuario.getNome());
			usuarioDTO.setCpf(usuario.getCpf());

			return usuarioDTO;
		})

				.collect(Collectors.toList());
		Page<UsuarioDTO> pageDtos = new PageImpl<>(listDtos, page, listDtos.size());
		return new ResponseEntity<Page<UsuarioDTO>>(pageDtos, HttpStatus.OK);
	}
	
	
	@GetMapping(value = "/consultarnome/{nome}/page/{page}", produces = "application/json")
	// @CacheEvict(value="cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<UsuarioDTO>> usuarioPorNomePage(@PathVariable("nome") String nome, @PathVariable("page") int page) throws Exception {
		
		PageRequest pageRequest = null;
		Page<Usuario> list = null;
		
		if (nome == null || (nome != null && nome.trim().isEmpty())
				|| nome.equalsIgnoreCase("undefined")) {
			
			pageRequest = PageRequest.of(page, 3,Sort.by("nome"));
		    list = usuarioRepository.findAll(pageRequest);
			
		}else {
			
			pageRequest = PageRequest.of(page, 3,Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome,pageRequest);
		}

		
		List<UsuarioDTO> listDtos = list.stream().map(usuario -> {
			List<Telefone> telefones = usuario.getTelefones();

			UsuarioDTO usuarioDTO = new UsuarioDTO(usuario, telefones);

			usuarioDTO.setLogin(usuario.getLogin());
			usuarioDTO.setNome(usuario.getNome());
			usuarioDTO.setCpf(usuario.getCpf());

			return usuarioDTO;
		})

				.collect(Collectors.toList());
		Page<UsuarioDTO> pageDtos = new PageImpl<>(listDtos, pageRequest, listDtos.size());
		return new ResponseEntity<Page<UsuarioDTO>>(pageDtos, HttpStatus.OK);
	}

	@PostMapping(value = "/cadastrar", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {

		String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhacriptografada);

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {

			usuario.getTelefones().get(pos).setUsuario(usuario);

		}

		// Consumindo uma api pública externa

		if (usuario.getCep() != null) {

			URL url = new URL("https://viacep.com.br/ws/" + usuario.getCep() + "/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String cep = "";
			StringBuilder jsoncep = new StringBuilder();

			while ((cep = br.readLine()) != null) {

				jsoncep.append(cep);
			}

			Usuario userAux = new Gson().fromJson(jsoncep.toString(), Usuario.class);
			usuario.setCep(userAux.getCep());
			usuario.setLogradouro(userAux.getLogradouro());
			usuario.setComplemento(userAux.getComplemento());
			usuario.setBairro(userAux.getBairro());
			usuario.setLocalidade(userAux.getLocalidade());
			usuario.setUf(userAux.getUf());

		}
		


	
		
	 // Formatando a data de nascimento para o formato pt-BR
		
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    String dataNascimentoFormatada = sdf.format(usuario.getDataNascimento());
	    usuario.setDataNascimento(sdf.parse(dataNascimentoFormatada));

		Usuario usersave = usuarioRepository.save(usuario);

		implementacaoUserDetailsService.insereAcessoPadrao(usersave.getId());

		return new ResponseEntity<Usuario>(usersave, HttpStatus.OK);

	}

	@DeleteMapping(value = "/removerfone/{id}", produces = "application/text")
	public String deleteFone(@PathVariable("id") Long id) {

		telefoneRepository.deleteById(id);

		return "ok";

	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {

			usuario.getTelefones().get(pos).setUsuario(usuario);

		}

		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();

		if (!userTemporario.getSenha()
				.equals(usuario.getSenha())) { /*
												 * Essa condição vai ser aplicada se as senhas forem diferentes
												 */

			String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhacriptografada);

		}

		Usuario usersave = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usersave, HttpStatus.OK);

	}

	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable("id") Long id) {

		usuarioRepository.deleteById(id);

		return "ok";

	}

}
