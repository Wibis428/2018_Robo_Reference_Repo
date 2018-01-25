package org.usfirst.frc.team1787.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake {

  // Intake Solenoid (The pistons that move the intake arm are controlled by a double solenoid)
  private final int SOLENOID_FORWARD_CHANNEL = 1;
  private final int SOLENOID_REVERSE_CHANNEL = 2;
  private DoubleSolenoid intakeSolenoid = new DoubleSolenoid(SOLENOID_FORWARD_CHANNEL, 
                                                             SOLENOID_REVERSE_CHANNEL);
  // these values determined through testing
  public final DoubleSolenoid.Value DEPLOY = DoubleSolenoid.Value.kReverse;
  public final DoubleSolenoid.Value RETRACT = DoubleSolenoid.Value.kForward;
  
  // Intake Wheels (the wheels on the intake that spin to pickup balls)
  private final int INTAKE_TALON_ID = 3;
  private WPI_TalonSRX intakeMotor = new WPI_TalonSRX(INTAKE_TALON_ID);
  
  public final double DEFAULT_INTAKE_SPEED = 0.8;
  
  // Singleton Instance
  private static Intake instance;

  private Intake() {
	// config talon
	intakeMotor.setInverted(true);
  }
  
  /**
   * @param desiredState intended to be one of either pickupArm.DEPLOY
   * or pickupArm.RETRACT
   */
  public void moveArm(DoubleSolenoid.Value desiredState) {
    intakeSolenoid.set(desiredState);
  }
  
  /**
   * @param moveValue positive values for intaking balls,
   * negative values for expelling balls.
   */
  public void spinIntake(double moveValue) {
    intakeMotor.set(moveValue);
  }
  
  public void stop() {
    intakeMotor.set(0);
  }
  
  public void publishDataToSmartDash() {
	// Talon
	SmartDashboard.putData("Intake Motor Output", intakeMotor);
	
	// Solenoid
	SmartDashboard.putBoolean("Intake Deployed", intakeSolenoid.get() == this.DEPLOY);
  }
  
  public static Intake getInstance() {
	if (instance == null) {
      instance = new Intake();
	}
    return instance;
  }
}
