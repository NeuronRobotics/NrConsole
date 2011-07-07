package com.neuronrobotics.nrconsole;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.IConnectionEventListener;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class MenuBar extends JMenuBar implements IConnectionEventListener {
	private static final long serialVersionUID = 1L;
	
	private JMenuItem quitMenuItem = new JMenuItem("Quit");
	
	private JMenuItem disconnectMenuItem = new JMenuItem("Disconnect");
	private JMenuItem connectionMenuItem = new JMenuItem("Set Connection");
	private JMenuItem aboutMenuItem = new JMenuItem("About NRConsole");
	private PluginManager manager;
	private boolean ready=false;
	JMenu fileMenu;
	JMenu aboutMenu;
	JMenu connectionMenu;
	private JFrame aboutFrame;
	private JPanel about = new JPanel(new MigLayout());
	public MenuBar(PluginManager console) {
		this.manager = console;
		
		initMenus();
		
		fileMenu = new JMenu("File");
		fileMenu.add(quitMenuItem);
		
		
		connectionMenu= new JMenu("Connection");
		connectionMenu.add(connectionMenuItem);
		connectionMenu.add(disconnectMenuItem);
		
		aboutMenu = new JMenu("About");
		aboutMenu.add(aboutMenuItem);
		
	    add(fileMenu);
	    add(connectionMenu);
	    add(aboutMenu);
	    about.add(new JLabel(NRConsoleWindow.getConsoleVersion()),"wrap");
	    //about.add(new JLabel("Build date: "+SDKBuildInfo.getBuildDate()),"wrap");
	}
	public void setMenues(ArrayList<JMenu> menues){
		removeAll();
		add(fileMenu);
	    add(connectionMenu);
	    add(aboutMenu);
	    if(menues != null) {
		    for(JMenu m:menues){
		    	if (m != null)
		    		add(m);
		    }
	    }
	}

	public void addActionListener(ActionListener l) {
		quitMenuItem.addActionListener(l);
		disconnectMenuItem.addActionListener(l);
		connectionMenuItem.addActionListener(l);
		aboutMenuItem.addActionListener(l);	
	}
	
	private void initMenus() {
		
		quitMenuItem.setMnemonic(KeyEvent.VK_Q);
		quitMenuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				disconnect();
				System.exit(0);
			}
		});
		
		aboutMenuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				aboutFrame = new JFrame(about.getName());
				aboutFrame.add(about);
				aboutFrame.setLocationRelativeTo(null); 
				aboutFrame.pack();
				aboutFrame.setVisible(true);
			}
		});
		
		disconnectMenuItem.setMnemonic(KeyEvent.VK_D);
		disconnectMenuItem.setEnabled(false);
		disconnectMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect();	
			}
		});
		
		//connectionMenuItem.setAction(new ConnectionAction());
		connectionMenuItem.setMnemonic(KeyEvent.VK_C);
		connectionMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
	}
	
	public void connect(){
		disconnect();
		try {
			connectionMenu.setEnabled(false);
			manager.connect(this);
			ready = true;
			connectionMenu.setEnabled(true);
		}catch(Exception ex){
			disconnect();
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to connect to device, "+ex.getMessage(), "Bowler ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void disconnect() {
		try {
			manager.disconnect();
		}catch(Exception ex) {
			System.err.println("NRCONSOLE disconnection error print:");
			ex.printStackTrace();
		}
		ThreadUtil.wait(75);
	}
	public boolean isReady(){
		return ready ;
	}
	
	@Override
	public void onDisconnect() {
		disconnectMenuItem.setEnabled(false);
		connectionMenu.setEnabled(true);
		setMenues(null);
		ready = false;	
	}
	@Override
	public void onConnect() {
		disconnectMenuItem.setEnabled(true);
		connectionMenu.setEnabled(true);
	}
}
