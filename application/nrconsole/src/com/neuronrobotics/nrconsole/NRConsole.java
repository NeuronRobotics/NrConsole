package com.neuronrobotics.nrconsole;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.neuronrobotics.nrconsole.plugin.IPluginUpdateListener;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.ThreadUtil;
@SuppressWarnings("unused")
public class NRConsole implements ActionListener {
	private NRConsoleWindow nrcWindow = null;

	private PluginManager manager=new PluginManager();
	private MenuBar nrcMenubar = new MenuBar(manager);
	private showManager shower = new showManager ();
	private static NRConsole self;
	public static void main(String [] args) {
		try {
			if(args.length != 0)
				new NRConsole(true);
			else
				new NRConsole(false);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void disconnect() {
		self.nrcMenubar.disconnect();
	}
	
	public NRConsole(boolean debug) {
		self = this;
		nrcWindow = new NRConsoleWindow();
		nrcWindow.setJMenuBar(nrcMenubar);
		nrcMenubar.setMenues(null);
		nrcMenubar.addActionListener(this);
		
		nrcWindow.setSize((manager.getMinimumWidth()+53),(manager.getMinimumHeight()+105));
		nrcWindow.setLocationRelativeTo(null);
		nrcWindow.setVisible(true);
		
		
		shower.start();
		if(debug)
			Log.enableDebugPrint(true);
		
		while(!nrcWindow.isShowing()){
			ThreadUtil.wait(100);
		}
		
		while(nrcWindow.isShowing()){
			ThreadUtil.wait(500);
		}
		manager.disconnect();
		System.out.println("Exit clean");
		System.exit(0);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("set-connection")) {
			//System.out.println("Do something with the aciton command.");
		}
	}
	private class showManager extends Thread implements IPluginUpdateListener{
		public void run(){
			try{
				nrcMenubar.connect();
			}catch (Exception ex){
				ex.printStackTrace();
			}
			while(true){	
				if(nrcMenubar.isReady()){
					
					onPluginListUpdate(manager);
					manager.addIPluginUpdateListener(this);
					//System.out.println("Starting application");
					while(nrcMenubar.isReady()){
						ThreadUtil.wait(50);
					}
					//System.out.println("Exiting Application");
				}else{
					manager.removeIPluginUpdateListener(this);
					nrcMenubar.setMenues(null);
					nrcWindow.displayLogo(manager);
					nrcWindow.invalidate();
					nrcWindow.setVisible(true);
					//System.out.println("Starting splash");
					while(!nrcMenubar.isReady()){
						ThreadUtil.wait(50);
					}
					//System.out.println("Exiting splash");
				}
			}
		}

		@Override
		public void onPluginListUpdate(PluginManager manager) {
			System.out.println("NRConsole is refreshing");
			if(nrcMenubar.isReady()){
				nrcMenubar.setMenues(manager.getMenueItems());
				nrcWindow.setDeviceManager(manager);
				nrcWindow.invalidate();
				nrcWindow.setVisible(true);
				
				
			}
		}
	}
}
