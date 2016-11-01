package utils;

import java.io.Serializable;

/**
 * Rappresenta un messaggio di notifica del cambiamento di stato
 * di uno dei client.
 * @author Andrea
 * @see Serializable
 * @version 1.0
 * @since 1.0
 */
public class StatusMessage implements Serializable{
	private static final long serialVersionUID = -8051783205179627040L;
	/**
	 * Nickname dell'autore dello StatusMessage
	 */
	private String nickname;
	
	/**
	 * Contenuto: nuovo stato di nickname.
	 */
	private int status;
	
	/**
	 * Costruttore standard
	 * @param n nickname dello user che ha cambiato il suo stato.
	 * @param s nuovo stato dello user.
	 */
	public StatusMessage(String n,int s){
		this.nickname=n;
		this.status=s;
	}
	
	/**
	 * @return nickname del mittente
	 */
	public String getNickname(){
		return this.nickname;
	}
	
	/**
	 * @return  nuovo status del mittente
	 */
	public int getNewStatus(){
		return this.status;
	}
	

	@Override
	public String toString(){
		return "{"+getNickname()+","+getNewStatus()+"}";
	}
}
