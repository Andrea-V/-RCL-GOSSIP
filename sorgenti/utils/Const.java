package utils;

/**
 * Classe statica contenente alcune costanti di utilità.
 * @author Andrea
 * @version 1.0
 * @since 1.0
 */
public class Const {
	/**
	 * Dimensione buffer del socket UDP.
	 */
	public static final int MAX_UDP_BUF =1024;
	
	/**
	 * porta su cui verrà creato il registry RMI.
	 */
	public static final int DEFAULT_RMI_PORT=1099;
	
	/**
	 * Nome del registry RMI.
	 */
	public static final String RMI_SERVER_NAME="GOSSIP_RMI";
	
	/**
	 * Locazione di default del registry RMI.
	 */
	public static final String RMI_SERVER_HOST="localhost";
	
	/**
	 * Valore StatusMessage: l'utente è online.
	 */
	public static final int ONLINE =0;
	
	/**
	 * Valore StatusMessage: l'utente è offline.
	 */
	public static final int OFFLINE=1;
	
	/**
	 * Valore StatusMessage: l'utente ha chiuso il client.
	 */
	public static final int CLOSED=4;
	
	/**
	 * Valore status UserAgent: inizializzazione.
	 */
	public static final int INIT=2;
	
	/**
	 * Valore status UserAgent: dopo registrazione.
	 */
	public static final int AFTER_REG=3;
	
	/**
	 * indirizzo di default server TCP.
	 */
	public static final String TCP_SERVER_HOST="localhost";
	
	/**
	 * porta TCP di default.
	 */
	public static final int TCP_SERVER_PORT=7777;
	
	/**
	 * Esito: fallimento.
	 */
	public static final int FAILURE=-1;
	
	/**
	 * Esito: successo.
	 */
	public static final int SUCCESS=0;
}
