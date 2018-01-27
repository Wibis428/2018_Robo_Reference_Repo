package org.usfirst.frc.team1787.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret {
  
  // Talon
  private final int TURRET_TALON_ID = 1;
  private WPI_TalonSRX turretMotor = new WPI_TalonSRX(TURRET_TALON_ID);

  // Gyro
  private final int GYRO_ANALOG_PORT = 0;
  private AnalogGyro gyro = new AnalogGyro(GYRO_ANALOG_PORT);

  // PID Controller Gains / Configuration Preferences
  private final double PID_KP = 0;
  private final double PID_KI = 0;
  private final double PID_KD = 0;
  private final double PID_KF = 0;
  private final double PID_ERROR_TOLERENCE = 0; // turret PID error is measured in [degrees]
  private PIDController turretController = new PIDController(PID_KP, PID_KI, PID_KD, PID_KF, 
		  										 gyro, turretMotor, PIDController.kDefaultPeriod);
  
  // Singleton Instance
  private static Turret instance;
  
  private Turret() {
	// init gyro
	/* The gyro is a sensor that can measure rate of rotation.
	 * It outputs a voltage that's directly proportional to the rate of rotation.
	 * Expressed mathematically: 
	 * 
	 * V = k * "omega"
	 * 
	 * where "k" is a constant with units (volts / (degrees / second)).
	 * It's that constant that's being set below. The default value from
	 * the AnalogGyro class is currently being used, but it's currently unknown to me
	 * whether or not this default value is actually correct for our gyro.
	 * 
	 * See the article on gyros in the 2018 FRC control system for more info.
	 * 
	 * Side Note: yes, the rate of rotation (omega) is measured in (degrees / second), not (radians / second)
	 */
    gyro.setSensitivity(0.007);
	
	// config PID controller
    turretController.setAbsoluteTolerance(PID_ERROR_TOLERENCE);
  }
  
  public PIDController getPIDController() {
    return turretController;
  }
  
  public AnalogGyro getGyro() {
    return gyro;
  }
  
  public void zeroSensors() {
    gyro.reset();
  }

  public void manualControl(double moveValue) {
    if (turretController.isEnabled()) {
      turretController.reset();
    }
    turretMotor.set(moveValue);
  }
  
  public void stop() {
    manualControl(0);
  }

  public void publishDataToSmartDash() {
	// Talon
	SmartDashboard.putData("Turret Motor Output", turretMotor);
	
	// Gyro
	SmartDashboard.putData("Gyro", gyro);
	
	// PID controller
	SmartDashboard.putData("Turret PID Controller", turretController);
    SmartDashboard.putNumber("Turret PID Error", turretController.getError());
    SmartDashboard.putBoolean("Turret PID On Target", turretController.onTarget());
  }
  
  public static Turret getInstance() {
	if (instance == null) {
      instance = new Turret();
	}
    return instance;
  }
}
