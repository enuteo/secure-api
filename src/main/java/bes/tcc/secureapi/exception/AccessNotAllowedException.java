package bes.tcc.secureapi.exception;

public class AccessNotAllowedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AccessNotAllowedException() {
		super("não tem permissão de acessar o recurso");
	}
}
