package com.neuronrobotics.nrconsole.plugin.BowlerRPCDisplay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.sdk.common.BowlerDatagram;
import com.neuronrobotics.sdk.common.RpcEncapsulation;
import com.neuronrobotics.sdk.genericdevice.GenericDevice;


public class RpcCommandPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9199252749669892888L;
	private GenericDevice device;
	private RpcEncapsulation rpc;
	private DefaultMutableTreeNode rpcDhild;
	private boolean commandsEnabled=false;
	private ArrayList<JTextField> tx = new ArrayList<JTextField>();
	private ArrayList<JLabel> rx = new ArrayList<JLabel>();
	private JButton send = new JButton("Send");
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
		
		add(new JLabel("Tx>>"), "cell 0 3,alignx leading");
		add(new JLabel("Rx<<"), "cell 0 4,alignx leading");
		add(send,"cell 2 3,alignx leading");
		
		for (String s:rpc.getDownstreamArguments()){
			JTextField tmp= new JTextField(5);
			tmp.setText("0");
			tx.add(tmp);
		}
		for (String s:rpc.getUpstreamArguments()){
			JLabel tmp= new JLabel();
			tmp.setText("0");
			rx.add(tmp);
		}
		for(JTextField t:tx){
			add(t, "cell 1 3,alignx leading");
		}
		for(JLabel t:rx){
			add(t, "cell 1 4,alignx leading");
		}
		
		send.addActionListener(this);
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int[] values = new int[tx.size()];
		for (int i=0;i<values.length;i++){
			values[i] = Integer.parseInt(tx.get(i).getText());
		}
		BowlerDatagram bd =device.send(rpc.getCommand(values));
		int [] up = rpc.parseResponse(bd);
		for(int i=0;i<up.length;i++){
			rx.get(i).setText(new Integer(up[i]).toString());
		}
	}

}
