package org.usfirst.frc.team1787.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Feeder {
  
  // Talons
  private final int TURRET_FEEDER_TALON_ID = 2;
  private WPI_TalonSRX feederMotor = new WPI_TalonSRX(TURRET_FEEDER_TALON_ID);
  
  public final double DEFAULT_FEEDER_SPEED = 0.42;
  
  // Singleton Instance
  private static final Feeder instance = new Feeder();

  private Feeder() {
	feederMotor.setInverted(true);
  }
  
  /**
   * @param moveValue positive values to run the feeder
   * in the correct direction, negative values to run it in reverse.
   */
  public void spin(double moveValue) {
    feederMotor.set(moveValue);
    // feeder is mounted such that positive move values will
    // actually make it spin the wrong way, so the input is
    // multiplied by -1 to get the desired behavior in the 
    // method description.
  }

  public void stop() {
    feederMotor.set(0);
  }
  
  public static Feeder getInstance() {
    return instance;
  }
}
