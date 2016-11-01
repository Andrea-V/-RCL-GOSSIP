package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import main.User;
import utils.NicknameTakenException;

/**
 * Interfaccia dell'oggetto remoto Rmi lato server.
 * I client utilizzano i metodi specificati in questa interfaccia per comunicare
 * al server eventi importanti relativi all'interfaccia come registrazione, login,
 * logout, aggiunta/rimozione di un contatto nelle liste di ingresso/uscita.
 * @author Andrea
 * @see Remote
 * @see RemoteException
 * @version 1.0
 * @since 1.0
 */
public interface GossipManager extends Remote {
	
	/**
	 * Registra un utente sul server. Lancia una NicknameTakenException
	 * se il nickname dell'utente da registrare è già stato utilizzato
	 * da un altro utente.
	 * @param u utente da registrare
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI
	 * @throws NicknameTakenException se il nickname del nuovo utente è già utilizzato da un altro utente.
	 */
	public void register(User u) throws RemoteException,NicknameTakenException;
	
	/**
	 * Eseguie il login di u al server.
	 * @param u utente che esegue il login.
	 * @param callbacks callbacks dell'utente da registrare.
	 * @return utente aggiornato
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI
	 */
	public User login(User u,GossipCallbacks callbacks) throws RemoteException;
	
	/**
	 * Esegue il logout di u dal server. 
	 * @param u utente che esegue il logout da server.
	 * @return caller aggiornato
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI
	 */
	public User logout(User u) throws RemoteException;
	
	/**
	 * Seleziona un contatto in ingresso tra la lista dei contatti
	 * di caller.
	 * @param caller utente a cui aggiungere il contatto
	 * @param contact nome del contatto da selezionare.
	 * @return nuovo stato di caller
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI se fallisce l'invocazione del metodo RMI se fallisce l'invocazione del metodo RMI se fallisce l'invocazione del metodo RMI se fallisce l'invocazione del metodo RMI se fallisce l'invocazione del metodo RMI se fallisce l'invocazione del metodo RMI
	 */
	public User addInContact(User caller,String contact) throws RemoteException;
	
	/**
	 * Seleziona un contatto in uscita dalla lista dei contatti di caller.
	 * @param caller utente da cui rimuovere il contatto
	 * @param contact nome contatto da aggiungere
	 * @return informazioni per comunicare con contact (in particolare
	 * indirizzo e porta del socket udp associato a contact)
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI
	 */
	public User addOutContact(User caller,String contact) throws RemoteException;
	
	/**
	 * Deseleziona un contatto in ingresso da caller.
	 * @param caller utente a cui aggiungere il contatto
	 * @param contact nome del contatto da deselezionare.
	 * @return caller aggiornato
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI 
	 */
	public User remInContact(User caller,String contact) throws RemoteException;
	
	/**
	 * Deseleziona un contatto in uscita da caller.
	 * @param caller utente da cui rimuovere il contatto
	 * @param contact nome del contatto da rimuovere
	 * @return caller aggiornato
	 * @throws RemoteException se fallisce l'invocazione del metodo RMI
	 */
	public User remOutContact(User caller,String contact) throws RemoteException;
}
