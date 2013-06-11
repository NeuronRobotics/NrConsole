package com.neuronrobotics.nrconsole.plugin.BowlerRPCDisplay;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;


public class RpcCommandPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9199252749669892888L;
	private GenericDevice device;
	private RpcEncapsulation rpc;
	private DefaultMutableTreeNode rpcDhild;
	private boolean commandsEnabled=false;

	public RpcCommandPanel(RpcEncapsulation rpc,GenericDevice device, DefaultMutableTreeNode rpcDhild){
		this.setRpcDhild(rpcDhild);
		this.setRpc(rpc);
		this.setDevice(device);
		setLayout(new MigLayout());
		add(new JLabel("Namespace"), "cell 0 0,alignx leading");
		add(new JLabel(rpc.getNamespace().split(";")[0]), "cell 1 0,alignx leading");
		
		add(new JLabel("Method"), "cell 0 1,alignx leading");
		add(new JLabel(rpc.getDownstreamMethod().toString()), "cell 1 1,alignx leading");
		
		add(new JLabel("RPC"), "cell 0 2,alignx leading");
		add(new JLabel(rpc.getRpc()), "cell 1 2,alignx leading");
		
		
	}

	public RpcEncapsulation getRpc() {
		return rpc;
	}

	private void setRpc(RpcEncapsulation rpc) {
		this.rpc = rpc;
	}

	public GenericDevice getDevice() {
		return device;
	}

	private void setDevice(GenericDevice device) {
		this.device = device;
	}

	public DefaultMutableTreeNode getRpcDhild() {
		return rpcDhild;
	}

	public void setRpcDhild(DefaultMutableTreeNode rpcDhild) {
		this.rpcDhild = rpcDhild;
	}

	public void enableCommands() {
		if(commandsEnabled)
			return;
		
		commandsEnabled = true;	
	}

}
