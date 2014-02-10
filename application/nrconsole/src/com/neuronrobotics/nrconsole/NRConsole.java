package com.neuronrobotics.nrconsole;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.neuronrobotics.nrconsole.plugin.IPluginUpdateListener;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.ui.ConnectionImageIconFactory;
import com.neuronrobotics.sdk.util.ThreadUtil;
@SuppressWarnings("unused")
public class NRConsole implements ActionListener {
	private NRConsoleWindow nrcFrame = null;

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
		nrcFrame = new NRConsoleWindow();
		nrcFrame.setJMenuBar(nrcMenubar);
		nrcMenubar.setMenues(null);
		nrcMenubar.addActionListener(this);
		
		nrcFrame.setLocationRelativeTo(null);
		nrcFrame.setVisible(true);
		nrcFrame.setIconImage( ConnectionImageIconFactory.getIcon("images/hat.png").getImage()); 
		
		shower.start();
		if(debug){
			Log.enableInfoPrint();
		}
		
//		while(!nrcFrame.isShowing()){
//			ThreadUtil.wait(100);
//		}
//		
//		while(nrcFrame.isShowing()){
//			ThreadUtil.wait(500);
//		}
//		manager.disconnect();
//		System.out.println("Exit clean");
//		System.exit(0);
		nrcFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent winEvt) {
				manager.disconnect();
				System.out.println("Exit clean");
		        System.exit(0); 
		    }
		});
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
					//nrcFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);  
					while(nrcMenubar.isReady()){
						ThreadUtil.wait(50);
					}
					//System.out.println("Exiting Application");
				}else{
					manager.removeIPluginUpdateListener(this);
					nrcMenubar.setMenues(null);
					nrcFrame.displayLogo(manager);
					nrcFrame.invalidate();
					nrcFrame.setVisible(true);
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
			//System.out.println("NRConsole is refreshing");
			if(nrcMenubar.isReady()){
				nrcMenubar.setMenues(manager.getMenueItems());
				nrcFrame.setDeviceManager(manager);
				nrcFrame.invalidate();
				nrcFrame.setVisible(true);
			}
		}
	}
}
