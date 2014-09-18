package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.util.ArrayList;

import com.jme3.scene.Geometry;

public class LayerGeoms extends ArrayList<Geometry>{
	private int layer;
	private int layerHeight;
	
	
	public int getLayer() {
		return layer;
	}
	public void setLayer(int _layer) {
		layer = _layer;
	}
	public int getLayerHeight() {
		return layerHeight;
	}
	public void setLayerHeight(int _layerHeight) {
		layerHeight = _layerHeight;
	}
}
