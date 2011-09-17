package com.neuronrobotics.nrconsole.plugin.DyIO.channelwidgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import com.neuronrobotics.nrconsole.plugin.DyIO.ChannelManager;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.peripherals.DigitalInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;
import com.neuronrobotics.sdk.dyio.peripherals.IDigitalInputListener;

public class DigitalInputWidget extends ControlWidget implements IDigitalInputListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton button = new JButton();
	private JButton refresh = new JButton("Refresh");
	private JCheckBox async = new JCheckBox("Async");
	
	private DigitalInputChannel dic;
	
	public DigitalInputWidget(ChannelManager c) {
		super(c);
		Log.debug("BEGIN Setting up: "+this.getClass());
		setRecordable(true);
		
		dic = new DigitalInputChannel(getChannel(),true);
		
		
		button.setEnabled(false);

		add(button);
		add(refresh);
		//add(async);
		setValue(true);
		dic.addDigitalInputListener(this);
		refresh.addActionListener(this);
		async.addActionListener(this);
		Log.debug("END Setting up: "+this.getClass());
		
	}

	private void setValue(boolean value) {
		if(value) {
			button.setText("High");
			recordValue(255);
		} else {
			button.setText("Low");
			recordValue(0);
		}
	}

	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == refresh) {
			pollValue();
		}else if(e.getSource() == async) {
			if(!async.isSelected()) {
				dic.setAsync(false);
				dic.removeDigitalInputListener(this);
			} else {
				dic.setAsync(true);
				dic.addDigitalInputListener(this);
			}
		}
	}

	
	public void onDigitalValueChange(DigitalInputChannel source, boolean isHigh) {
		setValue(isHigh);
	}
	
	
	public void pollValue() {
		
		if(!async.isSelected())
			setValue(dic.isHigh());
		//throw new RuntimeException();
	}

	
	public DyIOAbstractPeripheral getPerpheral() {
		// TODO Auto-generated method stub
		return dic;
	}
}
