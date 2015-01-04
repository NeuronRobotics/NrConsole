package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.util.ArrayList;

public class GCodes extends ArrayList<GCodePosition>{

DisplayConfigs displayConfigs;

public GCodes(DisplayConfigs _displayConfigs){
	displayConfigs = _displayConfigs;
}





int layers = 1;



public boolean add(double _x, double _y, double _z, double _e){
	
	if (size() >  1){
		
	if (get(size()-1).getZ() != _z){
		layers++;
		
	}
	
	return add(new GCodePosition(get(size()-1), _x, _y, _z, _e, layers));
	}
	return add(new GCodePosition(null, _x, _y, _z, _e, layers));
}






public void printOutput(){
	
	for (GCodePosition code : this) {
		System.out.println("Position #: " + indexOf(code) + " x: " + code.getX() + " y: " + code.getY() + " z: " + code.getZ() + " e: " + code.getE());
	}
	System.out.println("Number of Layers: " + numLayers());
	System.out.println("Total Extrusion Length: " + totalExtrusionLength());
}

public boolean isRapidMove(int _position1, int _position2){
	if (get(_position1).getE() >= get(_position2).getE()){
		return true;
	}
	else
	{
		return false;
	}
}
public boolean isRapidMove(GCodePosition _pos1, GCodePosition _pos2){
	if (_pos1.getE() >= _pos2.getE()){
		return true;
	}
	else
	{
		return false;
	}
}
public boolean isRapidMove(GCodePosition _code){
	GCodePosition prevCode = getPrevCode(_code);
	if (prevCode != null){
		//System.out.println("boop");
		return isRapidMove(prevCode, _code);
		
	}
	
	return true;
}


public double getLayerHeight(int _layer){
	double height = 0;
	double prevHeight = 0;
	for (GCodePosition code : this) {
		if (isRapidMove(code) == false){
			if ((code.getLayer() < _layer)){
				prevHeight = code.getZ();
			}
			else{
				
				return code.getZ() - prevHeight;
			}
		}
	}
		return height;
}
public GCodePosition getPrevCode(GCodePosition _code){
	return _code.getPrevCode();
}

public double getLayerHeight(GCodePosition _code){
	return getLayerHeight(_code.getLayer());
	//return _code.getLayerHeight();
}
public int numLayers(){
	return layers;
	}
public int getLayer(GCodePosition _code){
	return _code.getLayer();
	}
	

public double getMoveLength(GCodePosition _code){
	GCodePosition prevCode = getPrevCode(_code);
	if (prevCode != null){	
	
	double dX =  ((_code.getX()-prevCode.getX()));
	double dY = ((_code.getY()-prevCode.getY()));
	double dZ =  ((_code.getZ()-prevCode.getZ()));
	double length =  (Math.sqrt((Math.pow((dX),2))+(Math.pow((dY),2))+(Math.pow((dZ),2))));
	if (length > 45){
		//System.out.println("Move Length: " + length);
	}
	
	return length;
	}
	else{
		return 0;
	}
}

public double getMoveExtLength(int _position1, int _position2){
	double eLength = 0;
	
	double startE = get(_position1).getE();
	double endE = get(_position2).getE();
	
	eLength = endE - startE;
	return eLength;
	
}
public double getMoveExtLength(GCodePosition _pos1, GCodePosition _pos2){
	
	
	double eLength = 0;
	
	double startE = _pos1.getE();
	double endE = _pos2.getE();
	
	eLength = endE - startE;
	return eLength;
	
}

public double getMoveExtLength(GCodePosition _code){
	GCodePosition prevCode = getPrevCode(_code);
	if (prevCode != null){	
	double eLength = 0;
	
	double startE = prevCode.getE();
	double endE = _code.getE();
	
	eLength = endE - startE;
	return eLength;
	}
	else{
		return 0;
	}
	
}
public double getMoveExtVolume(int _position1, int _position2){
	double eVol = 0;
	
	eVol = (Math.PI * (Math.pow((displayConfigs.getFilaDia()/2),2))) * getMoveExtLength(_position1, _position2);
	
	return eVol;
}


public double getMoveExtVolume(GCodePosition _pos1, GCodePosition _pos2){
	double eVol = 0;
	
	eVol = (Math.PI * (Math.pow((displayConfigs.getFilaDia()/2),2))) * getMoveExtLength(_pos1, _pos2);
	
	return eVol;
}

/**
 * Returns the volume of the previous extrusion leading up to the given code
 * @param _code The line of g-code which is at the end of the movement
 * @return the volume of the extrusion in units^3 (usually mm^3)
 */
public double getMoveExtVolume(GCodePosition _code){
	GCodePosition prevCode;
	if (indexOf(_code) > 0){
		prevCode = get(indexOf(_code) - 1);
		return getMoveExtVolume(prevCode, _code);
	}
	else{
		return 0;
	}
	
}

/**
 * Returns the width of the previous extrusion leading up the the given code.
 * The width is calculated using the volume of the extrusion and the layer height.
 * The advantage to this method is that is accurately simulates the width
 * of each line of a print based on the given parameters, thus helping the 
 * user to accurately determine if the printer is going to feed enough filament. 
 * @param _code The line of g-code which is at the end of the movement
 * @return the width of the extrusion in units (usually mm^3)
 */
public double getMoveVolExtWidth(GCodePosition _code){
	double height = getLayerHeight(_code);
	double vol = getMoveExtVolume(_code);
	double length = getMoveLength(_code);
	double width = (vol/(height*length));
	if (width > 1){
		//System.out.println("Height: " + height + " Ext Length: " + getMoveExtLength(_code) + " Length: " + length + " Width: " + width);
	}
	
	return width;
	
}

/**
 * This method runs a number of tests on the given g-code to determine if
 * it is "good".  These tests include checking the extrusion width against the nozzle
 * diameter, checking layer height, etc.
 * @param _code The g-code to perform the tests on
 * @return Whether the code has passed the tests
 */
public boolean isGoodExtrusion(GCodePosition _code){
	boolean isOK = true;
	
	if (getMoveExtLength(_code) > 0){
		if (getMoveVolExtWidth(_code) <= displayConfigs.getNozzleDia()*.75){
			isOK = false;
		}
		if (getLayerHeight(_code) >= displayConfigs.getNozzleDia()){
			isOK = false;
		}
	}
	
	
	return isOK;
	
}
public boolean isPrintMove(GCodePosition _code){
	if (getMoveExtLength(_code) > 0){
		return true;
	}
	else{
		return false;
	}
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
