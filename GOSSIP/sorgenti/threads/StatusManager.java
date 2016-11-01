package threads;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import interfaces.GossipCallbacks;
import main.RegistryServer;
import main.User;
import utils.Const;
import utils.Contact;
import utils.StatusMessage;

/**
 * Thread invocato da TcpListener, processa uno StatusMessage, 
 * aggiornando lo stato del RegistryServer.
 * Invia opportune callbacks agli UserAgent interessati dal cambiamento.
 * 
 * @author Andrea
 * @see Thread
 * @version 1.0
 * @since 1.0
 */
public class StatusManager extends Thread {
	
	/**
	 * Socket di comunicazione con il client.
	 */
	private Socket socket;
	
	/**
	 * Mappa delle Callbacks.
	 */
	private ConcurrentHashMap<String,GossipCallbacks>callbacks;
	
	/**
	 * Mappa degli Users.
	 */
	private ConcurrentHashMap<String,User>users;
	
	
	/**
	 * Costruttore standard.
	 * @param s  socket di comunicazione con il client.
	 * @param rs  RegistryServer a cui lo StatusManager è associato. 
	 */
	public StatusManager(Socket s,RegistryServer rs){
		super();
		this.setDaemon(true);
		socket=s;
		users=rs.getUsers();
		callbacks=rs.getCallbacks();
	}
	
	/**
	 * Si mette in lettura sul socket, aspettando l'invio di 
	 * messaggi di stato dal client.
	 */
	public void run(){
		boolean stop=false;
		String nick="";
		
		while(!stop){
			try{
				ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
				StatusMessage msg=(StatusMessage)ois.readObject();
				nick=msg.getNickname();
				Enumeration<String> enu=users.keys();
				
				switch(msg.getNewStatus()){
					case Const.ONLINE:
						users.get(nick).setOnline(true);
						
						while(enu.hasMoreElements()){
							String k=enu.nextElement();
							User   v=users.get(k);
							
							if(!k.equals(nick) && v.isOnline()){
								User u=new User(users.get(nick));
								callbacks.get(v.getNickname()).notifyOnline(v,u);
								users.get(k).getInContacts().put(nick,new Contact(nick));
							}
						}		
						break;
					case Const.OFFLINE:
						users.get(msg.getNickname()).setOnline(false);
						
						while(enu.hasMoreElements()){
							String k=enu.nextElement();
							User   v=users.get(k);
							
							if(!k.equals(nick) && v.isOnline())
								callbacks.get(v.getNickname()).notifyOffline(v,new User(users.get(nick)));
						}
						break;
					case Const.CLOSED:
							System.out.println("chiudo connessione al server");
							socket.close();
							stop=true;
							break;
					default:
						System.err.println("Errore ricezione status: ");
						System.exit(Const.FAILURE);
				}
			}
			catch(SocketException exc){
				stop=true;
				//users.get(nick).setOnline(false);
				users.remove(nick);
				callbacks.remove(nick);
			}
			catch(Exception exc){
				System.err.println("Eccezione in ricezione messaggio TCP: "+exc.getMessage());
				exc.printStackTrace();
			}
		}
	}
}
