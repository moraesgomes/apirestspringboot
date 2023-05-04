package curso.apirest.model;

import java.io.Serializable;

public class UsuarioDTO implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	private String userlogin;
	
	private String usernome;
	
	private String usercpf;
	
	public UsuarioDTO(Usuario usuario) {
		
		this.userlogin = usuario.getLogin();
		this.usernome = usuario.getNome();
		this.usercpf = usuario.getCpf();
	}

	

	public String getUserlogin() {
		return userlogin;
	}

	public void setUserlogin(String userlogin) {
		this.userlogin = userlogin;
	}

	public String getUsernome() {
		return usernome;
	}

	public void setUsernome(String usernome) {
		this.usernome = usernome;
	}

	public String getUsercpf() {
		return usercpf;
	}

	public void setUsercpf(String usercpf) {
		this.usercpf = usercpf;
	}
	
	

}
