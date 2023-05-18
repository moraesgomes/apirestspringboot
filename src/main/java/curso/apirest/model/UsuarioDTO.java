package curso.apirest.model;

import java.io.Serializable;

public class UsuarioDTO implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String login;
	
	private String nome;
	
	private String senha;
	
	private String cpf;
	
	public UsuarioDTO(Usuario usuario) {
		
		this.id = usuario.getId();
		this.login = usuario.getLogin();
		this.nome = usuario.getNome();
		this.senha = usuario.getSenha();
		this.cpf = usuario.getCpf();
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	


	

}
