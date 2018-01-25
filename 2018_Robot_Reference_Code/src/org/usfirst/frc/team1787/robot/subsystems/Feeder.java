package org.usfirst.frc.team1787.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Feeder {
  
  // Talons
  private final int TURRET_FEEDER_TALON_ID = 2;
  private WPI_TalonSRX feederMotor = new WPI_TalonSRX(TURRET_FEEDER_TALON_ID);
  
  public final double DEFAULT_FEEDER_SPEED = 0.42;
  
  // Singleton Instance
  private static Feeder instance;

  private Feeder() {
	// Config Talon
	feederMotor.setInverted(true);
  }
  
  /**
   * @param moveValue positive values to run the feeder
   * in the correct direction, negative values to run it in reverse.
   */
  public void spin(double moveValue) {
    feederMotor.set(moveValue);
  }

  public void stop() {
    feederMotor.set(0);
  }
  
  public void publishDataToSmartDash() {
	SmartDashboard.putData("Feeder Motor Output", feederMotor);
  }
  
  public static Feeder getInstance() {
	if (instance == null) {
      instance = new Feeder();
	}
    return instance;
  }
}
