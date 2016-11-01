package main;

import java.io.IOException;

import java.net.ServerSocket;

import java.rmi.*;
import java.rmi.registry.*;

import java.util.concurrent.ConcurrentHashMap;

import interfaces.GossipCallbacks;
import threads.TcpListener;
import utils.Const;

/**
 * Implementa il Registry. Ha il compito di occuparsi delle connessioni tcp e rmi,
 * di tenere traccia dello stato di tutti gli utenti e di notificare eventuali cambiamenti
 * mediante le callbacks.
 * @author Andrea
 * @version 1.0
 * @since 1.0
 */
public class RegistryServer{
	//STATUS
	/**
	 * Utenti auutalmente registrati.
	 */
	private ConcurrentHashMap<String,User>users=new ConcurrentHashMap<String,User>();
	/**
	 * Callbacks registrate.
	 */
	private ConcurrentHashMap<String,GossipCallbacks>callbacks=new ConcurrentHashMap<String,GossipCallbacks>();
	
	//COMM
	/**
	 * Interfaccia RMI.
	 */
	private RmiServer stub;

	/**
	 * Registry RMI.
	 */
	private Registry registry;
	
	/**
	 * Socket TCP passivo.
	 */
	private ServerSocket tcp_socket;
	
	/**
	 * Inizializza le connessioni tcp e rmi e crea il thread di
	 * ascolto tcp.
	 */
	public RegistryServer(){
		init_rmi_server();
		init_tcp_server();
		System.out.println("RegistryServer up and running.");
		
		new TcpListener(this).start();
	}
	
	/**
	 * Inizializza connessione rmi lato server.
	 */
	private void init_rmi_server(){
		try{
			stub=new RmiServer(this);
			LocateRegistry.createRegistry(Const.DEFAULT_RMI_PORT);
			registry=LocateRegistry.getRegistry(Const.DEFAULT_RMI_PORT);
			registry.rebind(Const.RMI_SERVER_NAME,stub);
		}
		catch(RemoteException exc){
			System.err.println("Eccezione Server RMI: "+exc.getMessage());
			exc.printStackTrace();
			System.exit(Const.FAILURE);
		}
	}
	
	/**
	 * Inizializza connessione tcp lato server.
	 */
	private void init_tcp_server(){
		try{
			tcp_socket=new ServerSocket(Const.TCP_SERVER_PORT);
		}
		catch (IOException exc){
			System.err.println("Eccezione Server TCP: "+exc.getMessage());
			exc.printStackTrace();
			System.exit(Const.FAILURE);
		}
	}
	
	/**
	 * @return hash map degli utenti registrati.
	 */
	public ConcurrentHashMap<String,User>getUsers(){
		return this.users;
	}
	
	/**
	 * @return hash map delle callbacks degli utenti registrati.
	 */
	public ConcurrentHashMap<String,GossipCallbacks>getCallbacks(){
		return this.callbacks;
	}
	
	/**
	 * @return ServerSocket a cui il Registry è correntemente associato.
	 */
	public ServerSocket getServerSocket(){
		return this.tcp_socket;
	}
}
