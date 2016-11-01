package main;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import utils.Contact;

/**
 * Rappresenta un'utente di GOSSIP. Lo UserAgent usa User per ricordare informazioni
 * riguardo lo stato del client, mentre RegistryServer rappresenta le informazioni
 * sugli utenti connessi con una Hash Map di User.
 * @author Andrea
 * @see Serializable
 * @version 1.0
 * @since 1.0
 */
public class User implements Serializable{
	private static final long serialVersionUID = -7820960917702503419L;
	
	/**
	 * Nickname dell'utente.
	 */
	private String 	nickname;
	
	/**
	 * Indirizzo ip del socket udp.
	 */
	private String udp_address;
	
	/**
	 * Porta udp.
	 */
	private Integer udp_port;
	
	/**
	 * Indica se l'utente è online od offline.
	 */
	private boolean online;
	
	/**
	 * Contatti in ingresso dell'utente.
	 */
	private volatile ConcurrentHashMap<String,Contact> in_contacts;
	
	/**
	 * Contatti in uscita dell'utente.
	 */
	private volatile ConcurrentHashMap<String,Contact> out_contacts;
	
	/**
	 * Istanzia uno user con nickname nick.
	 * @param nick - nickname dello user.
	 */
	public User(String nick){
		nickname=nick;
		online=false;
		udp_address=null;
		udp_port=null;
		in_contacts =new ConcurrentHashMap<String,Contact>();
		out_contacts=new ConcurrentHashMap<String,Contact>();
	}
	
	/**
	 * Costruttore di copia, istanzia uno user a partire da un altro user.
	 * @param u user da cui copiare
	 * */
	public User(User u){
		this.nickname=new String(u.nickname);
		this.udp_address=new String(u.udp_address);
		this.udp_port=new Integer(u.udp_port);
		this.online=u.online;
		this.in_contacts =new ConcurrentHashMap<String,Contact>(u.getInContacts());
		this.out_contacts=new ConcurrentHashMap<String,Contact>(u.getOutContacts());
	}
	
	/**
	 * 
	 * @param f nuovo stato dello user
	 */
	public void setOnline(boolean f){
		this.online=true;
	}
	
	/**
	 * 
	 * @return true se lo user è online, false altrimenti.
	 */
	public boolean isOnline(){
		return this.online;
	}
	
	/**
	 * 
	 * @return il nickname dello User
	 */
	public String getNickname(){
		return this.nickname;
	}
	
	/**
	 * 
	 * @return Indirizzo udp del socket associato allo User.
	 */
	public InetAddress getUdpAddress(){
		try {
			return InetAddress.getByName(this.udp_address);
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param addr nuovo indirizzo ip dello User.
	 */
	public void setUdpAddress(InetAddress addr){
		this.udp_address=addr.getHostAddress();
	}
	
	/**
	 * 
	 * @param p nuova porta dello User.
	 */
	public void setUdpPort(Integer p){
		this.udp_port=p;
	}
	
	/**
	 * 
	 * @return numero di porta cui è correntemente associato il socket dello user.
	 */
	public Integer getUdpPort(){
		return new Integer(this.udp_port);
	}

	/**
	 * 
	 * @return I contatti in ingresso di questo User
	 */
	public ConcurrentHashMap<String,Contact> getInContacts(){
		return this.in_contacts;
	}
	
	/**
	 * 
	 * @return I contatti in uscita di questo User.
	 */
	public ConcurrentHashMap<String,Contact> getOutContacts(){
		return this.out_contacts;
	}
	
	@Override
	public String toString(){
		String str="----------------------------------\n"; 
		str+="--- "+this.getNickname()+" ---\n";
		str+="- socket: "+this.getUdpAddress()+"\n";
		str+="- port: "+this.getUdpPort()+"\n";
		str+="----------------------------------\n";
		str+="- in: "+this.getInContacts()+"\n";
		str+="- out: "+this.getOutContacts()+"\n";
		str+="----------------------------------\n";
		return str;
	}
}
