package curso.apirest.security;


import curso.apirest.ApplicationContextLoad;
import curso.apirest.model.Usuario;
import curso.apirest.repository.UsuarioRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Service
@Component
public class JWTTokenAutenticacaoService {

    /*Tempo de Validade de 2 dias  - Token */
    private static final long EXPIRATION_TIME = 172800000;

    /*Uma senha única para compor a autenticação e ajudar na segurança*/
    private static final String SECRET = "SenhaExtremamenteSecreta";

    /*Prefixo padrão de Token */
    private static final String TOKEN_PREFIX = "Bearer";

    private static final String HEADER_STRING = "Authorization";

    /*Gerando Token de autenticação e adicionando ao cabeçalho e resposta Http */

    public void addAuthentication(HttpServletResponse response, String username) throws IOException {

        /*Montagem do Token*/
        String JWT = Jwts.builder() /* Chama o gerador de Token */
                .setSubject(username) /*Adiciona o usuário*/
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))/*Tempo de expiração*/
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();/*Compactação e algoritmos de geração de senha*/

        /*Junta o token com o prefixo*/
        String token = TOKEN_PREFIX + " " + JWT; /*Bearer  87877w8788w78877w*/

        /*Adiciona o cabeçalho com  o HTTP*/

        response.addHeader(HEADER_STRING, token); /*Authorization:Bearer 8888deeee8w885www888*/
        
        ApplicationContextLoad.getApplicationContext()
        .getBean(UsuarioRepository.class).atualizaTokenUser(JWT, username);

        /*Liberando resposta para portas diferentes que usam a API ou caso clientes web*/
        liberacaoCors(response);

        /*Escreve token como resposta no corpo do HTTP*/
        response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
    }

    /*Retorna o usuário validado com o token , caso contrário retorna null*/

    public Authentication getAuthentication(HttpServletRequest request ,HttpServletResponse response) {

        /*Pega o tokren enviado no cabeçalho http*/

        String token = request.getHeader((HEADER_STRING));
        
        try {

        if (token != null) {

            String tokenlimpo = token.replace(TOKEN_PREFIX, "").trim();

            /*Faz a validação do token do usuário na requisição */

            String user = Jwts.parser().setSigningKey(SECRET)
                    .parseClaimsJws(tokenlimpo)
                    .getBody().getSubject(); /* Retorno do usuário*/
            if (user != null) {

                Usuario usuario = ApplicationContextLoad.getApplicationContext()
                        .getBean(UsuarioRepository.class).findUserByLogin(user);

                /*Retorna o usuário logado*/

                if (usuario != null ) {

                    if(tokenlimpo.equalsIgnoreCase(usuario.getToken())) {

                        return new UsernamePasswordAuthenticationToken
                                (usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
                    }
                }

            }

        }
        
        
        }catch (ExpiredJwtException e) {
        	
        	try {
				response.getOutputStream().println("Seu token esta expirado ,faça o login ou informe um novo token");
			} catch (IOException e1) {
				
				
			}
			
		}
        
        liberacaoCors(response);
        return null;

    }

    private void liberacaoCors(HttpServletResponse response) {

        if (response.getHeader("Access-Control-Allow-Origin") == null) {
            response.addHeader("Access-Control-Allow-Origin", "*");
        }

        if (response.getHeader("Access-Control-Allow-Headers") == null) {
            response.addHeader("Access-Control-Allow-Headers", "*");
        }


        if (response.getHeader("Access-Control-Request-Headers") == null) {
            response.addHeader("Access-Control-Request-Headers", "*");
        }

        if(response.getHeader("Access-Control-Allow-Methods") == null) {
            response.addHeader("Access-Control-Allow-Methods", "*");
        }
    }

}



