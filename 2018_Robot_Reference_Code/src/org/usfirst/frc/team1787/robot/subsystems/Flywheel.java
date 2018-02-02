package org.usfirst.frc.team1787.robot.subsystems;

import org.usfirst.frc.team1787.robot.utils.UnitConverter;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Flywheel {
  
  // Talon
  private final int FLYWHEEL_TALON_ID = 5;
  private WPI_TalonSRX flywheelMotor = new WPI_TalonSRX(FLYWHEEL_TALON_ID);
  
  // Encoder
  private final int ENCODER_A_CHANNEL = 6;
  private final int ENCODER_B_CHANNEL = 7;
  private Encoder flywheelEncoder = new Encoder(ENCODER_A_CHANNEL, ENCODER_B_CHANNEL);
  
  //2048 encoder ticks per encoder revolution, and the encoder is mounted on the same axle as the flywheel
  private final double FLYWHEEL_ENCODER_REVOLUTIONS_PER_PULSE = 1.0 / 2048; // 2048 * 4 raw ticks

  // PID Control Loop Gains / Preferences
  private final double PID_KP = 0;
  private final double PID_KI = 0;
  private final double PID_KD = 0;
  private final double PID_KF = 1.0 / 80; // (max setpoint is 80 revolutions / second)
  private final double PID_ERROR_TOLERANCE = 0;
  private PIDController flywheelController = new PIDController(PID_KP, PID_KI, PID_KD, PID_KF,
		  										   flywheelEncoder, flywheelMotor, PIDController.kDefaultPeriod);

  // Flywheel Geometry (in meters) (note that flywheel has 4.875 inch diameter)
  private final double FLYWHEEL_RADIUS = UnitConverter.inchesToMeters(4.875/2.0);
  private final double FLYWHEEL_CIRCUMFERENCE = 2 * Math.PI * FLYWHEEL_RADIUS;
  private final double EXIT_ANGLE_DEGREES = 1; // <- PLACEHOLDER
  private final double EXIT_ANGLE_RADIANS = Math.toRadians(EXIT_ANGLE_DEGREES);
  
  // Singleton Instance
  private static Flywheel instance;
  
  private Flywheel() {
    // Configure Talon
	flywheelMotor.setNeutralMode(NeutralMode.Coast);

    // Configure Encoder
	flywheelEncoder.setReverseDirection(true);
	flywheelEncoder.setDistancePerPulse(FLYWHEEL_ENCODER_REVOLUTIONS_PER_PULSE);
    flywheelEncoder.setPIDSourceType(PIDSourceType.kRate);
    
    // Configure PID Controller
    flywheelController.setAbsoluteTolerance(PID_ERROR_TOLERANCE);
  }
  
  // PID Controller Methods
  
  public PIDController getPIDController() {
    return flywheelController;
  }
  
  /**
   * Sets the flywheel to the appropriate speed for the given distance
   * @param distanceX The horizontal distance to the target in meters
   * @param distanceY The vertical distance to the target in meters
   */
  public void setCalculatedSetpoint(double distanceX, double distanceY) {
    /* TO DO: figure out how to determine the appropriate
     * flywheel speed for a given horizontal distance and vertical distance
     * to the target. 
     * 
     * Because it was determined from standard kinematic equations, 
     * the method that's commented out below should work in a physics friendly world 
     * (i.e. no air resistance, no holes or spin on the ball, the ball rolls without slipping in the turret), 
     * but that's not the world we live in. Still, it probably wouldn't be a bad idea to try it out 
     * and see how close it is. If you're going to try it out, make sure to comment out the last line of this method
     * first. Otherwise, the calculation will be overridden.
     * */
    
    double numeratorSquared = (-9.81 / 2) * Math.pow(distanceX / Math.cos(EXIT_ANGLE_RADIANS), 2);
    double denominatorSquared = distanceY - (distanceX * Math.tan(EXIT_ANGLE_RADIANS));
    double requiredExitVelocity = Math.sqrt(numeratorSquared / denominatorSquared);
    
    /* Theoretically, the translational velocity of the ball will be half of the tangential velocity of the edge of the flywheel.
     * Therefore, for the ball to achieve the requiredExitVelocity, the edge of the flywheel must be moving twice as fast. */
    // (Meters / Second) * (1 Revolution / CIRCUMFERENCE meters) = Revolutions / Second
    double calculatedSetpoint = (2 * requiredExitVelocity * (1.0 / FLYWHEEL_CIRCUMFERENCE));
    flywheelController.setSetpoint(calculatedSetpoint);
    
    // Ignore the calculated setpoint for now, because it needs to be tested.
    flywheelController.setSetpoint(distanceX);
  }
  
  // Encoder Methods
  
  public void zeroSensors() {
    flywheelEncoder.reset();
  }
  
  public Encoder getEncoder() {
    return flywheelEncoder;
  }
  
  // Other Methods
  
  public void manualControl(double moveValue) {
    if (flywheelController.isEnabled()) {
      flywheelController.reset();
    }
    flywheelMotor.set(moveValue);
  }
  
  public void stop() {
    manualControl(0);
  }

  public void publishDataToSmartDash() {
	// Talon
	SmartDashboard.putData("Flywheel Motor Output", flywheelMotor);
	
	// Encoder
	SmartDashboard.putData("Flywheel Encoder", flywheelEncoder);
	SmartDashboard.putNumber("Flywheel Encoder Ticks", flywheelEncoder.getRaw());
	
	// PID Controller
	SmartDashboard.putData("Flywheel PID Controller", flywheelController);
    SmartDashboard.putNumber("Flywheel PID Error", flywheelController.getError());
    SmartDashboard.putBoolean("Flywheel PID On Target", flywheelController.onTarget());
  }
  
  public static Flywheel getInstance() {
	if (instance == null) {
      instance = new Flywheel();
	}
    return instance;
  }
}