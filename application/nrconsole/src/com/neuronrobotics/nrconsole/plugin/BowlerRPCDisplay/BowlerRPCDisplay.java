package com.neuronrobotics.nrconsole.plugin.BowlerRPCDisplay;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.common.BowlerAbstractConnection;
import com.neuronrobotics.sdk.common.BowlerMethod;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;

public class BowlerRPCDisplay extends JPanel implements TreeSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4324863910313012663L;
	
	private GenericDevice dev;
	private JTree display;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Bowler Namespaces");
	private ArrayList< RpcCommandPanel> commandPanels = new ArrayList< RpcCommandPanel>();
	private JPanel controlPanel = new JPanel();
	
 	public BowlerRPCDisplay(){
		setName("Bowler RPC");
		setLayout(new MigLayout());
		
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
				RpcCommandPanel control = new RpcCommandPanel(r, dev,rpcDhild);
				
				if(r.getDownstreamMethod() == BowlerMethod.GET){
					if(getTree == null)
						getTree = new DefaultMutableTreeNode("GET");
					getTree.add(rpcDhild);
				}
				if(r.getDownstreamMethod() == BowlerMethod.POST){
					if(postTree == null)
						postTree = new DefaultMutableTreeNode("POST");
					postTree.add(rpcDhild);
				}
				if(r.getDownstreamMethod() == BowlerMethod.CRITICAL){
					if(critTree == null)
						critTree = new DefaultMutableTreeNode("CRITICAL");
					critTree.add(rpcDhild);
				}
				if(r.getDownstreamMethod() == BowlerMethod.ASYNCHRONOUS){
					if(asynTree == null)
						asynTree = new DefaultMutableTreeNode("ASYNCHRONOUS");
					asynTree.add(rpcDhild);
				}
				
				
				commandPanels.add(control);
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
		display.addTreeSelectionListener(this);
		
		add(display,"cell 0 0,alignx leading");
		add(controlPanel,"cell 1 0,alignx trailing");
		return dev.connect();
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		
		for(RpcCommandPanel r:commandPanels){
			if(r.getRpcDhild() == display.getLastSelectedPathComponent()){
				controlPanel.removeAll();
				r.enableCommands();
				controlPanel.add(r);
				controlPanel.repaint();
			}
		}
	}
}
