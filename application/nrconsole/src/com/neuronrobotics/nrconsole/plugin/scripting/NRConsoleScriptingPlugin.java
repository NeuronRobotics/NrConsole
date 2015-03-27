package com.neuronrobotics.nrconsole.plugin.scripting;

import java.awt.Dimension;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.dyio.DyIORegestry;



public class NRConsoleScriptingPlugin extends AbstractNRConsoleTabedPanelPlugin {

	private static final String[] myNamespaces = new String[]{"neuronrobotics.dyio.*"};
	
	GistTabbedBrowser se =null;

	private PluginManager pm;

	
	public NRConsoleScriptingPlugin( PluginManager pm) {
		super(myNamespaces, pm);
		this.pm = pm;
		
	}
	
	public ArrayList<JMenu> getMenueItems() {
		if(se!=null){
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
		return null;
	}

	@Override
	public JPanel getTabPane() {
		if(se == null){
			se=new GistTabbedBrowser(DyIORegestry.get(),pm);
		}
		JPanel ret =new JPanel(new MigLayout());
		ret.setName("Groovy Scripting");
		ret.add(se);
		return ret;
	}

	@Override
	public boolean setConnection(BowlerAbstractConnection connection) {
		if(!DyIORegestry.get().isAvailable()){
			return DyIORegestry.setConnection(connection);
		}
		return true;
	}

	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return new Dimension(1050,1200);
	}

}


