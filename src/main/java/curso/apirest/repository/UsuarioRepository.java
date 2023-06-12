package curso.apirest.repository;


import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import curso.apirest.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

      @Query("select u from Usuario u where u.login = ?1")
      Usuario findUserByLogin(String login);
      
      @Query("select u from Usuario u where u.nome like %?1%")
      List<Usuario> findUserByNome (String nome);
      
      @Modifying
      @Transactional
      @Query(nativeQuery = true, value = " update usuario set token = ?1 where login = ?2")
      void atualizaTokenUser(String token, String login);
      
      @Transactional
      @Modifying
      @Query(nativeQuery = true,value = "insert into usuarios_role (usuario_id, role_id) values(?1,(select id from role where "
      		+ "nome_role='ROLE_USER'));")
      void insereAcessoRolePadrao(Long idUser);

	default Page<Usuario> findUserByNamePage(String nome, PageRequest page){
		
		Usuario usuario = new Usuario();
		usuario.setNome(nome);
		
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		Example<Usuario> example = Example.of(usuario,exampleMatcher);
		
		Page<Usuario> retorno = findAll(example,page);
		
		return retorno;
	}
      
      
     
}
