package threads;

import static java.lang.System.err;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.swing.JTextArea;

import utils.Const;
import utils.Message;

/**
 * Thread che ascolta il traffico udp diretto verso un determinato socket.
 * Si occupa di processare i messaggi in arrivo e aggiornare la textarea.
 * @author Andrea
 * @see Thread
 * @version 1.0
 * @since 1.0
 */
public class UdpListener extends Thread {
	/**
	 * Socket udp da cui ascoltare i messaggi.
	 */
	private DatagramSocket udpsocket;
	/**
	 * Textarea da aggiornare.
	 */
	private JTextArea ta_recv;
	
	/**
	 * Inizializza il listener con i riferimenti al socket udp ed alla textarea del client.
	 * @param skt socket da cui ascoltare
	 * @param ta JTextArea in cui verranno visualizzati i messaggi.
	 */
	public UdpListener(DatagramSocket skt,JTextArea ta){
		this.setDaemon(true);
		udpsocket=skt;
		ta_recv=ta;
	}
	
	
	/**
	 * Si mette in ascolto di messaggi in arrivo sul socket, legge, e processa i messaggi.
	 */
	public void run(){
		
		byte[] buffer=new byte[Const.MAX_UDP_BUF];
		
		while(true){
			try{
				DatagramPacket pkt=new DatagramPacket(buffer,buffer.length);
				udpsocket.receive(pkt);
				processMessage(pkt);
			}
			catch(IOException exc){
				err.println("Errore I/O in ricezione messaggio UDP!");
				err.println(exc);
			}
			catch(Exception exc){
				err.println(exc);
				System.exit(Const.FAILURE);
			}
		}
	}
	
	/**
	 * Processa un singolo Message in arrivo.
	 * @param pkt pacchetto letto
	 * @throws IOException se c'è un problema nella ricezione del messaggio
	 * @throws ClassNotFoundException se riceve un pacchetto che non corrisponde a un Message 
	 */
	private void processMessage(DatagramPacket pkt) throws IOException, ClassNotFoundException{
		
		ObjectInputStream is=new ObjectInputStream(new ByteArrayInputStream(pkt.getData()));
		Message msg=(Message)is.readObject();
		
		synchronized(ta_recv){
			ta_recv.append(msg.getAuthor()+" --> "+msg.getContent()+"\n");		
		}
	}
}
