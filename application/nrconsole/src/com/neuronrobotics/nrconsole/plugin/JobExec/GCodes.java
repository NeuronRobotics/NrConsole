package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class GCodes extends ArrayList<GCodePosition>{
double filaDia = 3;
	
	
public double getFilaDia() {
	return filaDia;
}
public boolean add(double _x, double _y, double _z, double _e){
	return add(new GCodePosition(_x, _y, _z, _e));
}

public void setFilaDia(double _filaDia) {
	filaDia = _filaDia;
}

public void printOutput(){
	
	for (GCodePosition code : this) {
		System.out.println("Position #: " + indexOf(code) + " x: " + code.getX() + " y: " + code.getY() + " z: " + code.getZ() + " e: " + code.getE());
	}
	System.out.println("Number of Layers: " + numLayers());
	System.out.println("Total Extrusion Length: " + totalExtrusionLength());
}

public boolean isRapidMove(int _position1, int _position2){
	if (get(_position1).getE() == get(_position2).getE()){
		return true;
	}
	else
	{
		return false;
	}
}
public boolean isRapidMove(GCodePosition _pos1, GCodePosition _pos2){
	if (_pos1.getE() == _pos2.getE()){
		return true;
	}
	else
	{
		return false;
	}
}

public double getLayerHeight(int _layer){
	double prevZ = -1; //Initialize this to an impossible layer height to ensure counting of the first layer
	int layers = 0;
	for (GCodePosition code : this) {
		if (code.getZ() != -1){
			if (code.getZ() != prevZ){
				layers++;
				if (layers == _layer){
					return (code.getZ() - prevZ);
				}
				prevZ = code.getZ();
				
				}
			}
		}
		return 0;
}
public double getLayerHeight(GCodePosition _code){
	
	
	int inCode = indexOf(_code);
	double currZ = _code.getZ();
	double layerHeight = 0;
	while(layerHeight == 0 && inCode > -1){
		double prevZ = get(inCode).getZ();
		layerHeight = currZ - prevZ;
		if (layerHeight < 0){		//** Really Hack** This is for handling slicers which do not
			layerHeight = currZ;	//set the z height to zero before beginning the first layer
		}
		System.out.println("Current Z: " + currZ + " Previous Z: " + prevZ + " inCode: " + inCode);
		inCode--;
	}
	
		return layerHeight;
}
public int numLayers(){
	double prevZ = -1; //Initialize this to an impossible layer height to ensure counting of the first layer
	int layers = 0;
	for (GCodePosition code : this) {
		if (code.getZ() != -1){
			if (code.getZ() != prevZ){
				prevZ = code.getZ();
				layers++;
				}
			}
		}
		return layers;
	}
	

public double getMoveExtLength(int _position1, int _position2){
	double eLength = 0;
	
	double startE = get(_position1).getE();
	double endE = get(_position2).getE();
	
	eLength = endE = startE;
	return eLength;
	
}
public double getMoveExtLength(GCodePosition _pos1, GCodePosition _pos2){
	double eLength = 0;
	
	double startE = _pos1.getE();
	double endE = _pos2.getE();
	
	eLength = endE = startE;
	return eLength;
	
}

public double getMoveExtVolume(int _position1, int _position2){
	double eVol = 0;
	
	eVol = (Math.PI * (Math.pow((filaDia/2),2))) * getMoveExtLength(_position1, _position2);
	
	return eVol;
}
public double getMoveExtVolume(GCodePosition _pos1, GCodePosition _pos2){
	double eVol = 0;
	
	eVol = (Math.PI * (Math.pow((filaDia/2),2))) * getMoveExtLength(_pos1, _pos2);
	
	return eVol;
}

public double totalExtrusionLength(){
	double eLength = 0;
	/*
	for (GCodePosition code : this) {
		if (indexOf(code) > 0){
		GCodePosition prevCode = get(indexOf(code)- 1);
		double thisExtrude = getMoveExtLength(prevCode, code);
		if (thisExtrude > 0){
			eLength = eLength + thisExtrude;
		}
		}
	}
	*/
	
	return get(size()-1).getE();
}
}
