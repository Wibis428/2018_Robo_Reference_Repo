package org.usfirst.frc.team1787.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Winch {
  
  // Talons
  private final int WINCH_MASTER_TALON_ID = 4;
  private final int WINCH_FOLLOWER_TALON_ID = 10;
  private WPI_TalonSRX winchMasterMotor = new WPI_TalonSRX(WINCH_MASTER_TALON_ID);
  private WPI_TalonSRX winchFollowerMotor = new WPI_TalonSRX(WINCH_FOLLOWER_TALON_ID);
  
  public final double DEFAULT_CLIMB_SPEED = 1.0;
  public final double DEFAULT_DECEND_SPEED = -0.5;
  
  // Singleton Instance
  private static Winch instance;
  
  private Winch() {
	// Talons
	winchMasterMotor.setInverted(true);
	
	winchFollowerMotor.follow(winchMasterMotor);
  }
  
  /**
   * @param moveValue Positive values for going up, negative values for going down.
   */
  public void spin(double moveValue) {
    winchMasterMotor.set(moveValue);
  }

  public void stop() {
    winchMasterMotor.set(0);
  }
  
  public void publishDataToSmartDash() {
	// Talons
	SmartDashboard.putData("Winch Motor Output", winchMasterMotor);
  }
  
  public static Winch getInstance() {
	if (instance == null) {
      instance = new Winch();
	}
    return instance;
  }
}
