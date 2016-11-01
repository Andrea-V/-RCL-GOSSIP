package utils;

import java.io.Serializable;

/**
 * Rappresenta il formato dei messaggi. Il Message è inviato su udp
 * agli altri peer.
 * @author Andrea
 * @see Serializable
 * @since 1.0 
 * @version 1.0
 */
public class Message implements Serializable{
	private static final long serialVersionUID = -8119286115335025198L;
	
	/**
	 * Autore del messaggio.
	 */
	private String author;
	
	/**
	 * Contenuto del messaggio.
	 */
	private String content;
	
	/**
	 * Inizializza autore e contenuto del messaggio.
	 * @param auth autore del messaggio
	 * @param con  contenuto del messaggio
	 */
	public Message(String auth,String con){
		author=auth;
		content=con;
	}
	
	/**
	 * Ritorna l'autore (mittente) del messaggio.
	 * @return mittente 
	 */
	public String getAuthor(){
		return this.author;
	}
	
	/**
	 * Titorna il contenuto del messaggio.
	 * @return contenuto
	 */
	public String getContent(){
		return this.content;
	}
	
	/**
	 * Imposta l'autore del messaggio a author
	 * @param author nuovo autore del messaggio
	 */
	public void setAuthor(String author){
		this.author=author;
	}
	
	/**
	 * Imposta il contenuto del messaggio a content
	 * @param content nuovo contenuto del messaggio
	 */
	public void setContent(String content){
		this.content=content;
	}
}
