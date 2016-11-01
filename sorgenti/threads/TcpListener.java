package threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import main.RegistryServer;
import utils.Const;

/**
 *  Ascolta connessioni TCP entranti, invocando un thread worker StatusManager
 *  per ogni connessione nuova.
 *  
 * @author Andrea
 * @see Thread
 * @version 1.0
 * @since 1.0
 */
public class TcpListener extends Thread {
	
	/**
	 * ServerSocket del RegistryServer.
	 */
	private ServerSocket tcp_socket;

	/**
	 * Riferimento al RegistryServer
	 */
	private RegistryServer rs;
	
	/**
	 * Inizializza lo stato con i riferimenti del RegistryServer.
	 * @param rs RegistryServer
	 */
	public TcpListener(RegistryServer rs){
		super();
		this.setDaemon(true);
		this.rs=rs;
		this.tcp_socket=rs.getServerSocket();
	}
	
	/**
	 * Si mette in ascolto di nuove connessioni, delgando ogni nuova 
	 * connessione creata a uno StatusManager.
	 */
	public void run(){
		while(true){
			Socket s;
			try{
				s=tcp_socket.accept();
				Thread t=new StatusManager(s,rs);
				t.start();
			}
			catch(IOException exc){
				System.err.println("Errore in connessione al server: "+exc.getMessage());
				exc.printStackTrace();
				System.exit(Const.FAILURE);
			}
		}
	}
}
