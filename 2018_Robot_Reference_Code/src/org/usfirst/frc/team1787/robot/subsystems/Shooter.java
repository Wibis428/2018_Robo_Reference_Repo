package org.usfirst.frc.team1787.robot.subsystems;

import org.usfirst.frc.team1787.robot.vision.ImageProcessor;
import org.usfirst.frc.team1787.robot.vision.Target;

import edu.wpi.first.wpilibj.Joystick;

/**
 * The shooter class is composed of the turret, the flywheel, and the feeder.
 * This class serves to coordinate those mechanisms.
 */
public class Shooter {

  // Sub-Mechanisms
  private Turret turret = Turret.getInstance();
  private Flywheel flywheel = Flywheel.getInstance();
  private Feeder feeder = Feeder.getInstance();
  
  // Vision
  private ImageProcessor imgProcessor = ImageProcessor.getInstance();
  
  // Singleton Instance
  private static Shooter instance;

  private Shooter() {
    // Intentionally left blank; no initialization needed.
  }
  
  public void enablePIDControllers() {
    turret.getPIDController().enable();
    flywheel.getPIDController().enable();
  }
  
  public boolean pidIsEnabled() {
    return turret.getPIDController().isEnabled() ||
           flywheel.getPIDController().isEnabled();
  }
  
  public void fullAutoShooting() {
    trackTarget();
    double horizontalDistanceToTarget = imgProcessor.getCurrentTarget().getHorizontalDistance();
    double verticalDistanceToTarget = Target.TURRET_TO_TARGET_VERTICAL_DISTANCE;
    flywheel.setCalculatedSetpoint(horizontalDistanceToTarget, verticalDistanceToTarget);
    if (turret.getPIDController().onTarget() && flywheel.getPIDController().onTarget()) {
      feeder.spin(feeder.DEFAULT_FEEDER_SPEED);
    } else {
      feeder.stop();
    }
  }
  
  public void trackTarget() {
    imgProcessor.runVisionProcessing();
    double currentAngle = turret.getGyro().getAngle();
    double targetError = imgProcessor.getCurrentTarget().getErrorInDegreesX();
    
    /* currentAngle and targetError are added to get the setpoint because 
     * targetError is relative to where we currently are.
     * For example, if the target is 30 degrees to the right, and the turret angle 
     * currently reads 50 degrees, then the setpoint for the PIDController should be 80,
     * as that will cause the turret to turn 30 degrees to the right starting from where it currently is. */
    turret.getPIDController().setSetpoint(currentAngle + targetError);
  }
  
  public void zeroSensors() {
    turret.zeroSensors();
    flywheel.zeroSensors();
  }
  
  public void manualControl(double turretMoveValue, double flywheelMoveValue, double feederMoveValue) {
    turret.manualControl(turretMoveValue);
    flywheel.manualControl(flywheelMoveValue);
    feeder.spin(feederMoveValue);
  }
  
  public void manualControl(Joystick stick) {
    turret.manualControl(stick.getX());
    flywheel.manualControl(stick.getY());
    if (stick.getTrigger()) {
      feeder.spin(feeder.DEFAULT_FEEDER_SPEED);
    } else {
      feeder.stop();
    }
  }
  
  public void stop() {
    turret.stop();
    flywheel.stop();
    feeder.stop();
  }
  
  public void publishDataToSmartDash() {
    turret.publishDataToSmartDash();
    flywheel.publishDataToSmartDash();
    feeder.publishDataToSmartDash();
  }
  
  public static Shooter getInstance() {
	if (instance == null) {
      instance = new Shooter();
	}
    return instance;
  }
}
