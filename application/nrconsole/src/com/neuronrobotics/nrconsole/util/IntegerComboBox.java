package com.neuronrobotics.nrconsole.util;

import javax.swing.JComboBox;

public class IntegerComboBox extends JComboBox{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2439771311089831575L;

	public void removeInteger(int in){
		for(int i=0;i<getItemCount();i++){
			Integer selected = (Integer)( getItemAt(i));
			if(selected != null){
				if(selected.intValue() == in){
					removeItemAt(i);
					return;
				}
			}
		}
	}
	
	public void addInteger(int in){
		for(int i=0;i<getItemCount();i++){
			Integer selected = (Integer)( getItemAt(i));
			if(selected != null){
				if(selected.intValue() == in){
					return;
				}
			}
		}
		addItem(new Integer(in));
	}
	
	public void setSelectedInteger(int in){
		for(int i=0;i<getItemCount();i++){
			Integer selected = (Integer)( getItemAt(i));
			if(selected != null){
				if(selected.intValue() == in){
					setSelectedItem(getItemAt(i));
					return;
				}
			}
		}
		addInteger(in);
		setSelectedInteger(in);
	}
	
	public int getSelectedInteger(){
		return Integer.parseInt(getSelectedItem().toString());
	}
}
