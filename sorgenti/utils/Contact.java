package utils;

import java.io.Serializable;

/**
 * Rappresente un contatto nelle lista di ingresso uscita dello User.
 * @author Andrea
 * @version 1.0
 * @since 1.0
 */
public class Contact implements Serializable{
	private static final long serialVersionUID = -6802359939766294845L;
	
	/**
	 * Nome del contatto.
	 */
	private String nickname;
	
	/**
	 * Indica se il contatto è selezionato o meno.
	 */
	private boolean selected;
	
	/**
	 * Inizializza il nome del contatto
	 * @param str nome del contatto
	 */
	public Contact(String str){
		nickname=str;
		selected=false;
	}
	
	/**
	 * Inizializza contatto con nome e stato attuale
	 * @param str nome
	 * @param sel stato attuale
	 */
	public Contact(String str,boolean sel){
		nickname=str;
		selected=sel;
	}
	
	/**
	 * Costruttore di copia.
	 * Inizializza il contatto con gli stessi valori di c.
	 * @param c contatto da cui copiare
	 */
	public Contact(Contact c){
		nickname=c.getNickname();
		selected=c.isSelected();
	}
	
	/**
	 * 
	 * @return true se il contatto è selezionato, false altrimenti.
	 */
	public boolean isSelected(){
		return selected;
	}
	
	/**
	 * Imposta se il contatoo è selezionato o no.
	 * @param f nuovo valore
	 */
	public void setSelected(boolean f){
		selected=f;
	}
	
	/**
	 * 
	 * @return nickname del contatto
	 */
	public String getNickname(){
		return nickname;
	}
	
	@Override
	public boolean equals(Object o){
		Contact c=(Contact)o;
		return nickname.equals(c.getNickname());
	}
	
	@Override
	public String toString(){
		return "<"+this.getNickname()+","+this.isSelected()+">";
	}
}
