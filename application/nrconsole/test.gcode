G90

G21
G92 E0

M109 S200 ; wait for temperature to be reached

F1800
G1 X30 Y0 Z100

G1 X30 Y-30	
G1 X30 Y30		
G1 X-30 Y30	
G1 X-30 Y-30	
G1 X30 Y-30	

G1 X0 Y0 Z150
G92 E0