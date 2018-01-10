package org.usfirst.frc.team1787.robot.utils;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class TalonConfigurer {
	
	public static final int DEFAULT_TALON_FUNCTION_TIMEOUT_MS = 10;
  
  /**
   * Configs the given talon as follows
   * 1) Sets inverted to false
   * 2) Sets brake mode to enabled
   * 3) Sets current limit to default
   * 4) Sets voltage ramp rate minimum to default
   * 5) Enables voltage compensation mode, with a max voltage of 12 volts
   * @param talon the talon to configure.
   */
  public static void configTalon(WPI_TalonSRX talon) {
    // can be set to true if positive values make the motor turn backwards
    talon.setInverted(false);
    
    // determines what the motor does (either brake or coast) when an output of 0 is sent to the motor controller
    talon.setNeutralMode(NeutralMode.Brake);
    
    // The above 2 config settings are the only config settings that need to be set on a "follower" talon.
    // all other config settings will be inherited from the master talon.
    
    // Current Limiting
    /* There are 3 different parameters that are important regarding current limiting.
     * 1) peakCurrentLimit: this is the max amount of current (in amps) that the motor controller will give to the motor.
     * 2) peakCurrentDuration: this is the max amount of time (in ms) that the peakCurrentLimit will be applied to the motor. 
     * 3) continuousCurrentLimit: this is the "fallback" current that will be applied to a motor 
     * that has been drawing the peakCurrentLimit for the peakCurrentDuration. 
     * Note: If the peakCurrentLimit is set to 0, then the continuousCurrentLimit will be enforced at all times. */
    talon.configPeakCurrentLimit(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.configPeakCurrentDuration(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.configContinuousCurrentLimit(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.enableCurrentLimit(false);
    
    // Voltage Ramp Rate
    /* This sets constraints on the rate at which the voltage applied can change. This is to help prevent
     * instantaneous changes in voltage that may harm the motor. 
     * parameter: (minimumSecondsFromNeutralToFull, timeoutForChangingThisSettingInMs) 
     * timeoutForChanging this setting = the max time allowed for the talon to change this setting. 
     * Note: set the minSecondsFromNeutralToFull to 0 to enforce no ramp rate. */
    talon.configOpenloopRamp(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    
    // Voltage Compensation
    /* helps maintain consistency despite changing battery voltage. */
    talon.configNominalOutputForward(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.configNominalOutputReverse(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.configPeakOutputForward(1, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.configPeakOutputReverse(-1, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    
    // Sets max voltage for voltage compensation mode.
    talon.configVoltageCompSaturation(12, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.enableVoltageCompensation(true);
     
    talon.set(0);
  }
  
  /* Random Notes on WPI_TalonSRX */
  /* talon is constructed with default update rate of 10ms
   * if a different update rate is desired, it can be set as a 2nd 
   * parameter to the constructor. The given # will be the update rate in ms.
   * note that increasing this rate will increase bandwidth. leaving it at the default is probably best
   * for now until we have time to research / test the consequences of changing it.
   */
}
