package org.usfirst.frc.team1787.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Winch {
  
  // Talons
  private final int WINCH_MOTOR_1_TALON_ID = 4;
  private final int WINCH_MOTOR_2_TALON_ID = 10;
  private WPI_TalonSRX winchMotor1 = new WPI_TalonSRX(WINCH_MOTOR_1_TALON_ID);
  private WPI_TalonSRX winchMotor2 = new WPI_TalonSRX(WINCH_MOTOR_2_TALON_ID);
  
  public final double DEFAULT_CLIMB_SPEED = 1.0;
  public final double DEFAULT_DECEND_SPEED = -0.5;
  
  // Singleton Instance
  private static final Winch instance = new Winch();
  
  private Winch() {
	winchMotor1.setInverted(true);
  }
  
  /**
   * @param moveValue Positive values for going up, negative values for going down.
   */
  public void spin(double moveValue) {
    winchMotor1.set(moveValue);
    winchMotor2.set(moveValue);
  }

  public void stop() {
    winchMotor1.set(0);
    winchMotor2.set(0);
  }
  
  public static Winch getInstance() {
    return instance;
  }
}
