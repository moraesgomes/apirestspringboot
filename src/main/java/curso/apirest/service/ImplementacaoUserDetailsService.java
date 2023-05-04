package curso.apirest.service;


import curso.apirest.model.Usuario;
import curso.apirest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Consultar o usuário no banco de dados

        Usuario usuario = usuarioRepository.findUserByLogin(username);

        if(usuario == null){

            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        return new User(usuario.getLogin(), usuario.getPassword(),usuario.getAuthorities());
    }
}
