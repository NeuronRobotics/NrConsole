package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class ServoOutputScheduleChannelUI extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7112414698561768276L;
	private ServoOutputScheduleChannel channel;
	public ServoOutputScheduleChannelUI(ServoOutputScheduleChannel chan){
		setChannel(chan);
		setLayout(new MigLayout());
		setBorder(BorderFactory.createLoweredBevelBorder());
		add(new JLabel("Output Channel: "+getChannel().getChannelNumber()));
	}
	public int getChannelNumber() {
		// TODO Auto-generated method stub
		return getChannel().getChannelNumber();
	}
	public void setChannel(ServoOutputScheduleChannel channel) {
		this.channel = channel;
	}
	public ServoOutputScheduleChannel getChannel() {
		return channel;
	}
	
}
