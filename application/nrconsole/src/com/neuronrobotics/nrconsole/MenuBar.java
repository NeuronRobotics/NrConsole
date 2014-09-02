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
import com.neuronrobotics.nrconsole.plugin.DyIO.GettingStartedPanel;
import com.neuronrobotics.nrconsole.plugin.kinematics.NRConsoleKinematicsLabPlugin;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.IConnectionEventListener;
import com.neuronrobotics.sdk.util.ThreadUtil;

public class MenuBar extends JMenuBar implements IConnectionEventListener {
	private static final long serialVersionUID = 1L;
	
	private JMenuItem quitMenuItem = new JMenuItem("Quit");
	
	private JMenuItem disconnectMenuItem = new JMenuItem("Disconnect");
	private JMenuItem connectionMenuItem = new JMenuItem("Set Connection");
	private JMenuItem virtualPidMenuItem = new JMenuItem("Virtual PID");
	private JMenuItem aboutMenuItem = new JMenuItem("About NRConsole");
	private JMenuItem kinematicsLabMenuItem = new JMenuItem("Kinematics Lab");
	
	private PluginManager manager;
	
	private boolean ready=false;
	
	JMenu fileMenu;
	JMenu aboutMenu;
	JMenu connectionMenu;
	JMenu advanced;
	
	private JFrame aboutFrame;
	private JPanel aboutPanel; 
	
	public MenuBar(PluginManager console) {
			
		this.manager = console;//Add the PluginManager
		
		aboutPanel = new JPanel(new MigLayout()); //Create the about panel
		
		initJMenuItems();//Initialize the JMenuItems
		
		
		fileMenu = new JMenu("File");
		fileMenu.add(quitMenuItem);
		
		
		connectionMenu= new JMenu("Connection");
		connectionMenu.add(connectionMenuItem);
		connectionMenu.add(disconnectMenuItem);
		connectionMenu.add(virtualPidMenuItem);
		
		aboutMenu = new JMenu("About");
		aboutMenu.add(aboutMenuItem);
		
		advanced = new JMenu("Advanced");
		advanced.add(kinematicsLabMenuItem);
		
	    add(fileMenu);
	    add(connectionMenu);
	    add(aboutMenu);
	    //add(advanced);
	    aboutPanel.add(new JLabel(NRConsoleWindow.getConsoleVersion()),"wrap");
	    //about.add(new JLabel("Build date: "+SDKBuildInfo.getBuildDate()),"wrap");
	}
	
	public void setMenues(ArrayList<JMenu> menues){
		removeAll();
		add(fileMenu);
	    add(connectionMenu);
	    add(aboutMenu);
	    //add(advanced);
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
	
	/**
	 * Set up the JMenuItems
	 * 		"quitMenuItem","aboutMenuItem","disconnectMenuItem",
	 *  	"virtualPidMenuItem", "connectionMenuItem",
	 * 		 "kinematicsLabMenuItem"
	 *  ...by creating and adding the appropriate action listeners and setting mnemonics
	 *  (note that kinematicsLabMenuItem, virtualPidMenuItem, and aboutMenuItem do NOT have mnemonics).
	 *  
	 *  Also disables the disconnectMenuItem by default.
	 *  
	 *  Requires:
	 *  	-> that the "aboutPanel" JPanel be instantiated.
	 *  	-> the PluginManager "manager" be instantiated/set.
	 *  
	 */
	private void initJMenuItems() {
		
		//Set up quitMenuItem
		quitMenuItem.setMnemonic(KeyEvent.VK_Q);//Set the mnemonic to the "q" key (in standard US keyboard layout)
		quitMenuItem.addActionListener(new ActionListener() { 
			
			public void actionPerformed(ActionEvent e) {
				disconnect();
				System.exit(0);
			}
		});
		
		//Set up aboutMenuItem
		aboutMenuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				//Try to open the specified URL 
				//(using the Desktop variable of the GettingStartedPanel)
				//If it fails, will generate, pack and show the aboutFrame containing the aboutPanel.
				//TODO: give some sort of error message or something in the empty aboutFrame.
				try {
					GettingStartedPanel.openPage("http://wiki.neuronrobotics.com/NR_Console_Intro");
				} catch (Exception e1) {
					aboutFrame = new JFrame(aboutPanel.getName());
					aboutFrame.add(aboutPanel);
					aboutFrame.setLocationRelativeTo(null); 
					aboutFrame.pack();
					aboutFrame.setVisible(true);
				}
			}
		});
		
		//Set up disconnectMenuItem
		disconnectMenuItem.setMnemonic(KeyEvent.VK_D);//Set the mnemonic to the "d" key (in standard US keyboard layout)
		disconnectMenuItem.setEnabled(false);//Set the disconnectMenuItem to be disabled (grayed out) by default
		disconnectMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect();	
			}
		});
		
		//Set up virtualPidMenuItem
		virtualPidMenuItem.addActionListener(new ActionListener() {
			
			//On activation, will use the PluginManager "manager" to "connect" to a virtual PID device.
			//Will also signal that a plugin has been updated and set the "ready" variable to true
			@Override
			public void actionPerformed(ActionEvent e) {
				disconnect();
				manager.connectVirtualPID();
				manager.firePluginUpdate();
				ready = true;
			}
		});
		
		//Set up connectionMenuItem
		connectionMenuItem.setMnemonic(KeyEvent.VK_C);//Set the mnemonic to the "c" key (in standard US keyboard layout)
		connectionMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		
		//Set up kinematicsLabMenuItem
		kinematicsLabMenuItem.addActionListener(new ActionListener() {
			
			//On activation, will create a new NRConsoleKinematicsLabPlugin and notify the PluginManager "manager"
			//that a plugin has been updated
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				new NRConsoleKinematicsLabPlugin(manager);
				manager.firePluginUpdate();
			}
		});
		
		//All JMenuItems set up. We are done.
	}
	
	public void connect(){
		disconnect();
		try {
			connectionMenu.setEnabled(false);
			if(manager.connect(this)) {
				ready = true;
				connectionMenu.setEnabled(true);
				
			}else {
				disconnect();
				onDisconnect(manager.getConnection());
			}
		}catch(Exception ex){
			disconnect();
			onDisconnect(manager.getConnection());
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
	public void onDisconnect(BowlerAbstractConnection source) {
		disconnectMenuItem.setEnabled(false);
		connectionMenu.setEnabled(true);
		setMenues(null);
		ready = false;	
	}
	@Override
	public void onConnect(BowlerAbstractConnection source) {
		disconnectMenuItem.setEnabled(true);
		connectionMenu.setEnabled(true);
	}
}
