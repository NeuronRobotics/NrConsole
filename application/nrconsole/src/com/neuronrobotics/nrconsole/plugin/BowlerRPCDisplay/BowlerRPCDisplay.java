package com.neuronrobotics.nrconsole.plugin.BowlerRPCDisplay;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
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
			DefaultMutableTreeNode namespaceTree = new DefaultMutableTreeNode(split[0]);
			DefaultMutableTreeNode getTree = null;
			DefaultMutableTreeNode postTree = null;
			DefaultMutableTreeNode critTree = null;
			DefaultMutableTreeNode asynTree = null;

			ArrayList<RpcEncapsulation> rpcSet = dev.getRpcList(s);
			
			for(RpcEncapsulation r:rpcSet){
				DefaultMutableTreeNode rpcDhild = new DefaultMutableTreeNode(r.getRpc());
				if(r.getMethod() == BowlerMethod.GET){
					if(getTree == null)
						getTree = new DefaultMutableTreeNode("GET");
					getTree.add(rpcDhild);
				}
				if(r.getMethod() == BowlerMethod.POST){
					if(postTree == null)
						postTree = new DefaultMutableTreeNode("POST");
					postTree.add(rpcDhild);
				}
				if(r.getMethod() == BowlerMethod.CRITICAL){
					if(critTree == null)
						critTree = new DefaultMutableTreeNode("CRITICAL");
					critTree.add(rpcDhild);
				}
				if(r.getMethod() == BowlerMethod.ASYNCHRONOUS){
					if(asynTree == null)
						asynTree = new DefaultMutableTreeNode("ASYNCHRONOUS");
					asynTree.add(rpcDhild);
				}
			}
			if(getTree!=null)
				namespaceTree.add(getTree);
			if(postTree!=null)
				namespaceTree.add(postTree);
			if(critTree!=null)
				namespaceTree.add(critTree);
			if(asynTree!=null)
				namespaceTree.add(asynTree);
			
			root.add(namespaceTree);
		}
		
		display= new JTree(root);
		add(display);
		return dev.connect();
	}
}
