package main;

import static java.lang.System.out;
import static java.lang.System.err;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.*;

import interfaces.GossipManager;
import threads.UdpListener;
import utils.*;

/**
 * Implementa un client GOSSIP. Gestisce la GUI, le connesioni UDP TCP e RMI,
 * gli eventi grafici e lo stato interno relativo al proprio User. 
 * @author Andrea
 * @see Thread
 * @see ActionListener
 * @see MouseListener
 * @see WindowListener
 * @version 1.0
 * @since 1.0
 */
public class UserAgent extends Thread implements ActionListener,MouseListener,WindowListener{
	
	/**
	 * Conta il numero di UserAgent correntemente aperti su questo host.
	 */
	public static int nistances=0;
	
	//STATUS
	/**
	 * Mappa hash delle informazioni relative ai contatti correntemente online.
	 */
	private ConcurrentHashMap<String,User> out_info=new ConcurrentHashMap<String,User>();
	
	/**
	 * Stato interno di questo utente.
	 */
	private User myself;
	
	/**
	 * stub delle callback.
	 */
	private RmiClient stub;
	
	//COMM
	/**
	 * Metodi remoti RMI.
	 */
	private GossipManager	rmi_server;
	
	/**
	 * Socket udp per comunicazione peer to peer.
	 */
	private DatagramSocket	udp_socket;
	
	/**
	 * Socket tcp per comunicazione con server.
	 */
	private Socket 			tcp_socket;
	
	/**
	 * Indirizzo del registry RMI.
	 */
	private InetAddress 	rmi_addr;
	
	/**
	 * Indirizzo del server TCP.
	 */
	private InetAddress 	tcp_addr;
	
	//GUI
	/**
	 * Finestra del client.
	 */
	private JFrame 		window=new JFrame("GOSSIP"); 
	
	/**
	 * Campo di testo usato per inviare messaggi ai peers.
	 */
	private JTextField 	tf_snd=new JTextField(50);
	
	/**
	 * Campo di testo usato per impostare il nickname.
	 */
	private JTextField 	tf_nickname=new JTextField(20);
	
	/**
	 * Textarea contenente i messaggi inviati a this.
	 */
	private JTextArea 	ta_recv		=new JTextArea(30,80);
	
	/**
	 * Visualizza informazioni sullo stato del client.
	 */
	private JLabel 		lbl_status=new JLabel("Inizializzazione...");
	
	/**
	 * Invia un messaggio a un peer.
	 */
	private JButton  	btn_snd=new JButton("Invia");
	/**
	 * Registra this sul server.
	 */
	private JButton 	btn_reg =new JButton("Registrati");
	
	/**
	 * Toggle stato online/offline.
	 */
	private JButton 	btn_log =new JButton("Passa Online");
	
	//LISTE
	/**
	 * Lista dei contatti in ingresso.
	 */
	private JList<String> in_cnts	=new JList<String>();
	
	/**
	 * Lista dei contatti in uscita.
	 */
	private JList<String> out_cnts	=new JList<String>();
	
	/**
	 * Lista dei contatto online.
	 */
	private JList<String> ol_cnts	=new JList<String>();
	
	
	/**
	 * Inizializza le connessioni dello UserAgent.
	 * @param rmi_host Indirizzo del server RMI.
	 * @param tcp_host Indirizzo del server TCP.
	 * @see InetAddress
	 */
	public UserAgent(InetAddress rmi_host,InetAddress tcp_host){
		super();
		this.rmi_addr=rmi_host;
		this.tcp_addr=tcp_host;
		init_rmi_connection();
		nistances++;
		setStatus("Pronto. Status: offline");
	}
	
	/**
	 * Costruisce l'interfaccia grafica.
	 */
	private void buildGUI(){
		JTabbedPane tabs=new JTabbedPane();
		JPanel panchat	=new JPanel();
		JPanel pancont	=new JPanel();
		JPanel panimp	=new JPanel();
		JPanel pancenter=new JPanel();
		JPanel paneast  =new JPanel();
		JPanel pan_lc	=new JPanel();
		
		
		//init
		tf_nickname.setFont(new Font("Courier",Font.BOLD,20));
		tf_snd.setFont(new Font("Courier",Font.PLAIN,20));
		ta_recv.setFont(new Font("Verdana", Font.BOLD, 14));
		lbl_status.setFont(new Font("Courier",Font.ITALIC,20));
		ta_recv.setEditable(false);
		setGUImode(Const.INIT);
		
		//lists init
		ol_cnts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		in_cnts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		out_cnts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		in_cnts.setSelectionModel(new ToggleListSelectionModel());
		out_cnts.setSelectionModel(new ToggleListSelectionModel());
		ol_cnts.setFixedCellWidth(200);
		in_cnts.setFixedCellWidth(200);
		out_cnts.setFixedCellWidth(200);
		
		//frame
		window.setSize(1200,700);
		window.setLocation(100,100);
		tabs.addTab("Chat",panchat);
		tabs.addTab("Contatti",pancont);
		tabs.addTab("Impostazioni",panimp);
		tabs.setSelectedIndex(2);
		window.add(tabs);
		
		//tab impostazioni
		panimp.add(new JLabel("Il tuo nickname:"));
		panimp.add(tf_nickname);
		panimp.add(btn_reg);
		
		//tab chat
		panchat.setLayout(new BorderLayout());
		pancenter.setLayout(new BoxLayout(pancenter,BoxLayout.Y_AXIS));
		paneast.setLayout(new BoxLayout(paneast,BoxLayout.Y_AXIS));
		pancenter.add(ta_recv);
		pancenter.add(pan_lc);
		pan_lc.add(tf_snd);
		pan_lc.add(btn_snd);
		pan_lc.add(btn_log);
		
		
		panchat.add(lbl_status,BorderLayout.SOUTH);
		panchat.add(pancenter,BorderLayout.CENTER);
		panchat.add(paneast,BorderLayout.EAST);
		paneast.add(new JLabel("Contatti online:"));
		paneast.add(ol_cnts);		
		
		//tab contatti
		JPanel pan_in =new JPanel();
		JPanel pan_out=new JPanel();
		pancont.setLayout(new GridLayout(1,2,100,100));
		pancont.add(pan_in);
		pancont.add(pan_out);
		pan_in.setLayout(new BoxLayout(pan_in,BoxLayout.Y_AXIS));
		pan_out.setLayout(new BoxLayout(pan_out,BoxLayout.Y_AXIS));
		pan_in.add(new JLabel("Contatti in ingresso:"));
		pan_in.add(in_cnts);
		pan_out.add(new JLabel("Contatti in uscita:"));
		pan_out.add(out_cnts);
		
		//listeners
		btn_snd.setName("snd");
		btn_snd.addActionListener(this);
		btn_reg.setName("reg");
		btn_reg.addActionListener(this);
		btn_log.setName("log");
		btn_log.addActionListener(this);
		in_cnts.setName("in");
		in_cnts.addMouseListener(this);
		out_cnts.setName("out");
		out_cnts.addMouseListener(this);
		
		window.addWindowListener(this);
		window.setVisible(true);
	}
	
	/**
	 * Abilita e disabilita i componenti grafici in base allo stato
	 * dello UserAgent.
	 * @param mode Stato interno dello UserAgent.
	 */
	private void setGUImode(int mode){
		switch(mode){
			case Const.ONLINE:
				btn_snd.setEnabled(true);
				tf_snd.setEditable(true);
				ol_cnts.setEnabled(true);
				in_cnts.setEnabled(true);
				out_cnts.setEnabled(true);
				btn_log.setText("Passa Offline");
				setStatus("Pronto. Status: Online");
				break;
			case Const.OFFLINE:
				btn_snd.setEnabled(false);
				tf_snd.setEditable(false);
				ol_cnts.setEnabled(false);
				in_cnts.setEnabled(false);
				out_cnts.setEnabled(false);
				btn_log.setText("Passa Online");
				setStatus("Pronto. Status: Offline");
				break;
			case Const.INIT:
				btn_snd.setEnabled(false);
				btn_log.setEnabled(false);
				tf_snd.setEditable(false);
				in_cnts.setEnabled(false);
				out_cnts.setEnabled(false);
				ol_cnts.setEnabled(false);
				in_cnts.setEnabled(false);
				out_cnts.setEnabled(false);
				break;
			case Const.AFTER_REG:
				tf_nickname.setEditable(false);
				btn_reg.setEnabled(false);
				btn_log.setEnabled(true);
				window.setTitle("GOSSIP - "+myself.getNickname());
				break;
		}
	}

	/**
	 * Individua la sorgente dell'evento, poi smista la gestione dello stesso 
	 * ad un metodo opportuno.
	 */
	public void actionPerformed(ActionEvent e) {
		JButton caller=(JButton)e.getSource();
		
		if(caller.getName().equals("snd"))			
			btn_snd_handler();
		else if(caller.getName().equals("reg"))
			btn_reg_handler();
		else if(caller.getName().equals("log"))
			btn_log_handler();
	}
	
	
	/**
	 * Gestisce l'evento del click del mouse su una delle JList dei contatti.
	 */
	public void mouseClicked(MouseEvent e) {
		JList<String>caller=(JList<String>)e.getSource();
		
		if(!caller.getSelectionModel().getValueIsAdjusting()){
			int i		=caller.locationToIndex(e.getPoint());
			String str	=caller.getModel().getElementAt(i);
			boolean sel	=caller.getSelectionModel().isSelectedIndex(i);
	
			if(caller.getName().equals("in") && in_cnts.isEnabled())
				in_cnts_handler(sel,str);
			else if(caller.getName().equals("out") && out_cnts.isEnabled())
				out_cnts_handler(sel,str);
		}
	}
	
	/**
	 * Inizializza connessione TCP.
	 */
	private void init_tcp_connection() {
		setStatus("Inizializzazione connessione TCP...");
		try{
			InetAddress addr=this.tcp_addr;
			this.tcp_socket=new Socket(addr,Const.TCP_SERVER_PORT);
		}
		catch(Exception exc){
			err.println("Errore connessione TCP: "+exc.getMessage());
			exc.printStackTrace();
			System.exit(Const.FAILURE);
		}
		setStatus("Pronto.");
	}
	
	/**
	 * Inizializza connessione UDP.
	 */
	private void init_udp_connection(){
		setStatus("Inizializzazione connessione UDP...");
		try{
			int port=get_free_port();
			
			this.udp_socket=new DatagramSocket(port,InetAddress.getLocalHost());
			out.println("Ho creato il socket.");
			out.println(udp_socket.getLocalAddress());
			out.println(udp_socket.getLocalPort());
			
			myself.setUdpAddress(udp_socket.getLocalAddress());
			myself.setUdpPort(udp_socket.getLocalPort());
		}catch(Exception exc){
			err.println("Problema con socket UDP!");
			err.println(exc);
			System.exit(Const.FAILURE);
		}
		setStatus("Pronto.");
	}
	
	/**
	 * Restituisce un numero di porta UDP libera.
	 * @return numero di porta UDP
	 */
	private int get_free_port() {
		DatagramSocket s=null;
		int port=0;
		
		try {
			s=new DatagramSocket();
			port=s.getLocalPort();
			s.close();
		}catch(SocketException exc){
			err.println(exc.getMessage());
			exc.printStackTrace();
			System.exit(Const.FAILURE);
		}
		return port;
	}

	/**
	 * Inizializza connessione RMI.
	 */
	private void init_rmi_connection(){
		setStatus("Inizializzazione connessione RMI...");
		try{
			Registry reg=LocateRegistry.getRegistry(this.rmi_addr.getHostAddress(),Const.DEFAULT_RMI_PORT);
			rmi_server=(GossipManager)reg.lookup(Const.RMI_SERVER_NAME);
		}
		catch(Exception exc){
			System.err.println("Errore con connessione RMI: "+exc.getMessage());
			System.exit(Const.FAILURE);
		}
		setStatus("Pronto.");
	}
	
	/**
	 * Imposta la label di stato a str.
	 * @param str nuovo valore della label di stato.
	 */
	private void setStatus(String str){
		this.lbl_status.setText(str);
	}
	
	/**
	 * Invia un Message ad un altro UserAgent dest.
	 * @param msg Messaggio da inviare.
	 * @param dest Nome del destinatario.
	 * @throws IOException se c'e' un errore nella comunicazione
	 */
	private void udp_send(Message msg,String dest) throws IOException{
		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		ObjectOutputStream 		os=new ObjectOutputStream(bout);
		
		os.writeObject(msg);
		byte[] buffer=bout.toByteArray();
		
		User u=out_info.get(dest);
		DatagramPacket pkt=new DatagramPacket(buffer,buffer.length,u.getUdpAddress(),u.getUdpPort());
		udp_socket.send(pkt);
	}
	
	/**
	 * Gestisce l'attivazione del bottone "Invia" della GUI.
	 */
	private void btn_snd_handler(){
		try{
			String dest=ol_cnts.getSelectedValue();
			if(dest==null)
				JOptionPane.showMessageDialog(window,"Nessun destinatario selezionato. Selezionare un contatto nella lista dei contatti online.");
			else{
				udp_send(new Message(myself.getNickname(),tf_snd.getText()),dest);
				synchronized(ta_recv){
					ta_recv.append("TU --> ");
					ta_recv.append(this.tf_snd.getText());
					ta_recv.append("\n");
				}
				tf_snd.setText("");
			}
		}
		catch(Exception exc){
			err.println("Errore invio messaggio UDP: "+exc.getMessage());
			exc.printStackTrace();
		}
	}
	
	/**
	 * Gestisce l'attivazione del bottone "Registrati"
	 */
	private void btn_reg_handler(){
		setStatus("Registrazione al server...");
		try{
			myself=new User(tf_nickname.getText());
			stub=new RmiClient(this);
			init_udp_connection();
			init_tcp_connection();
			rmi_server.register(myself);
			out.println(myself);
			
			new UdpListener(udp_socket,ta_recv).start();
			
			setGUImode(Const.AFTER_REG);
		}
		catch(NicknameTakenException exc){
			JOptionPane.showMessageDialog(window,"Nickname già in uso da un altro client. Utilizzare un altro nickname.");
			this.finalize();
			myself=null;
		}
		catch(Exception exc){
			err.println("Errore in registrazione client: "+exc.getMessage());
		}
		setStatus("Pronto.");
	}
	
	/**
	 * Gestisce l'attivazione del bottone "Online/Offline".
	 */
	private void btn_log_handler(){
		if(btn_log.getText().equals("Passa Online")){
			try{
				myself=rmi_server.login(myself,stub);
		
				refresh_jlist(in_cnts,myself.getInContacts(),true,false);
				tcp_send(new StatusMessage(myself.getNickname(),Const.ONLINE));
				setGUImode(Const.ONLINE);
				myself.setOnline(true);
				
			}
			catch(Exception exc){
				err.println("Errore login: "+exc.getMessage());
				exc.printStackTrace();
			}
		}
		else if(btn_log.getText().equals("Passa Offline")){
			try{
				myself=rmi_server.logout(myself);
				
				refresh_jlist(in_cnts,myself.getInContacts(),true,false);
				refresh_jlist(out_cnts,myself.getOutContacts(),true,false);
				refresh_jlist(ol_cnts,myself.getOutContacts(),false,true);
				tcp_send(new StatusMessage(myself.getNickname(),Const.OFFLINE));
				setGUImode(Const.OFFLINE);
				myself.setOnline(false);
			}
			catch(Exception exc){
				err.println("Errore logout: "+exc.getMessage());
			}
		}
	}
	
	/**
	 * Aggiorna l prelevando gli elementi da map.
	 * @param l JList da aggiornare.
	 * @param map Mappa dei contatti.
	 * @param keepselected Se true: ricorda e mantiene la precedente 
	 * selezione degli elementi nella lista aggiornata. 
	 * @param extractselected Se true: Estrae da map solamente i contatti selezionati.
	 */
	public void refresh_jlist(JList<String>l,ConcurrentHashMap<String,Contact>map,boolean keepselected,boolean extractselected){		
		Vector<String> vs		=new Vector<String>();
		Vector<Integer> indexes	=new Vector<Integer>(); 
		Enumeration<String> enu=map.keys();
		
		int i=0;
		if(extractselected)
			while(enu.hasMoreElements()){
				String str=enu.nextElement();
				if(map.get(str).isSelected()){
					vs.addElement(str);
					indexes.add(i);
				}
				i++;
			}
		else
			while(enu.hasMoreElements()){
				String str=enu.nextElement();
				vs.addElement(str);
				if(map.get(str).isSelected())
					indexes.add(i);
				i++;
			}
		
		synchronized(l){
			l.setListData(vs);
		
			if(keepselected)
				for(int j:indexes)
					l.getSelectionModel().addSelectionInterval(j,j);
		
			l.revalidate();
			l.repaint();
		}
	}
	
	/**
	 * Ritorna true se lo user corrente è online.
	 * @return stato dello user corrente.
	 */
	public boolean isOnline(){
		return myself.isOnline();
	}

	/**
	 * Gestisce i cambiamento di stato della lista dei contatti in ingresso.
	 * @param selected true se il contatto cliccato è stato selezionato.
	 * @param nickname contatto selezionato.
	 */
	private void in_cnts_handler(boolean selected,String nickname){
		try{	
			if(selected)
				myself=rmi_server.addInContact(myself,nickname);	
			else
				myself=rmi_server.remInContact(myself,nickname);
			refresh_jlist(in_cnts,myself.getInContacts(),true,false);
		}catch(Exception exc){
			err.println("Errore invocazione metodo remoto: "+exc.getMessage());
			//exc.printStackTrace();
		}
	}
	
	/**
	 * Gestisce i cambiamento di stato della lista dei contatti in uscita.
	 * @param selected true se il contatto cliccato è stato selezionato.
	 * @param nickname contatto selezionato.
	 */
	private void out_cnts_handler(boolean selected,String nickname){
		try{
			User ret;
			if(selected){
				myself.getOutContacts().get(nickname);
				ret=rmi_server.addOutContact(myself,nickname);
				myself.getOutContacts().get(nickname).setSelected(true);
				out_info.put(ret.getNickname(),ret);
			}
			else{
				myself=rmi_server.remOutContact(myself,nickname);
				out_info.remove(nickname);
			}
			refresh_jlist(out_cnts,myself.getOutContacts(),true,false);
			refresh_jlist(ol_cnts,myself.getOutContacts(),false,true);
		}
		catch(Exception exc){
			err.println("Errore invocazione metodo remoto: "+exc.getMessage());
			//exc.printStackTrace();
		}
	}
	
	
	/**
	 * Ritorna lo User a cui è associato lo UserAgent.
	 * @return user associato
	 */
	public User getUser(){
		return this.myself;
	}
	
	/**
	 * Imposta lo user associato a questo UserAgent. 
	 * @param u user associato
	 */
	public void setUser(User u){
		this.myself=u;
	}
	
	/**
	 * Invia uno StatusMessage al server per notificare un cambiamento di stato.
	 * @param msg messaggio di aggiornamento.
	 */
	private void tcp_send(StatusMessage msg){
		try {
			ObjectOutputStream oos=new ObjectOutputStream(tcp_socket.getOutputStream());
			oos.writeObject(msg);
			oos.flush();
		}
		catch (IOException exc) {
			err.println("Errore invio messaggio TCP: "+exc.getMessage());
			exc.printStackTrace();
		}
	}
	
	@Override
	public void finalize(){
		setStatus("Chiusura connessioni...");
		try {
			myself=rmi_server.logout(myself);
			udp_socket.close();
			tcp_send(new StatusMessage(myself.getNickname(),Const.CLOSED));
			tcp_socket.close();
		}
		catch(IOException exc){
			err.println(exc.getMessage());
		}
	}
	
	/**
	 * Ritorna la lista dei contatti in ingresso dello user.
	 * @return lista contatti in ingresso
	 */
	public JList<String> getInList(){
		return this.in_cnts;
	}
	
	/**
	 * Ritorna la lista dei contatti in uscita dello user.
	 * @return lista contatti in uscita
	 */
	public JList<String> getOutList(){
		return this.out_cnts;
	}
	
	/**
	 * Ritorna la lista dei contatti online dello user.
	 * @return lista contatti online
	 */
	public JList<String> getOlList(){
		return this.ol_cnts;
	}

	/**
	 * Inizializza la GUI
	 */
	public void run(){
		buildGUI();
	}
	
	/**
	 * Imposta il tipo di chiusura della finestra, in base
	 * al numero di istanze ancora presenti sulla JVM locale. 
	 */
	public void windowClosing(WindowEvent e) {
		if(nistances==1)
			((JFrame)e.getSource()).setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		else
			((JFrame)e.getSource()).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		nistances--;
		
		try{
			if(btn_log.getText().equals("Passa Offline")){
				((JFrame)e.getSource()).setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				JOptionPane.showMessageDialog(window,"Impossibile chiudere l'applicazione mentre si è ancora online.");
				nistances++;
			}
		}
		catch(Exception exc){}
	}
	

	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e){}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}	
	@Override
	public void mouseEntered(MouseEvent e){}
	@Override
	public void mouseExited(MouseEvent e){}
	@Override
	public void mousePressed(MouseEvent e){}
	@Override
	public void mouseReleased(MouseEvent e){}
}