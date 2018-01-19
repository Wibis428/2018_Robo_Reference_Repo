package org.usfirst.frc.team1787.robot.subsystems;

import org.usfirst.frc.team1787.robot.utils.CustomPIDController;

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
  private CustomPIDController turretController = new CustomPIDController(PID_KP, PID_KI, PID_KD, PID_KF, 
		  										 gyro, turretMotor, PIDController.kDefaultPeriod);
  
  // Singleton Instance
  private static final Turret instance = new Turret();
  
  private Turret() {
    // Configure PID Controller
    turretController.setAbsoluteTolerance(PID_ERROR_TOLERENCE);
    
    gyro.calibrate();
  }
  
  public CustomPIDController getPIDController() {
    return turretController;
  }
  
  public AnalogGyro getGyro() {
    return gyro;
  }
  
  public void zeroSensors() {
    gyro.reset();
  }

  public void manualControl(double value) {
    if (turretController.isEnabled()) {
      turretController.reset();
    }
    turretMotor.set(value);
  }
  
  public void stop() {
    manualControl(0);
  }

  public void publishDataToSmartDash() {
    SmartDashboard.putBoolean("Turret PID Enabled", turretController.isEnabled());
    SmartDashboard.putNumber("turretAngle", gyro.getAngle());
    SmartDashboard.putNumber("turretError", turretController.getError());
    SmartDashboard.putNumber("turretMotorOutput", turretController.get());
    SmartDashboard.putBoolean("Turret On Target", turretController.onTarget());
  }
  
  public static Turret getInstance() {
    return instance;
  }
}
