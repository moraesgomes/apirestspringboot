package curso.apirest;

import java.sql.SQLException;

import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@ControllerAdvice
public class ControleExececoes extends ResponseEntityExceptionHandler {
	
	
	
	/*Tratamento da maioria dos erro a nível de banco de dados e aplicação*/
	@ExceptionHandler({DataIntegrityViolationException.class,ConstraintViolationException.class,
		PSQLException.class,SQLException.class,Exception.class,RuntimeException.class,Throwable.class})
	protected ResponseEntity<Object> handleExceptionDataIntegry(Exception ex) {
		
		String msg = "";
		

		if (ex instanceof TransactionSystemException) {
			
			msg = ((TransactionSystemException) ex).getCause().getCause().getMessage();
			
		}
		
		
		else if(ex instanceof DataIntegrityViolationException) {
			msg = ((DataIntegrityViolationException) ex).getCause().getCause().getMessage();
			
			
		}
		else if(ex instanceof PSQLException) {
			
			msg = ((PSQLException) ex).getCause().getCause().getMessage();
			
		}
		
        else if(ex instanceof SQLException) {
 			
			msg = ((SQLException) ex).getCause().getCause().getMessage();
			
		}
		
        else if(ex instanceof ConstraintViolationException) {
			
			msg = ((ConstraintViolationException) ex).getCause().getCause().getMessage();
			
		}
		
		else {
			
			msg = ex.getMessage();
		}
		
		ObjetoErro objetoErro = new ObjetoErro();
		objetoErro.setError(msg);
		objetoErro.setCode(HttpStatus.INTERNAL_SERVER_ERROR + "==>" + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		
		return new ResponseEntity<>(objetoErro , HttpStatus.INTERNAL_SERVER_ERROR);
	
	}

}
