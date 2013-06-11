package com.neuronrobotics.nrconsole.plugin.BowlerRPCDisplay;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;

public class BowlerRPCDisplay extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4324863910313012663L;
	
	private GenericDevice dev;
	private JTree display;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Bowler Namespaces");
	
 	public BowlerRPCDisplay(){
		setName("Bowler RPC");
		
	}

	public boolean setConnection(BowlerAbstractConnection connection) {
		dev = new GenericDevice(connection);
		// populate list

		 ArrayList<String>namespaces = dev.getNamespaces();
		
		for(String s:namespaces){
			String [] split = s.split(";");
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(split[0]);
			
			ArrayList<String> rpcSet = dev.getRpcList(s);
			for(String r:rpcSet){
				DefaultMutableTreeNode rpcDhild = new DefaultMutableTreeNode(r);
				child.add(rpcDhild);
			}
			
			root.add(child);
		}
		
		display= new JTree(root);
		add(display);
		return dev.connect();
	}
}
