package utils;

/**
 * Eccezione sollevata al momento della regisrazione del nickname
 * nel caso il nickname sia gi� stato preso da un altro utente. 
 * @author Andrea
 * @see Exception
 * @version 1.0
 * @since 1.0
 */
public class NicknameTakenException extends Exception {
	private static final long serialVersionUID = -4236959358042612739L;
	
	/**
	 * nickname gi� utilizzato.
	 */
	String nick;
	
	/**
	 * Inizializza l'eccezione
	 * @param nick nickname gi� utilizzato
	 */
	public NicknameTakenException(String nick){
		super();
		this.nick=nick;
	}
	
	@Override
	public String getMessage(){
		return "Nickname gi� in uso: "+nick;
	}
}
