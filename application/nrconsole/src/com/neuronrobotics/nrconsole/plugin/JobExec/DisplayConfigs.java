package com.neuronrobotics.nrconsole.plugin.JobExec;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

public class DisplayConfigs {
	private Vector3f printVolume = new Vector3f(200,0,200);
	private Vector3f printOrigin = new Vector3f(0,0,0);
	private Mesh pVol;
	private BoundingBox pbb;
	private double nozzleDia;
	private double filaDia;
	
	public void configure(Vector3f _printVolume, Vector3f _printOrigin){
		setPrintVolume(_printVolume);
		printOrigin = _printOrigin;
		
	}
	public void configureRect(float _printX, float _printY, float _printZ){
		getPrintVolume().set(_printX, _printY, _printZ);
	}
	public void configureCylinder(float _printR, float _printZ){
		getPrintVolume().set(_printR, 0, _printZ);
	}
	public void configureOrigin(float _originX, float _originY, float _originZ){
		printOrigin.set(_originX, _originY, _originZ);
	}
	
	
	public Mesh getPrintVol(){
		if (pVol == null){
		
		if (getPrintVolume().getY()==0){//It's a cylinder
			pVol = new Cylinder(100,100,(getPrintVolume().getX()/2),getPrintVolume().getZ(), true, false);
		}
		else{//It's a cube
			Box b = new Box();
			printOrigin.set(getPrintVolume().getX()/2,getPrintVolume().getY()/2,0);
			b.updateGeometry(printOrigin, getPrintVolume().getX()/2, getPrintVolume().getY()/2, getPrintVolume().getZ()/2);
			pVol = b;
		}
		}
		
		return pVol;
		
	}
	public boolean isCubeVol(){
		if (getPrintVolume().getY() == 0){
			return false;
		}
		else{
			return true;
		}
	}
	public Mesh getPrintBase(){
		Mesh base;
		
		if (getPrintVolume().getY()==0){//It's a cylinder
			base = new Cylinder(100,100,(getPrintVolume().getX()/2),0,true,false);
		}
		else{//It's a cube
			Box b = new Box();
			printOrigin.set(getPrintVolume().getX()/2,getPrintVolume().getY()/2,0);
			b.updateGeometry(printOrigin, getPrintVolume().getX()/2, getPrintVolume().getY()/2, 0);
			base = b;
		}
		
		
		return base;
		
	}
	public BoundingBox getVolBB(){
		if (pbb == null){
			pbb = new BoundingBox((BoundingBox) getPrintVol().getBound());
		}
		
		
		return pbb;
	}
	public Vector3f getPrintVolume() {
		return printVolume;
	}
	public void setPrintVolume(Vector3f printVolume) {
		this.printVolume = printVolume;
	}
	public double getNozzleDia() {
		return nozzleDia;
	}
	public void setNozzleDia(double nozzleDia) {
		this.nozzleDia = nozzleDia;
	}
	public double getFilaDia() {
		return filaDia;
	}
	public void setFilaDia(double filaDia) {
		this.filaDia = filaDia;
	}
}
