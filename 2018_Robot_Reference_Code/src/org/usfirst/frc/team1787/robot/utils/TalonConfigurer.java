package org.usfirst.frc.team1787.robot.utils;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

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
  public static void configOpenLoopSettings(TalonSRX talon) {
    System.out.println("Configuring TalonSRX #" + talon.getDeviceID());
    
    /* config which direction the motor turns when calling talon.set() with positive values.
     * by convention, this should be whichever direction that's considered forward.
     * Note: all motors in a master/follower chain need to have their direction set independently.
     */
    talon.setInverted(false);
    
    /* config what the motor does when set to an output of 0.
     * Note: all motors in a master/follower chain need to have their neutral mode set independently.
     */
    talon.setNeutralMode(NeutralMode.Brake);
    
    // Current (as in electric current) Config Settings
    /* If a motor draws an amount of current greater than **peakCurrentLimit** amps
     * for more than **peakCurrentDuration** milliseconds, then current limiting will be activated.
     * This causes the current draw to be limited to **continuousCurrentLimit** amps.
     * 
     * If peakCurrentDuration is configured to 0, current limiting is enforced 
     * immediately after current-draw surpasses the peak current threshold.
     * 
     * Warning: currentLimit below 5 amps not recommended. See phoenix documentation for why. 
     */
    talon.configPeakCurrentLimit(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.configPeakCurrentDuration(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.configContinuousCurrentLimit(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.enableCurrentLimit(false);
    
    // Voltage Config Settings
    /* Allows constraints on the rate at which applied voltage can change.
     * The input is the minimum amount of time (in seconds) required to go from an output of 0 to full output.
     * 
     * Note: followers in a master/follower chain should all be configured 
     * with a ramp rate of 0 (i.e. no restriction on ramp rate), because they will already
     * mimic the output of the master.
     */
    talon.configOpenloopRamp(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.configClosedloopRamp(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    
    
    // factory default deadband is 4% (4% = 0.04)
    talon.configNeutralDeadband(0.04, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    
    // Voltage Compensation
    /* helps maintain consistency despite changing battery voltage. */
    // Sets max voltage for voltage compensation mode.
    talon.configVoltageCompSaturation(12, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
    talon.enableVoltageCompensation(true);
    
    // Voltage Measurement (# of samples in rolling average)
    talon.configVoltageMeasurementFilter(32, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
     
    talon.set(ControlMode.PercentOutput, 0);
  }
  
  public static void configSensorSettings(TalonSRX talon) {
    /* IMPORTANT NOTE: Sensor readings are reported in native units. Velocity reported in native units per 100ms.
     * See CTRE documentation on github (https://github.com/CrossTheRoadElec/Phoenix-Documentation) for more info.
     */
	
	// Limit Switches
    talon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.overrideLimitSwitchesEnable(false); // <- can be used to enable and disable limit switch features on the fly.
	
	// Extra Limit Switch Features
	/* Limit switches can be configured to zero the selectedSensor position when asserted. Pass 1 to the 2nd argument
	 * to enable this feature. Pass 0 to the 2nd argument to disable this feature. Arguments 2 & 3 aren't used.
	 * See pg. 103 for some more info and a similar config for quadrature encoders w/ an index pin.
	 */
	talon.configSetParameter(ParamEnum.eClearPositionOnLimitF, 0, 0, 0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.configSetParameter(ParamEnum.eClearPositionOnLimitR, 0, 0, 0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	
	// Other Sensors (see TalonSRX software reference manual for full list of supported sensors)
	talon.configSelectedFeedbackSensor(FeedbackDevice.None, 0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.setSensorPhase(false); // <- use this function to ensure positive sensor readings correspond to positive motor output.
	
	/* if using an analog sensor, you can select whether or not it's continuous.
	 * An example of a continuous sensor is a gyro that will give readings 
	 * above 360 degrees instead of just wrapping back to 0.
	 * See TalonSRX software reference manual pg. 46 for more info.
	 */
	//talon.configSetParameter(ParamEnum.eFeedbackNotContinuous, 0, 0, 0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	
	// Soft Limit can be used to stop a motor if the selectedSensorPosition goes past the given threshold.
	// if (selectedSensorPosition > fwdSoftLimitThreshold) stop fwd movement.
	// if (selectedSensorPosition < reverseSoftLimitThreshold) stop backward movement.
	talon.configForwardSoftLimitThreshold(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.configReverseSoftLimitThreshold(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.configForwardSoftLimitEnable(false, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.configReverseSoftLimitEnable(false, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.overrideSoftLimitsEnable(false); // <- pass false to disable the soft limits feature. pass true to honor the given configs above.
	
	// Config Sensor Measurement Settings (see pages. 50 & 52 in the TalonSRX software reference manual for more info)
	talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.configVelocityMeasurementWindow(64, DEFAULT_TALON_FUNCTION_TIMEOUT_MS); // <- num of samples in rolling average.
	
	// Note, if using a Tachometer, see github documentation and software reference manual, as they have some specific configs just for them.
	talon.setSelectedSensorPosition(0, 0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	
  }
  
  public static void configClosedLoopSettings(TalonSRX talon) {
	talon.configClosedloopRamp(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	
	
	// see software reference pg 72-74
	talon.selectProfileSlot(0, 0);
	talon.config_kP(0, 0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.config_kI(0, 0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.config_kD(0, 0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.config_kF(0, 0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	
	// pg. 78
	talon.config_IntegralZone(0, 0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	
	// pg. 66
	talon.configNominalOutputForward(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.configNominalOutputReverse(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.configPeakOutputForward(1, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	talon.configPeakOutputReverse(-1, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	
	talon.configNeutralDeadband(0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	
	talon.configAllowableClosedloopError(0, 0, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	
  }
  
  public static void configUpdateRate(TalonSRX talon) {
	talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10, DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
	// need to check documentation for default values.
	// see pg. 89 for example usage.
	// pgs. 131-134 have more in-depth info.
  }
  
  public static void checkBatteryReadings(TalonSRX talon) {
	// see pg. 105-107
	StickyFaults stickyFaults = new StickyFaults();
	Faults nonStickyFaults = new Faults();
	
	talon.getStickyFaults(stickyFaults);
	talon.getFaults(nonStickyFaults);
	
	talon.clearStickyFaults(DEFAULT_TALON_FUNCTION_TIMEOUT_MS);
  }
  
  public TalonSRX createMasterTalon(int id) {
	TalonSRX returnVal = new TalonSRX(id);
	return returnVal;
  }
  
  public TalonSRX createFollowerTalon(int followerId, IMotorController master) {
	TalonSRX returnVal = new TalonSRX(followerId);
	returnVal.follow(master);
	return returnVal;
  }
  
  /* Notes on other features:
   * 
   * 1) one talon can follow the output of another by using
   * followerTalon.follow(masterTalon);
   * 
   * 
   */
  
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
