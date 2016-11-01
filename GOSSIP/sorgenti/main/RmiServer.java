package main;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import interfaces.GossipCallbacks;
import interfaces.GossipManager;
import utils.Contact;
import utils.NicknameTakenException;

/**
 * Implementa l'interfaccia GossipManager contenente i metodi da eseguire 
 * sul server mediante il protocollo rmi.
 * @author Andrea
 * @see GossipManager
 * @version 1.0
 * @since 1.0
 */
public class RmiServer extends UnicastRemoteObject implements GossipManager{
	private static final long serialVersionUID = 3790591308627980321L;
	
	/**
	 * Lista utenti registrati
	 */
	private ConcurrentHashMap<String,User> 				users;
	
	/**
	 * Lista delle callbacks registrate.
	 */
	private ConcurrentHashMap<String,GossipCallbacks> callbacks;
	
	/**
	 * Costruttore standard, inizializza il RmiServer con
	 * i riferimenti al RegistryServer opportuno.
	 * @param rs RegistryServer
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI se l'invocazione del metodo remoto fallisce.
	 */
	public RmiServer(RegistryServer rs) throws RemoteException{
		super();
		this.users=rs.getUsers();
		this.callbacks=rs.getCallbacks();
	}

	@Override
	public void register(User u) throws RemoteException,NicknameTakenException {
		String nick=u.getNickname();
		
		if(users.get(nick)!=null)
			throw new NicknameTakenException(nick);
		else
			users.put(nick,new User(u));
	}

	@Override
	public User login(User u,GossipCallbacks stub) throws RemoteException, NullPointerException{
		String nick=u.getNickname();
		
		u.getInContacts().putAll(getInContactNames(users,nick));
		//u.getOutContacts().putAll(getOutContactNames(users,nick));
		
		u.setOnline(true);

		users.put(nick,new User(u));
		callbacks.put(nick,stub);
		
		return u;
	}

	@Override
	public User logout(User u) throws RemoteException {
		u. getInContacts().clear();
		u. getOutContacts().clear();
		u. setOnline(false);
		users.put(u.getNickname(),new User(u));

		return u;
	}
	
	@Override
	public User addInContact(User caller,String cname) throws RemoteException {
		String nick=caller.getNickname();
		
		caller.getInContacts().get(cname).setSelected(true);
		
		users.get(nick).getInContacts().get(cname).setSelected(true);
		users.get(cname).getOutContacts().put(nick,new Contact(nick));
		
		User u=users.get(cname);
		if(u.isOnline())
			callbacks.get(cname).notifyAdded(users.get(cname),caller);
		
		return caller;
	}
	
	@Override
	public User remInContact(User caller,String cname) throws RemoteException {
		String nick=caller.getNickname();
		
		caller.getInContacts().get(cname).setSelected(false);
		users.get(nick).getInContacts().get(cname).setSelected(false);
		users.get(cname).getOutContacts().remove(nick);
		
		User u=users.get(cname);
		if(u.isOnline())
			callbacks.get(cname).notifyRemoved(users.get(cname),caller);

		return caller;
	}
	
	@Override
	public User addOutContact(User caller,String cname) throws RemoteException {
		String nick=caller.getNickname();

		caller.getOutContacts().get(cname).setSelected(true);
		users.get(nick).getOutContacts().get(cname).setSelected(true);

		return new User(users.get(cname));
	}
	
	@Override
	public User remOutContact(User caller,String cname) throws RemoteException {
		String nick=caller.getNickname();
		
		caller.getOutContacts().get(cname).setSelected(false);
		users.get(nick).getOutContacts().get(cname).setSelected(false);

		return caller;
	}
	
	/**
	 * Restituisce una hash map di tutti i contatti memorizzati sul server,
	 * che verranno usati per inizializzare i contatti in ingresso dello User.
	 * @param hm mappa degli utenti presenti in GOSSIP.
	 * @param caller nickname dell'utente da inizializzare
	 * @return mappa degli utenti in ingresso, pronta per essere assegnata all'utente.
	 */
	private ConcurrentHashMap<String,Contact> getInContactNames(ConcurrentHashMap<String,User> hm,String caller){
		Enumeration<String> enu=hm.keys();
		ConcurrentHashMap<String,Contact> map=new ConcurrentHashMap<String,Contact>();
		
		while(enu.hasMoreElements()){
			String k=enu.nextElement();
			if(!caller.equals(k))
				map.put(k,new Contact(k));
		}
		
		return map;
	}
	
	/*
	private ConcurrentHashMap<String,Contact> getOutContactNames(ConcurrentHashMap<String,User> hm,String caller){
		Enumeration<String> 	  c=hm.keys();
		ConcurrentHashMap<String,Contact> map=new ConcurrentHashMap<String,Contact>();
		
		while(c.hasMoreElements()){
			String k=c.nextElement();
			User   v=hm.get(k);
	
			Contact cc=v.getInContacts().get(caller);
			
			if(cc!=null && cc.isSelected() && v.isOnline())
				map.put(k,new Contact(k));
		}
		
		return map;
	}
	*/
}
