package curso.apirest.controller;


import curso.apirest.model.Usuario;
import curso.apirest.model.UsuarioDTO;
import curso.apirest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Essa anotação permite o acesso de qualquer servidor

@RestController
@RequestMapping(value = "/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping(value="/{id}",produces="application/json")//headers = "X-API-Version=v1")
    @CacheEvict(value="cacheuser", allEntries = true)
    @CachePut("cacheuser")
    public ResponseEntity<UsuarioDTO> inicioV1(@PathVariable (value = "id") Long id){

        Optional <Usuario> usuario = usuarioRepository.findById(id);
       
        return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()),HttpStatus.OK);
    }

    

    @GetMapping(value="/consultartodos",produces = "application/json")
    @CacheEvict(value="cacheusuarios", allEntries = true)
    @CachePut("cacheusuarios")
    public ResponseEntity<List<UsuarioDTO>> usuario (){

        List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
        List<UsuarioDTO> listdtoDtos = list.stream()
        		.map(usuario ->{
        			
        			UsuarioDTO usuarioDTO = new UsuarioDTO(usuario);
        			usuarioDTO.setUserlogin(usuario.getLogin());
        			usuarioDTO.setUsernome(usuario.getNome());
        			usuarioDTO.setUsercpf(usuario.getCpf());
        			
        			return usuarioDTO;
        		})
        		
              .collect(Collectors.toList());
        return new ResponseEntity<List<UsuarioDTO>>(listdtoDtos,HttpStatus.OK);
    }



    @PostMapping(value = "/cadastrar", produces = "application/json")
    public ResponseEntity<Usuario>cadastrar(@RequestBody Usuario usuario) throws Exception{

        String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
        usuario.setSenha(senhacriptografada);

        for(int pos = 0; pos < usuario.getTelefones().size();pos ++) {

            usuario.getTelefones().get(pos).setUsuario(usuario);

        }
        
        //** Consumindo uma api pública externa
        
           URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
           URLConnection connection = url.openConnection();
           InputStream is = connection.getInputStream();
           BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
           
           String cep = "";
           StringBuilder jsoncep = new StringBuilder();
           
           while((cep = br.readLine()) !=null) {
        	   
        	   jsoncep.append(cep);
           }
           
           Usuario userAux = new Gson().fromJson(jsoncep.toString(), Usuario.class);
           usuario.setCep(userAux.getCep());
           usuario.setLogradouro(userAux.getLogradouro());
           usuario.setComplemento(userAux.getComplemento());
           usuario.setBairro(userAux.getBairro());
           usuario.setLocalidade(userAux.getLocalidade());
           usuario.setUf(userAux.getUf());
           
           
        

        //** Consumindo uma api pública externa

        Usuario usersave = usuarioRepository.save(usuario);

        return new ResponseEntity<Usuario>(usersave,HttpStatus.OK);

    }

    @PutMapping(value = "/", produces = "application/json")
    public ResponseEntity<Usuario>atualizar(@RequestBody Usuario usuario){


        for(int pos = 0; pos < usuario.getTelefones().size();pos ++) {

            usuario.getTelefones().get(pos).setUsuario(usuario);

        }

        Usuario userTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());

        if(!userTemporario.getSenha().equals(usuario.getSenha())){  /* Essa condição vai ser aplicada
        se as senhas forem diferentes*/

            String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
            usuario.setSenha(senhacriptografada);

        }

        Usuario usersave= usuarioRepository.save(usuario);

        return new ResponseEntity<Usuario>(usersave,HttpStatus.OK);

    }

    @DeleteMapping(value="/{id}" , produces = "application/text")
    public String delete (@PathVariable("id") Long id) {

        usuarioRepository.deleteById(id);

        return "ok";


    }


}
