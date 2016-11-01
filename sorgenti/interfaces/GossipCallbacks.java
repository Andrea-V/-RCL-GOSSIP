package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import main.User;

/**
 * Definisce le callback che i client devono registrare sul RegistryServer
 * al momento della comunicazione
 * @author Andrea
 * @see Remote
 * @version 1.0
 * @since 1.0
 */
public interface GossipCallbacks extends Remote {
	/**
	 * Notifica a caller che è stato selezionato nella lista di contatti
	 * in ingresso da u.
	 * @param c utente da notificare
	 * @param u utente che ha causato l'invocazione della callback.
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI
	 */
	public void notifyAdded(User c,User u)		throws RemoteException;
	
	/**
	 * Notifica a caller che è stato rimosso dalla lista dei contatti 
	 * in ingresso di u.
	 * @param c utente da notificare
	 * @param u utente che ha causato l'invocazione della callback
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI
	 */
	public void notifyRemoved(User c,User u)	throws RemoteException;
	
	/**
	 * Notifica a c che u è online.
	 * @param c utente da notificare
	 * @param u utente che ha causato l'invocazione della callback
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI
	 */
	public void notifyOnline(User c,User u)	throws RemoteException;
	/**
	 * Notifica a c che u è offline.
	 * @param c utente da notificare
	 * @param u utente che ha causato l'invocazione della callback
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI
	 */
	public void notifyOffline(User c,User u)	throws RemoteException;
}
