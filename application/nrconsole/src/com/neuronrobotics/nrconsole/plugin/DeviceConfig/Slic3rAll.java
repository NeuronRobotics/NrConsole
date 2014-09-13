package com.neuronrobotics.nrconsole.plugin.DeviceConfig;

import java.util.ArrayList;

import net.miginfocom.swing.MigLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JFormattedTextField;

public class Slic3rAll extends SettingsPanel implements SettingsChangeListener{
	private JLabel lblNozzleDiameter;
	private JLabel lblPrintCenter;
	private JLabel lblFilamentDiameter;
	private JLabel lblExtrusionMultiplier;
	private JLabel lblPrintTemperature;
	private JLabel lblBedTemperature;
	private JLabel lblLayerHeight;
	private JLabel lblWallThickness;
	private JLabel lblUseSupportMaterial;
	private JLabel lblRetractLength;
	private JLabel lblTravelSpeed;
	private JLabel lblPerimeterSpeed;
	private JLabel lblBridgeSpeed;
	private JLabel lblGapFillSpeed;
	private JLabel lblInfillSpeed;
	private JLabel lblSupportMaterialSpeed;
	private JLabel lblSmallPerimeterSpeed;
	private JLabel lblExternalPerimeterSpeed;
	private JLabel lblSolidInfillSpeed;
	private JLabel lblTopSolidInfill;
	private JLabel lblSupportMaterialInterface;
	private JLabel lblFirstLayerSpeed;
	private JFormattedTextField formattedTextField;
	private JLabel lblX;
	private JFormattedTextField tfPrintCenterX;
	private JLabel lblY;
	private JFormattedTextField tfPrintCenterY;
	private JFormattedTextField tfFilaDia;
	private JFormattedTextField tfExtrusionMult;
	private JFormattedTextField tfPTemp;
	private JFormattedTextField tfBTemp;
	private JFormattedTextField tfLayerHeight;
	private JFormattedTextField tfWallThickness;
	private JFormattedTextField tfRetractLength;
	private JFormattedTextField tfTravelSpeed;
	private JFormattedTextField tfPerimeterSpeed;
	private JFormattedTextField tfBridgeSpeed;
	private JFormattedTextField tfGapFillSpeed;
	private JFormattedTextField tfInfillSpeed;
	private JFormattedTextField tfSupportMaterialSpeed;
	private JFormattedTextField tfSmallPerimeterSpeedPercent;
	private JFormattedTextField tfExternalPerimeterSpeedPercent;
	private JFormattedTextField tfSolidInfillSpeedPercent;
	private JFormattedTextField tfTopSolidInfillSpeedPercent;
	private JFormattedTextField tfSupportMaterialInterSpeedPercent;
	private JFormattedTextField tfFirstLayerSpeedPercent;
	public Slic3rAll() {		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				initComponents();
			}
		});

	}

	
	@Override
	public String getPanelName() {
		return "Slic3r Settings";
	}


	@Override
	public void settingsChanged() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void initComponents() {

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblNozzleDiameter())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getFormattedTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblPrintCenter())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getLblX())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfPrintCenterX(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getLblY())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfPrintCenterY(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblFilamentDiameter())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfFilaDia(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblExtrusionMultiplier())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfExtrusionMult(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblPrintTemperature())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfPTemp(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblBedTemperature())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfBTemp(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblLayerHeight())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfLayerHeight(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblWallThickness())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfWallThickness(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(getLblUseSupportMaterial())
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblRetractLength())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfRetractLength(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblTravelSpeed())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfTravelSpeed(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblPerimeterSpeed())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfPerimeterSpeed(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblInfillSpeed())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfInfillSpeed(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblSupportMaterialSpeed())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfSupportMaterialSpeed(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblSmallPerimeterSpeed())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfSmallPerimeterSpeedPercent(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblExternalPerimeterSpeed())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfExternalPerimeterSpeedPercent(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblSolidInfillSpeed())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfSolidInfillSpeedPercent(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblTopSolidInfill())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfTopSolidInfillSpeedPercent(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblSupportMaterialInterface())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfSupportMaterialInterSpeedPercent(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getLblFirstLayerSpeed())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getTfFirstLayerSpeedPercent(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(getLblBridgeSpeed())
								.addComponent(getLblGapFillSpeed()))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(getTfGapFillSpeed(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getTfBridgeSpeed(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(230, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblNozzleDiameter())
						.addComponent(getFormattedTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblPrintCenter())
						.addComponent(getLblX())
						.addComponent(getTfPrintCenterX(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(getLblY())
						.addComponent(getTfPrintCenterY(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblFilamentDiameter())
						.addComponent(getTfFilaDia(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblExtrusionMultiplier())
						.addComponent(getTfExtrusionMult(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblPrintTemperature())
						.addComponent(getTfPTemp(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblBedTemperature())
						.addComponent(getTfBTemp(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblLayerHeight())
						.addComponent(getTfLayerHeight(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblWallThickness())
						.addComponent(getTfWallThickness(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(getLblUseSupportMaterial())
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblRetractLength())
						.addComponent(getTfRetractLength(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblTravelSpeed())
						.addComponent(getTfTravelSpeed(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblPerimeterSpeed())
						.addComponent(getTfPerimeterSpeed(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblBridgeSpeed())
						.addComponent(getTfBridgeSpeed(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblGapFillSpeed())
						.addComponent(getTfGapFillSpeed(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblInfillSpeed())
						.addComponent(getTfInfillSpeed(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblSupportMaterialSpeed())
						.addComponent(getTfSupportMaterialSpeed(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblSmallPerimeterSpeed())
						.addComponent(getTfSmallPerimeterSpeedPercent(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblExternalPerimeterSpeed())
						.addComponent(getTfExternalPerimeterSpeedPercent(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblSolidInfillSpeed())
						.addComponent(getTfSolidInfillSpeedPercent(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblTopSolidInfill())
						.addComponent(getTfTopSolidInfillSpeedPercent(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblSupportMaterialInterface())
						.addComponent(getTfSupportMaterialInterSpeedPercent(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblFirstLayerSpeed())
						.addComponent(getTfFirstLayerSpeedPercent(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
		
	}

	
	
	
	

	private JLabel getLblNozzleDiameter() {
		if (lblNozzleDiameter == null) {
			lblNozzleDiameter = new JLabel("Nozzle Diameter");
		}
		return lblNozzleDiameter;
	}
	private JLabel getLblPrintCenter() {
		if (lblPrintCenter == null) {
			lblPrintCenter = new JLabel("Print Center");
		}
		return lblPrintCenter;
	}
	private JLabel getLblFilamentDiameter() {
		if (lblFilamentDiameter == null) {
			lblFilamentDiameter = new JLabel("Filament Diameter");
		}
		return lblFilamentDiameter;
	}
	private JLabel getLblExtrusionMultiplier() {
		if (lblExtrusionMultiplier == null) {
			lblExtrusionMultiplier = new JLabel("Extrusion Multiplier");
		}
		return lblExtrusionMultiplier;
	}
	private JLabel getLblPrintTemperature() {
		if (lblPrintTemperature == null) {
			lblPrintTemperature = new JLabel("Print Temperature");
		}
		return lblPrintTemperature;
	}
	private JLabel getLblBedTemperature() {
		if (lblBedTemperature == null) {
			lblBedTemperature = new JLabel("Bed Temperature");
		}
		return lblBedTemperature;
	}
	private JLabel getLblLayerHeight() {
		if (lblLayerHeight == null) {
			lblLayerHeight = new JLabel("Layer Height");
		}
		return lblLayerHeight;
	}
	private JLabel getLblWallThickness() {
		if (lblWallThickness == null) {
			lblWallThickness = new JLabel("Wall Thickness");
		}
		return lblWallThickness;
	}
	private JLabel getLblUseSupportMaterial() {
		if (lblUseSupportMaterial == null) {
			lblUseSupportMaterial = new JLabel("Use Support Material");
		}
		return lblUseSupportMaterial;
	}
	private JLabel getLblRetractLength() {
		if (lblRetractLength == null) {
			lblRetractLength = new JLabel("Retract Length");
		}
		return lblRetractLength;
	}
	private JLabel getLblTravelSpeed() {
		if (lblTravelSpeed == null) {
			lblTravelSpeed = new JLabel("Travel Speed");
		}
		return lblTravelSpeed;
	}
	private JLabel getLblPerimeterSpeed() {
		if (lblPerimeterSpeed == null) {
			lblPerimeterSpeed = new JLabel("Perimeter Speed");
		}
		return lblPerimeterSpeed;
	}
	private JLabel getLblBridgeSpeed() {
		if (lblBridgeSpeed == null) {
			lblBridgeSpeed = new JLabel("Bridge Speed");
		}
		return lblBridgeSpeed;
	}
	private JLabel getLblGapFillSpeed() {
		if (lblGapFillSpeed == null) {
			lblGapFillSpeed = new JLabel("Gap Fill Speed");
		}
		return lblGapFillSpeed;
	}
	private JLabel getLblInfillSpeed() {
		if (lblInfillSpeed == null) {
			lblInfillSpeed = new JLabel("Infill Speed");
		}
		return lblInfillSpeed;
	}
	private JLabel getLblSupportMaterialSpeed() {
		if (lblSupportMaterialSpeed == null) {
			lblSupportMaterialSpeed = new JLabel("Support Material Speed");
		}
		return lblSupportMaterialSpeed;
	}
	private JLabel getLblSmallPerimeterSpeed() {
		if (lblSmallPerimeterSpeed == null) {
			lblSmallPerimeterSpeed = new JLabel("Small Perimeter Speed Percent");
		}
		return lblSmallPerimeterSpeed;
	}
	private JLabel getLblExternalPerimeterSpeed() {
		if (lblExternalPerimeterSpeed == null) {
			lblExternalPerimeterSpeed = new JLabel("External Perimeter Speed Percent");
		}
		return lblExternalPerimeterSpeed;
	}
	private JLabel getLblSolidInfillSpeed() {
		if (lblSolidInfillSpeed == null) {
			lblSolidInfillSpeed = new JLabel("Solid Infill Speed Percent");
		}
		return lblSolidInfillSpeed;
	}
	private JLabel getLblTopSolidInfill() {
		if (lblTopSolidInfill == null) {
			lblTopSolidInfill = new JLabel("Top Solid Infill Speed Percent");
		}
		return lblTopSolidInfill;
	}
	private JLabel getLblSupportMaterialInterface() {
		if (lblSupportMaterialInterface == null) {
			lblSupportMaterialInterface = new JLabel("Support Material Interface Speed Percent");
		}
		return lblSupportMaterialInterface;
	}
	private JLabel getLblFirstLayerSpeed() {
		if (lblFirstLayerSpeed == null) {
			lblFirstLayerSpeed = new JLabel("First Layer Speed Percent");
		}
		return lblFirstLayerSpeed;
	}
	private JFormattedTextField getFormattedTextField() {
		if (formattedTextField == null) {
			formattedTextField = new JFormattedTextField();
		}
		return formattedTextField;
	}
	private JLabel getLblX() {
		if (lblX == null) {
			lblX = new JLabel("X:");
		}
		return lblX;
	}
	private JFormattedTextField getTfPrintCenterX() {
		if (tfPrintCenterX == null) {
			tfPrintCenterX = new JFormattedTextField();
		}
		return tfPrintCenterX;
	}
	private JLabel getLblY() {
		if (lblY == null) {
			lblY = new JLabel("Y:");
		}
		return lblY;
	}
	private JFormattedTextField getTfPrintCenterY() {
		if (tfPrintCenterY == null) {
			tfPrintCenterY = new JFormattedTextField();
		}
		return tfPrintCenterY;
	}
	private JFormattedTextField getTfFilaDia() {
		if (tfFilaDia == null) {
			tfFilaDia = new JFormattedTextField();
		}
		return tfFilaDia;
	}
	private JFormattedTextField getTfExtrusionMult() {
		if (tfExtrusionMult == null) {
			tfExtrusionMult = new JFormattedTextField();
		}
		return tfExtrusionMult;
	}
	private JFormattedTextField getTfPTemp() {
		if (tfPTemp == null) {
			tfPTemp = new JFormattedTextField();
		}
		return tfPTemp;
	}
	private JFormattedTextField getTfBTemp() {
		if (tfBTemp == null) {
			tfBTemp = new JFormattedTextField();
		}
		return tfBTemp;
	}
	private JFormattedTextField getTfLayerHeight() {
		if (tfLayerHeight == null) {
			tfLayerHeight = new JFormattedTextField();
		}
		return tfLayerHeight;
	}
	private JFormattedTextField getTfWallThickness() {
		if (tfWallThickness == null) {
			tfWallThickness = new JFormattedTextField();
		}
		return tfWallThickness;
	}
	private JFormattedTextField getTfRetractLength() {
		if (tfRetractLength == null) {
			tfRetractLength = new JFormattedTextField();
		}
		return tfRetractLength;
	}
	private JFormattedTextField getTfTravelSpeed() {
		if (tfTravelSpeed == null) {
			tfTravelSpeed = new JFormattedTextField();
		}
		return tfTravelSpeed;
	}
	private JFormattedTextField getTfPerimeterSpeed() {
		if (tfPerimeterSpeed == null) {
			tfPerimeterSpeed = new JFormattedTextField();
		}
		return tfPerimeterSpeed;
	}
	private JFormattedTextField getTfBridgeSpeed() {
		if (tfBridgeSpeed == null) {
			tfBridgeSpeed = new JFormattedTextField();
		}
		return tfBridgeSpeed;
	}
	private JFormattedTextField getTfGapFillSpeed() {
		if (tfGapFillSpeed == null) {
			tfGapFillSpeed = new JFormattedTextField();
		}
		return tfGapFillSpeed;
	}
	private JFormattedTextField getTfInfillSpeed() {
		if (tfInfillSpeed == null) {
			tfInfillSpeed = new JFormattedTextField();
		}
		return tfInfillSpeed;
	}
	private JFormattedTextField getTfSupportMaterialSpeed() {
		if (tfSupportMaterialSpeed == null) {
			tfSupportMaterialSpeed = new JFormattedTextField();
		}
		return tfSupportMaterialSpeed;
	}
	private JFormattedTextField getTfSmallPerimeterSpeedPercent() {
		if (tfSmallPerimeterSpeedPercent == null) {
			tfSmallPerimeterSpeedPercent = new JFormattedTextField();
		}
		return tfSmallPerimeterSpeedPercent;
	}
	private JFormattedTextField getTfExternalPerimeterSpeedPercent() {
		if (tfExternalPerimeterSpeedPercent == null) {
			tfExternalPerimeterSpeedPercent = new JFormattedTextField();
		}
		return tfExternalPerimeterSpeedPercent;
	}
	private JFormattedTextField getTfSolidInfillSpeedPercent() {
		if (tfSolidInfillSpeedPercent == null) {
			tfSolidInfillSpeedPercent = new JFormattedTextField();
		}
		return tfSolidInfillSpeedPercent;
	}
	private JFormattedTextField getTfTopSolidInfillSpeedPercent() {
		if (tfTopSolidInfillSpeedPercent == null) {
			tfTopSolidInfillSpeedPercent = new JFormattedTextField();
		}
		return tfTopSolidInfillSpeedPercent;
	}
	private JFormattedTextField getTfSupportMaterialInterSpeedPercent() {
		if (tfSupportMaterialInterSpeedPercent == null) {
			tfSupportMaterialInterSpeedPercent = new JFormattedTextField();
		}
		return tfSupportMaterialInterSpeedPercent;
	}
	private JFormattedTextField getTfFirstLayerSpeedPercent() {
		if (tfFirstLayerSpeedPercent == null) {
			tfFirstLayerSpeedPercent = new JFormattedTextField();
		}
		return tfFirstLayerSpeedPercent;
	}
}
