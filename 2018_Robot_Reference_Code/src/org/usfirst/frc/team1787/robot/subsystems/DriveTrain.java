package org.usfirst.frc.team1787.robot.subsystems;

import org.usfirst.frc.team1787.robot.utils.UnitConverter;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {
  
  // Talons
  private final int LEFT_MASTER_TALON_ID = 6;
  private final int LEFT_FOLLOWER_TALON_ID = 7;
  private final int RIGHT_MASTER_TALON_ID = 8;
  private final int RIGHT_FOLLOWER_TALON_ID = 9;
  private WPI_TalonSRX leftMasterMotor = new WPI_TalonSRX(LEFT_MASTER_TALON_ID);
  private WPI_TalonSRX leftFollowerMotor = new WPI_TalonSRX(LEFT_FOLLOWER_TALON_ID);
  private WPI_TalonSRX rightMasterMotor = new WPI_TalonSRX(RIGHT_MASTER_TALON_ID);
  private WPI_TalonSRX rightFollowerMotor = new WPI_TalonSRX(RIGHT_FOLLOWER_TALON_ID);
  
  // Encoders (one for each side of the drivetrain)
  private final int LEFT_ENCODER_A_CHANNEL = 2;
  private final int LEFT_ENCODER_B_CHANNEL = 3;
  private final int RIGHT_ENCODER_A_CHANNEL = 0;
  private final int RIGHT_ENCODER_B_CHANNEL =1;
  private Encoder leftEncoder = new Encoder(LEFT_ENCODER_A_CHANNEL, LEFT_ENCODER_B_CHANNEL);
  private Encoder rightEncoder = new Encoder(RIGHT_ENCODER_A_CHANNEL, RIGHT_ENCODER_B_CHANNEL);
  
  // determined through testing
  private final double METERS_PER_PULSE = UnitConverter.inchesToMeters(0.01249846);

  // Gear Shifter (pneumatic shifter controlled by a solenoid)
  private final int SOLENOID_ID = 0;
  private Solenoid gearShifter = new Solenoid(SOLENOID_ID);
  
  /* The boolean value that corresponds to each gear was determined through testing.
   * These booleans indicate the correct value to use when calling "solenoid.set()". */
  public final boolean HIGH_GEAR = false;
  public final boolean LOW_GEAR = true;
  
  // Singleton Instance
  private static DriveTrain instance;

  public DriveTrain() {
    // Config Talons  
    leftMasterMotor.setInverted(true);
    leftFollowerMotor.setInverted(true);
    
    leftFollowerMotor.follow(leftMasterMotor);
    rightFollowerMotor.follow(rightMasterMotor);
    
    // Config Encoders
    leftEncoder.setDistancePerPulse(METERS_PER_PULSE);
    rightEncoder.setDistancePerPulse(METERS_PER_PULSE);
  }
  
  /* --------------------------------
   * Driving Functions
   * -------------------------------- 
   */
  
  /**
   * y value influences linear motion, 
   * x value influences rate of rotation
   * @param y
   * @param x
   */
  public void arcadeDrive(double y, double x) {
	// use Math.abs() to preserve sign while squaring inputs
	y = y * Math.abs(y);
	x = x * Math.abs(x);	  
     
    double leftOutput = y + x;
	double rightOutput = y - x;
	  
	// limit outputs to [-1, 1]
    leftOutput = Math.max(-1, Math.min(leftOutput, 1));
	rightOutput = Math.max(-1, Math.min(rightOutput, 1));
	  
	setDriveOutputs(leftOutput, rightOutput);
  }
  
  public void setDriveOutputs(double leftOutput, double rightOutput) {
	leftMasterMotor.set(leftOutput);  
	rightMasterMotor.set(rightOutput);
  }
  
  public void stop() {
    setDriveOutputs(0, 0);
  }
  
  /* --------------------------------
   * Encoder Functions
   * --------------------------------
   */
	  
  public Encoder getLeftEncoder() {
    return leftEncoder;
  }
  
  public Encoder getRightEncoder() {
    return rightEncoder;
  }
  
  public void zeroSensors() {
	leftEncoder.reset();
	rightEncoder.reset();
  }
	  
  public double getAvgVelocity() {
    return (leftEncoder.getRate() + rightEncoder.getRate()) / 2.0;
  }
  
  /* --------------------------------
   * Gear Shifter Functions
   * --------------------------------
   */
  
  /**
   * @param desiredGear use either DriveTrain.LOW_GEAR or
   * DriveTrain.HIGH_GEAR (note that these values aren't static,
   * so you'll need to access them from an instance).
   */
  public void setGear(boolean desiredGear) {
    if (gearShifter.get() != desiredGear) {
      gearShifter.set(desiredGear);
    }
  }
  
  public boolean getGear() {
    return gearShifter.get();
  }
  
  /* --------------------------------
   * Other Functions
   * --------------------------------
   */

  public void publishDataToSmartDash() {
    // Talons
	SmartDashboard.putData("Drive Train Output (Left)", leftMasterMotor);
	SmartDashboard.putData("Drive Train Output (Right)", rightMasterMotor);
	
	// Encoders
    SmartDashboard.putNumber("Average Velocity (meters per second)", getAvgVelocity());
    
    SmartDashboard.putData("Left Drive Encoder", leftEncoder);
    SmartDashboard.putNumber("Left Drive Encoder Ticks", leftEncoder.getRaw());
    
    SmartDashboard.putData("Right Drive Encoder", rightEncoder);
    SmartDashboard.putNumber("Right Drive Encoder Ticks", rightEncoder.getRaw());
    
    // Gear Shifter
    if (gearShifter.get() == HIGH_GEAR) {
      SmartDashboard.putString("Current Gear", "High Gear");
    } else {
      SmartDashboard.putString("Current Gear", "Low Gear");
    }
  }
  
  public static DriveTrain getInstance() {
    if (instance == null) {
      instance = new DriveTrain();
    }
    return instance;
  }
}
