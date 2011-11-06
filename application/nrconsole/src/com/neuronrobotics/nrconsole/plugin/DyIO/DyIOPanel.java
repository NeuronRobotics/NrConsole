package com.neuronrobotics.nrconsole.plugin.DyIO;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.neuronrobotics.sdk.common.MACAddress;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOPowerEvent;
import com.neuronrobotics.sdk.dyio.DyIOPowerState;
import com.neuronrobotics.sdk.dyio.DyIORegestry;



import net.miginfocom.swing.MigLayout;

public class DyIOPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ImageIcon image;
	private JLabel voltage = new JLabel("Battery Voltage");
	private bankLED A = new bankLED ();
	private bankLED B = new bankLED ();
	private JButton refresh = new JButton("Refresh");
	private JLabel mac = new JLabel("MAC: 00:00:00:00:00:00");
	private JLabel fw = new JLabel("FW Version: ?.?.?");
	public DyIOPanel() {
		try{
			image = new ImageIcon(DyIOPanel.class.getResource("images/dyio.png"));
		}catch (Exception e){
			e.printStackTrace();
			image = new ImageIcon(DyIOPanel.class.getResource("images/dyio.png"));
		}
	    initPanel();
	    setName("DyIO");

	}
	
	private void initPanel() {
		Dimension size = new Dimension(image.getIconWidth(), image.getIconHeight());
	    setSize(size);
	    setMaximumSize(size);
	    setMinimumSize(size);
	    setPreferredSize(size);
	    setLayout(new MigLayout());
	    refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DyIORegestry.get().getBatteryVoltage(true);
			}
		});
	    add(voltage, "pos 210 50");
	    add(refresh, "pos 385 40");
	    add(mac, "pos 200 150");
	    add(fw, "pos 200 175");
		int ledPos = 12*34+125;
		add(A, "pos 440 "+ledPos);
		add(B, "pos 105 "+ledPos);
		
		
	}
	private void setFw(byte[] f){
		fw.setText("FW Version: "+f[0]+"."+f[1]+"."+f[2]);
	}
	private void setMac(MACAddress m){
		mac.setText("MAC: "+m.toString());
	}
	
	public void addChannels(List<ChannelManager> list, boolean alignedLeft) {
		int index = 0;
		//removeAll();
		for(ChannelManager cp : list) {
			cp.getChannelPanel().setAlignedLeft(alignedLeft);
			int x = (alignedLeft ? 105 : 370);
			int y = ((alignedLeft ? 11 - (index % 12) : index % 12) * 34) + 120;
			
			JLabel channelLabel = new JLabel(new DecimalFormat("00").format(cp.getChannel().getChannelNumber()));
			channelLabel.setFont(new Font("Sans-Serif", Font.BOLD, 18));
			
			add(cp.getChannelPanel(), "pos " + x + " " + y);
			add(channelLabel, "pos " + ((alignedLeft ? - 25 : 82) + x) + " " + y);
			
			index++;
		}

	}
	private void setVoltage(double v){
		voltage.setText("Battery Voltage = "+new DecimalFormat("00.00").format(v));
	}

	@Override
	public void paintComponent (Graphics g) {
    	super.paintComponent(g);
    	try {
    		g.drawImage(image.getImage(), 0,0,this.getWidth(),this.getHeight(),this);
    	} catch (Exception e) {
    		
    	}
    }
	public void setPowerEvent(DyIOPowerEvent dyIOPowerEvent) {
		setVoltage(dyIOPowerEvent.getVoltage());
		A.setState(dyIOPowerEvent.getChannelAMode());
		B.setState(dyIOPowerEvent.getChannelBMode());
	    setMac(DyIORegestry.get().getAddress());
	    setFw(DyIORegestry.get().getFirmwareRev());
		repaint();
	}
	private class bankLED extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3204367369543884223L;
		private DyIOPowerState state = DyIOPowerState.BATTERY_UNPOWERED;
		public void setState(DyIOPowerState s){
			state = s;
		}
		@Override
		public void paintComponent (Graphics g) {
	    	super.paintComponent(g);
	    	try {
	    		switch(state){
	    		case BATTERY_POWERED:
	    			g.setColor(Color.red);
	    			break;
	    		case BATTERY_UNPOWERED:
	    			g.setColor(Color.orange);
	    			break;
	    		case REGULATED:
	    			g.setColor(Color.green);
	    			break;
	    		}
	    		Graphics2D g2 = (Graphics2D)g;
	    		g2.fillRect(0, 0, 50, 50);
	    	} catch (Exception e) {
	    		
	    	}
	    }
	}

}
