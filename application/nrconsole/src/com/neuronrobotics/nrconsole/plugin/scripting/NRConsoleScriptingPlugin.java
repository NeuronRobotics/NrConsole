package com.neuronrobotics.nrconsole.plugin.scripting;

import java.awt.Dimension;
import java.io.PrintStream;
import java.util.ArrayList;

import javafx.application.Platform;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIORegestry;



public class NRConsoleScriptingPlugin extends AbstractNRConsoleTabedPanelPlugin {

	private static final String[] myNamespaces = new String[]{"neuronrobotics.dyio.*"};
	
	GistTabbedBrowser se =null;

	private PluginManager pm;

	private JPanel ret;

	
	public NRConsoleScriptingPlugin( PluginManager pm) {
		super(myNamespaces, pm);
		this.pm = pm;
		
	}
	
	public ArrayList<JMenu> getMenueItems() {

			JMenu collectionMenu = new JMenu("Script");
			JMenuItem open = new JMenuItem("Open");
			open.addActionListener(e -> {
				//open();
			});
			collectionMenu.add(open);
			
			JMenuItem saveas = new JMenuItem("Save As");
			saveas.addActionListener(e -> {
				//updateFile();
				//save();
			});
			collectionMenu.add(saveas);
			
			JMenuItem save = new JMenuItem("Save");
			save.addActionListener(e -> {
				//save();
			});
			collectionMenu.add(save);
			
//			nativeIdisplay = new JMenuItem("Switch to "+interfaceType.Native);
//			webgist = new JMenuItem("Switch to "+interfaceType.WebGist);
//			
//			nativeIdisplay.addActionListener(e -> {
//				nativeIdisplay.setEnabled(false);
//				webgist.setEnabled(true);
//				toDisplay=interfaceType.Native;
//				removeAll();
//				SwingUtilities.invokeLater(() -> {
//					try {
//						loadCodeFromCurrentGist();
//					} catch (Exception e1) {
//						e1.printStackTrace();
//					}
//				});
//				
//				add(codeScroll,"wrap");
//				add(controls,"wrap");
//				add(outputPane,"wrap");
//				invalidate();
//				pm.getFrame().invalidate();
//			});
//			webgist.addActionListener(e -> {
//				nativeIdisplay.setEnabled(true);
//				webgist.setEnabled(false);
//				toDisplay=interfaceType.WebGist;
	//
//				SwingUtilities.invokeLater(() -> {
//					removeAll();
//					add(browser,"wrap");
//					add(controls,"wrap");
//					add(outputPane,"wrap");
//					updateFile();
//					save();
//					SwingUtilities.invokeLater(() -> {	
//						invalidate();
//						pm.getFrame().invalidate();
//					});
//					
//				});
	//
//			});
//			
//			collectionMenu.add(nativeIdisplay);
//			collectionMenu.add(webgist);
//			webgist.setEnabled(false);
			ArrayList<JMenu> m = new ArrayList<JMenu>();
			m.add(collectionMenu);
			return m;
	}

	@Override
	public JPanel getTabPane() {
		if(ret == null){
			se=new GistTabbedBrowser(DyIORegestry.get(),pm);
			ret = new JPanel(new MigLayout());
			ret.setName("Groovy Scripting");
			ret.add(se);
			pm.getFrame().addComponentListener(new java.awt.event.ComponentAdapter() {
	            public void componentResized(java.awt.event.ComponentEvent e) {
	        		//Preferred Size of TabPane.
	            	SwingUtilities.invokeLater(()-> {
	            		ret.setSize(pm.getFrame().getWidth(), pm.getFrame().getHeight());
	            		se.setSize(pm.getFrame().getWidth(), pm.getFrame().getHeight());
	            	});
	            }
	        });	
		}
		return ret;
	}

	@Override
	public boolean setConnection(BowlerAbstractConnection connection) {
		if(!DyIORegestry.get().isAvailable()){
			DyIORegestry.setConnection(connection);
			DyIORegestry.get().connect();
			return DyIORegestry.get().isAvailable();
		}
		return true;
	}

	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return new Dimension(1050,1200);
	}

}


