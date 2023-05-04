package curso.apirest.security;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/* Filtro onde todas as requisções serão capturadas para autenticar */
public class JwtApiAutenticacaoFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        /*Estabelec autenticação para a requisição*/

        Authentication authentication = new JWTTokenAutenticacaoService().getAuthentication((HttpServletRequest) request ,
                (HttpServletResponse) response);

        /* Coloca o processo de autenticação no spring security */

        SecurityContextHolder.getContext().setAuthentication(authentication);

        /* Continua o processo */

        filterChain.doFilter(request,response);

    }
}
