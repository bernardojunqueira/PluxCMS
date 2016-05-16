package br.com.plux.cms.erro;

public final class UserAlreadyExistException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6279489966034452416L;

	public UserAlreadyExistException() {
        super();
    }

    public UserAlreadyExistException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistException(final String message) {
        super(message);
    }

    public UserAlreadyExistException(final Throwable cause) {
        super(cause);
    }

}
