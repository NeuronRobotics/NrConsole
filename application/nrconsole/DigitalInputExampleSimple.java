//Create a digital input object
DigitalInputChannel dig = new DigitalInputChannel(dyio.getChannel(0));
//Loop forever printing out the state of the button
while(true){
	System.out.println(dig.isHigh());
	Thread.sleep(100);

}