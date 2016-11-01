package utils;

/**
 * Eccezione sollevata al momento della regisrazione del nickname
 * nel caso il nickname sia già stato preso da un altro utente. 
 * @author Andrea
 * @see Exception
 * @version 1.0
 * @since 1.0
 */
public class NicknameTakenException extends Exception {
	private static final long serialVersionUID = -4236959358042612739L;
	
	/**
	 * nickname già utilizzato.
	 */
	String nick;
	
	/**
	 * Inizializza l'eccezione
	 * @param nick nickname già utilizzato
	 */
	public NicknameTakenException(String nick){
		super();
		this.nick=nick;
	}
	
	@Override
	public String getMessage(){
		return "Nickname già in uso: "+nick;
	}
}
