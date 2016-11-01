package main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import interfaces.GossipCallbacks;
import utils.Contact;

/**
 * Implementazione di GossipCallbacks, interfaccia che definisce le callback
 * a disposizione del server.
 * @author Andrea
 * @see GossipCallbacks
 * @since 1.0
 */
public class RmiClient extends UnicastRemoteObject implements GossipCallbacks {
	private static final long serialVersionUID = -5843004051450456612L;
	
	/**
	 * UserAgent a cui fa riferimento il RmiClient.
	 */
	private UserAgent ua;
	
	/**
	 * Inizializza lo RmiClient con un riferimento allo UserAgent.
	 * @param ua - UserAgent
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI
	 */
	public RmiClient(UserAgent ua) throws RemoteException{
		super();
		this.ua=ua;
	}

	@Override
	public void notifyAdded(User caller,User u) throws RemoteException {
		caller.getOutContacts().put(u.getNickname(),new Contact(u.getNickname()));

		synchronized(ua){
			ua.setUser(caller);
		}
		
		ua.refresh_jlist(ua.getOutList(),caller.getOutContacts(),true,false);
		ua.refresh_jlist(ua.getOlList(),caller.getOutContacts(),false,true);
	}

	@Override
	public void notifyRemoved(User caller,User u) throws RemoteException {
		caller.getOutContacts().remove(u.getNickname());
		
		synchronized(ua){
			ua.setUser(caller);
		}
		
		ua.refresh_jlist(ua.getOutList(),caller.getOutContacts(),true,false);
		ua.refresh_jlist(ua.getOlList(),caller.getOutContacts(),false,true);
	}
	
	@Override
	public void notifyOnline(User caller,User u){
		caller.getInContacts().put(u.getNickname(),new Contact(u.getNickname()));
		
		synchronized(ua){
			ua.setUser(caller);
		}
		
		ua.refresh_jlist(ua.getInList(),caller.getInContacts(),true,false);
	}
	
	@Override
	public void notifyOffline(User caller,User u){	
		caller.getInContacts().remove(u.getNickname());
		caller.getOutContacts().remove(u.getNickname());
		
		synchronized(ua){
			ua.setUser(caller);
		}
		
		ua.refresh_jlist(ua.getInList(),caller.getInContacts(),true,false);
		ua.refresh_jlist(ua.getOutList(),caller.getOutContacts(),true,false);
		ua.refresh_jlist(ua.getOlList(),caller.getOutContacts(),false,true);
	}
}
