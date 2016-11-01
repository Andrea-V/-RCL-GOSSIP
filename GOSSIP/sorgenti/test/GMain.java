package test;

import java.net.InetAddress;

import main.RegistryServer;
import main.UserAgent;

/**
 * Contiene il metodo interpreta i parametri
 * passati da linea di comando.
 * @author Andrea
 * @version 1.0
 * @since 1.0
 */
public class GMain {
	public static void main(String[]args) throws Exception{		
		
		if(args.length==1 || args.length==2){
			if(args[0].equals("client")){
				InetAddress addr=InetAddress.getByName(args[1]);
				new UserAgent(addr,addr).start();
			}
			else if(args[0].equals("server"))
				new RegistryServer();
		}
		
		/*
		InetAddress rmiaddr=InetAddress.getByName(Const.RMI_SERVER_HOST);
		InetAddress tcpaddr=InetAddress.getByName(Const.TCP_SERVER_HOST);

		new RegistryServer();
		
		Thread.sleep(1000);
		
		new UserAgent(rmiaddr,tcpaddr).start();
		new UserAgent(rmiaddr,tcpaddr).start();
		new UserAgent(rmiaddr,tcpaddr).start();
		*/
	}
}
                                                                               