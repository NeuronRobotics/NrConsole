println(dyio)
while(true){
	ThreadUtil.wait(100)                     // Spcae out the loop

	long start = System.currentTimeMillis()  //capture the starting value 

	int value = dyio.getValue(15)            //grab the value of pin 15
	int scaled = value/4                     //scale the analog voltage to match the range of the servos
	dyio.setValue(0,scaled)                  // set the new value to the servo

	//Print out this loops values
	print(" Loop took = "+(System.currentTimeMillis()-start))
	print("ms Value= "+value)
	println(" Scaled= "+scaled)
}