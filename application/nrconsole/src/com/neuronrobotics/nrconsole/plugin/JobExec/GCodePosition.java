package com.neuronrobotics.nrconsole.plugin.JobExec;

public class GCodePosition {
	private double x = -1;
	private double y = -1;
	private double z = -1;
	private double e = -1;
	private int layer;
	
	private GCodePosition prevCode = null;
public GCodePosition(){
	
}
public GCodePosition(GCodePosition _prevCode, double _x, double _y, double _z, double _e, int _layer){
	setPrevCode(_prevCode);
	setX(_x);
	setY(_y);
	setZ(_z);
	setE(_e);
	setLayer(_layer);
	
}

/**
 * @return the x
 */
public double getX() {
	return x;
}
/**
 * @param x the x to set
 */
public void setX(double _x) {
	x = _x;
}
/**
 * @return the y
 */
public double getY() {
	return y;
}
/**
 * @param _y the y to set
 */
public void setY(double _y) {
	y = _y;
}
/**
 * @return the z
 */
public double getZ() {
	return z;
}
/**
 * @param _z the z to set
 */
public void setZ(double _z) {
	z = _z;
}
/**
 * @return the e
 */
public double getE() {
	return e;
}
/**
 * @param _e the e to set
 */
public void setE(double _e) {
	e = _e;
}
public int getLayer() {
	return layer;
}
public void setLayer(int layer) {
	this.layer = layer;
}

public GCodePosition getPrevCode() {
	return prevCode;
}
public void setPrevCode(GCodePosition prevCode) {
	this.prevCode = prevCode;
}
}
