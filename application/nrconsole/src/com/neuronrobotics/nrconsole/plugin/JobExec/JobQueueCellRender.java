package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class JobQueueCellRender extends DefaultListCellRenderer {
private ArrayList<PrintObject> objects;


	@Override
	public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		
		if (value.getClass() == PrintObject.class){//If we know this is a printObject Type, lets do the special render
			PrintObject obj = (PrintObject) value;
			
			
			if (isSelected){
				switch (obj.getPrintStatus()) {
				case FAIL:
					setBackground(Color.RED);
					break;
				case PROBLEM:
					setBackground(Color.ORANGE);
					break;
				case GOOD:
					setBackground(Color.GREEN);
					break;
				default:
					break;
				}
				setForeground(Color.BLACK);
				setText("> " + obj.toString());
			}
			else{
				switch (obj.getPrintStatus()) {
				case FAIL:
					setBackground(Color.decode("#ff8080"));
					break;
				case PROBLEM:
					setBackground(Color.decode("#ffe380"));
					break;
				case GOOD:
					setBackground(Color.decode("#80ff80"));
					break;
				default:
					break;
				}
				
				setForeground(Color.BLACK);
				setText(obj.toString());
			}
		}
		else{
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
			setText(value.toString());
		}
		
		
		
		return this;
	}

}
