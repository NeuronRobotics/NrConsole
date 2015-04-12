package com.neuronrobotics.graphing;

import javafx.application.Platform;

import org.jfree.data.xy.XYSeries;

public class DataChannel {
	private String title;
	private XYSeries series;
	private static long startTime = System.currentTimeMillis();
	
	public DataChannel(String title) {
		this.title = title;
		series = new XYSeries(toString());
	}
	
	public String toString() {
		return title;
	}
	
	public void graphValue(double value) {
		try{
			Platform.runLater(()-> {
				long time = System.currentTimeMillis() - startTime ;
				if(series != null)
					series.add((double) time/1000, value);
				while(series.getItemCount()>3000){
					Platform.runLater(()-> {
						series.remove(0);
					});
				}
			});
		}catch(IllegalStateException ex){
			//application not yet loaded
		}
	}
	
	public XYSeries getSeries() {
		return series;
	}

	public static void restart() {
		startTime = System.currentTimeMillis();
	}
	
	public void clear() {
		series.clear();
	}
}
