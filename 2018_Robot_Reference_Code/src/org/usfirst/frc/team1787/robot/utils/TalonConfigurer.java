package org.usfirst.frc.team1787.robot.utils;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class TalonConfigurer {
	
	public static final int DEFAULT_TALON_FUNCTION_TIMEOUT_MS = 10;
  
  /**
   * Configs the given talon as follows
   * 1) Sets inverted to false
   * 2) Sets brake mode to enabled
   * 3) Sets current limit to default
   * 4) Sets voltage ramp rate minimum to default
   * 5) Sets deadband range to default
   * 6) Enables voltage compensation mode, with a max voltage of 12 volts
   * @param talon the talon to configure.
   */
  public static void configTalon(WPI_TalonSRX talon) {
    System.out.println("Configuring TalonSRX #" + talon.getDeviceID());
    
    // can be set to true if positive values make the motor turn backwards
    talon.setInverted(false);
    
    // determines what the motor does (either brake or coast) when an output of 0 is sent to the motor controller
    talon.setNeutralMode(NeutralMode.Brake);
    
    /*The above 2 config settings are the only config settings that need to be set on a "follower" talon.
     * it seems that all other config settings will be inherited from the master talon. */
    
    // Current Config Settings
    /* There are 3 different parameters that are important regarding current limiting.
     * 1) peakCurrentLimit: this is the max amount of current (in amps) that the motor controller will give to the motor.
     * 2) peakCurrentDuration: this is the max amount of time (in ms) that the peakCurrentLimit will be applied to the motor. 
     * 3) continuousCurrentLimit: this is the "fallback" current that will be applied to a motor 
     * that has been drawing the peakCurrentLimit for the peakCurrentDuration. 
     * Note: If the peakCurrentLimit is set to 0, then the continuousCurrentLimit will be enforced at all times. */
    talon.configPeakCurrentLimit(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.configPeakCurrentDuration(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.configContinuousCurrentLimit(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.enableCurrentLimit(false); // intentionally not enabling any current limit yet.
    
    // Voltage Config Settings
    /* Open Loop Ramp: This sets constraints on the rate at which the voltage applied can change. This is to help prevent
     * instantaneous changes in voltage that may harm the motor. 
     * parameter: (minimumSecondsFromNeutralToFull, timeoutForChangingThisSettingInMs) 
     * timeoutForChanging this setting = the max time allowed for the talon to change this setting. */
    talon.configOpenloopRamp(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    // factory default deadband is 4% (4% = 0.04)
    talon.configNeutralDeadband(0.04, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    
    // Voltage Compensation
    /* helps maintain consistency despite changing battery voltage. */
    // Sets max voltage for voltage compensation mode.
    talon.configVoltageCompSaturation(12, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.enableVoltageCompensation(true);
    
    // Processing Speed
    talon.setControlFramePeriod(ControlFrame.Control_3_General, 10);
     
    talon.set(0);
  }
  
  /* Random Notes on WPI_TalonSRX */
  /* Notes on TalonSRX Update Frames can be found in the PDF version of the software reference manual.
   * Essentially, you ca think of a frame as similar to a camera frame, in that it is a snapshot of some data
   * at a particular point in time. These frames are what are sent over the CAN cable to comunicate to other divices.
   * The TalonSRX utilize 2 main types of frames:
   * 1) Status Frame - A frame sent from the TalonSRX that contains data about it and sensors connected to it.
   * 2) Control Frame - A frem sent to the TalonSRX that contains data about desired control modes and output values.
   * 
   * Please note that the Status Frame has various subtypes that each hold their own information and are published 
   * at different rates. Based off of last year's documentation, it appears there are 4 different types of Status Frame,
   * each with different types of information in them, but this year's API Seems to suggest that there may now be more than
   * 4 different types of status frames.
   * 
   * The update rate of these frames is likely just kept as the default (Actual default values can be found in PDF),
   * but I will also note that these update rates can be specified using the "talon.setStatusFramePeriod" and "talon.setControlFramePeriod"
   * However, please note that the not any period can be used. Rather a pre-defined set of periods is provided for you to choose from.
   *  */
}
