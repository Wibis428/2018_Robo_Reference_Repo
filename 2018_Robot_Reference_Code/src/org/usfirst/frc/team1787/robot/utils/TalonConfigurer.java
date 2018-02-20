package org.usfirst.frc.team1787.robot.utils;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlFrame;
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
	
  public static final int CONFIG_FUNCTION_TIMEOUT_MS = 10;
  
  
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
    
    /* If the motor doesn't spin in the desired direction when positive voltage is applied,
     * then this function can be used to get the correct behavior.
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
     * Warning: currentLimit below 5 amps not recommended. See phoenix documentation for why. 
     */
    talon.configPeakCurrentLimit(0, CONFIG_FUNCTION_TIMEOUT_MS);
    talon.configPeakCurrentDuration(0, CONFIG_FUNCTION_TIMEOUT_MS);
    talon.configContinuousCurrentLimit(0, CONFIG_FUNCTION_TIMEOUT_MS);
    talon.enableCurrentLimit(false);
    
    // Voltage Config Settings
    
    // set the voltage that corresponds to full output (i.e. an output of 1).
    talon.configVoltageCompSaturation(12, CONFIG_FUNCTION_TIMEOUT_MS);
    talon.enableVoltageCompensation(true);
    
    // Motor output is set to 0 if the requested output is less than the given deadband.
    // factory default deadband is 4% (4% = 0.04)
    talon.configNeutralDeadband(0.04, CONFIG_FUNCTION_TIMEOUT_MS);
    
	// config minimum and maximum allowable outputs for each direction
	talon.configNominalOutputForward(0, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.configNominalOutputReverse(0, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.configPeakOutputForward(1, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.configPeakOutputReverse(-1, CONFIG_FUNCTION_TIMEOUT_MS);
    
    /* Note: If requested output is less than the minimum, but within the deadband range,
     * I believe the output will be 0 instead of being promoted to the minimum, but I haven't tested this.
     */
    
    // config the number of samples used in the rolling average to calculate voltage.
    talon.configVoltageMeasurementFilter(32, CONFIG_FUNCTION_TIMEOUT_MS);
    
    /* Allows constraints on the rate at which applied voltage can change.
     * The input is the minimum amount of time (in seconds) required to go from an output of 0 to full output.
     * 
     * Note: followers in a master/follower chain should all be configured 
     * with a ramp rate of 0 (i.e. no restriction on ramp rate), because they will already
     * mimic the output of the master.
     */
    talon.configOpenloopRamp(0, CONFIG_FUNCTION_TIMEOUT_MS);
     
    talon.set(ControlMode.PercentOutput, 0);
  }
  
  public static void configSensorSettings(TalonSRX talon) {
    /* IMPORTANT NOTE: Sensor readings are reported in native units. Velocity reported in native units per 100ms.
     * See CTRE documentation on github (https://github.com/CrossTheRoadElec/Phoenix-Documentation), 
     * as well as the TalonSRX Software Reference Manual for more info.
     */
	  
	/* Another note: if using a Tachometer, see github documentation and the TalonSRX software reference manual, 
	 * as configuring them is slightly different than configuring most other sensors.
	 */
	
	// Limit Switches
    talon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.overrideLimitSwitchesEnable(false); // <- can be used to enable and disable limit switch features on the fly.
	
	// Other Sensors (see TalonSRX software reference manual for full list of supported sensors)
	talon.configSelectedFeedbackSensor(FeedbackDevice.None, 0, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.setSensorPhase(false); // <- use this function to ensure positive sensor readings correspond to positive motor output.
	
	/* If using an analog sensor, you can select whether or not it's continuous.
	 * An example of a continuous sensor is a gyro that will give readings 
	 * above 360 degrees instead of just wrapping back to 0.
	 */
	talon.configSetParameter(ParamEnum.eFeedbackNotContinuous, 0, 0, 0, CONFIG_FUNCTION_TIMEOUT_MS);
	
	// Soft limit can be used to stop a motor if the selectedSensorPosition goes past the given threshold (essentially a software limit switch).
	talon.configForwardSoftLimitThreshold(0, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.configReverseSoftLimitThreshold(0, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.configForwardSoftLimitEnable(false, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.configReverseSoftLimitEnable(false, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.overrideSoftLimitsEnable(false); // <- pass false to disable the soft limits feature. pass true to honor the given configs above.
	
	/* Limit switches can be configured to zero the selectedSensor position when asserted. Pass 1 to the 2nd argument
	 * to enable this feature. Pass 0 to the 2nd argument to disable this feature. Arguments 2 & 3 aren't used.
	 * See pg. 106 for some more info and a similar config for quadrature encoders w/ an index pin.
	 */
	talon.configSetParameter(ParamEnum.eClearPositionOnLimitF, 0, 0, 0, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.configSetParameter(ParamEnum.eClearPositionOnLimitR, 0, 0, 0, CONFIG_FUNCTION_TIMEOUT_MS);
	
	// Config Sensor Measurement Settings
	talon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_100Ms, CONFIG_FUNCTION_TIMEOUT_MS); // <- the "dt" used to calc. velocity from position.
	talon.configVelocityMeasurementWindow(64, CONFIG_FUNCTION_TIMEOUT_MS); // <- num of samples in rolling average.
	
	talon.setSelectedSensorPosition(0, 0, CONFIG_FUNCTION_TIMEOUT_MS);
  }
  
  public static void configClosedLoopSettings(TalonSRX talon) {
	/* Note: See TalonSRX Software Reference Manual for full overview of 
	 * closed loop modes. Only minimal/basic closed loop features are showcased here.
	 */
	
	// Same idea as configOpenLoopRamp()
	// Closed loop ramp should normally just be 0 though, as other values might make closed loop modes jittery.
	talon.configClosedloopRamp(0, CONFIG_FUNCTION_TIMEOUT_MS);
	
	// Config PID loop gains
	// See software reference manual for more info on "profile slots"
	/* Note: Still waiting for response from CTRE to clarify difference between
	 * "pidIdx" and "slotIdx".
	 */
	talon.selectProfileSlot(0, 0);
	talon.config_kP(0, 0, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.config_kI(0, 0, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.config_kD(0, 0, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.config_kF(0, 0, CONFIG_FUNCTION_TIMEOUT_MS);
	
	// When error is less than IntegralZone, error will accumulate up to MaxIntegralAccumulator.
	talon.config_IntegralZone(0, 0, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.configMaxIntegralAccumulator(0, 0, CONFIG_FUNCTION_TIMEOUT_MS);
	
	talon.configAllowableClosedloopError(0, 0, CONFIG_FUNCTION_TIMEOUT_MS);
  }
  
  public static void configUpdateRate(TalonSRX talon) {
	/* Notes on TalonSRX Update Frames can be found in the PDF version of the software reference manual.
	 * Essentially, you can think of a frame as similar to a camera frame, in that it is a snapshot of some data
     * at a particular point in time. These frames are what are sent over the CAN cable to communicate to other devices.
     * The TalonSRX utilize 2 main types of frames:
     * 1) Status Frame - A frame sent from the TalonSRX that contains data about it and sensors connected to it.
     * 2) Control Frame - A frame sent to the TalonSRX that contains data about desired control modes and output values.
     * 
     * Please note that the Status Frame has various subtypes that each hold their own information and are published 
     * at different rates.
     * 
     * See the TalonSRX Software Reference Manual (Section 20) for a full overview
     * of how data is transferred over the CAN bus.
     */
	
	// Note, VictorSPX must use StatusFrame instead of StatusFrameEnhanced.
	talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 20, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 160, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, 160, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, 160, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 160, CONFIG_FUNCTION_TIMEOUT_MS);
	talon.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 160, CONFIG_FUNCTION_TIMEOUT_MS);
	
	talon.setControlFramePeriod(ControlFrame.Control_3_General, 10);
  }
  
  public static void checkBatteryReadings(TalonSRX talon) {
	// See software reference manual for more info on faults and sticky faults.
	StickyFaults stickyFaults = new StickyFaults();
	Faults nonStickyFaults = new Faults();
	
	talon.getStickyFaults(stickyFaults);
	talon.getFaults(nonStickyFaults);
	
	System.out.println("Faults: " + nonStickyFaults.toString());
	System.out.println("Sticky Faults: " + stickyFaults.toString());
	
	talon.clearStickyFaults(CONFIG_FUNCTION_TIMEOUT_MS);
  }
  
  public TalonSRX createMasterTalon(int id) {
	TalonSRX master = new TalonSRX(id);
	return master;
  }
  
  public TalonSRX createFollowerTalon(int followerId, IMotorController master) {
	TalonSRX follower = new TalonSRX(followerId);
	follower.follow(master);
	return follower;
  }
  
}
