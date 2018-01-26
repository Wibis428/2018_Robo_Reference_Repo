package org.usfirst.frc.team1787.robot;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.usfirst.frc.team1787.robot.auto.AutoMethods;
import org.usfirst.frc.team1787.robot.subsystems.DriveTrain;
import org.usfirst.frc.team1787.robot.subsystems.Flywheel;
import org.usfirst.frc.team1787.robot.subsystems.Intake;
import org.usfirst.frc.team1787.robot.subsystems.Shooter;
import org.usfirst.frc.team1787.robot.subsystems.Turret;
import org.usfirst.frc.team1787.robot.subsystems.Winch;
import org.usfirst.frc.team1787.robot.vision.CameraController;
import org.usfirst.frc.team1787.robot.vision.ImageProcessor;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
  // Don't Ask
  protected int farfar37;
  
  // Controls
  private final int RIGHT_JOYSTICK_ID = 0;
  private final int LEFT_JOYSTICK_ID = 1;
  private Joystick rightStick = new Joystick(RIGHT_JOYSTICK_ID);
  private Joystick leftStick = new Joystick(LEFT_JOYSTICK_ID);
  private final int JOYSTICK_TWIST_AXIS = 2;
  private final int JOYSTICK_SLIDER_AXIS = 3;
  
  // Button Map
  private final int DEPLOY_ARM_BUTTON = 3;
  private final int RETRACT_ARM_BUTTON = 4;
  private final int INTAKE_BUTTON = 1;
  private final int EXPELL_BUTTON = 2;
  
  private final int WINCH_CLIMB_BUTTON = 8;

  private final int TOGGLE_SHOOTER_CONTROL_BUTTON = 2;
  
  private final int TOGGLE_ACTIVE_CAMERA_BUTTON = 10;
  
  private final int TOGGLE_TUNING_MODE_ENABLED_BUTTON = 14;
  private final int CHANGE_CURRENT_TUNING_MODE_BUTTON = 15;
  
  // Control State Variables
  
  /** Determines which mode the shooter is in.
   * 0 = manual control,
   * 1 = automatic target tracking & shooting */
  private int shooterControlMode = 0;
  
  /** If tuning mode is active, this variable 
   * determines what exactly is being tuned */
  private int tuningMode = 0;
  private boolean tuningModeActive = false;
  
  // the frame published to the SmartDash stream
  private Mat outputFrame = new Mat();
  // where to get the image to publish to the SmartDash
  private int selectedStreamingSource = 0;
  
  // Instances of Subsystems
  private DriveTrain driveTrain = DriveTrain.getInstance();
  private Intake pickupArm = Intake.getInstance();
  private Shooter shooter = Shooter.getInstance();
  private Winch winch = Winch.getInstance();
  private CameraController camController = CameraController.getInstance();
  private ImageProcessor imgProcessor = ImageProcessor.getInstance();
  private AutoMethods auto = AutoMethods.getInstance();
  
  /* These subsystems are normally controlled collectively through the Shooter class,
   * but they are included here individually to tune PID loops for each component
   * separately. */
  private Flywheel flywheel = Flywheel.getInstance();
  private Turret turret = Turret.getInstance();
  
  // Preferences (interface used to get values from the SmartDash)
  Preferences prefs = Preferences.getInstance();
  
  /* ----------------------------------------------------------------
   * Member variables end here; only functions below this point!
   * ---------------------------------------------------------------- */
  
  /** This function is run once when the robot is first started up
   *  and should be used  for any initialization code. */
  @Override
  public void robotInit() {
	  // Default Period is 0.02 seconds per loop.
	  this.setPeriod(0.02);
	  
	  /*
	   * TODO:
	   * 4) finish going through all talon config features
	   * 5) practice working with a sensor that's attatched to the talon
	   * 6) figure out if it's possible to use a gyro with a talon / investigate Pidgeon IMU
	   * 7) Test out using the follow feature with talons
	   * 8) See what code structure would be like if you have the talons control pretty much everything
	   * 9) actually use TalonConfigurer everywhere.
	   * 
	   * 9) Review multi-threading for img processing
	   * 10) review img processing code and see if cleanup is necessary
	   * 
	   * 11) Review what's up with the new network tables protocols
	   * 12) Finish setting up the dashboard so that all values show
	   * 
	   * 13) make arcadeDrive consistent with what I learned in robotics + fix documentation.
	   * 
	   * 14) double check it's ok to remove all instances of CustomPIDController.
	   * 15) Replace all instances of CustomPIDController if this is the case.
	   */
  }
  
  /* While the robot is on, it can be in one of the following modes at a time:
   * 1) teleop (driver control from driver station)
   * 2) autonomous (no driver control, fully autonomous)
   * 3) test mode (intended to be helpful for testing, but I find it annoying)
   * 4) disabled (robot does nothing)
   * 
   * Each of these modes have an associated init() function
   * and an associated periodic() function. The init() function is
   * run once upon entering a given mode, then the periodic() function is looped
   * until a different mode is entered.
   * 
   * For example: Changing the current mode from 
   * disabled to teleop will cause teleopInit() to be run once, and then teleopPeriodic()
   * to be looped until a different mode is entered.
   */
  
  
  
  public void teleopInit() {
    
  }

  public void teleopPeriodic() {
    // Driving
    driveTrain.arcadeDrive(-rightStick.getY(), rightStick.getX());
	  //driveTrain.tryCurveDrive(rightStick.getY(), -rightStick.getX(), rightStick.getRawButton(EXPELL_BUTTON));

    // Gear Shifter
    if (rightStick.getRawAxis(JOYSTICK_SLIDER_AXIS) < 0) {
      driveTrain.setGear(driveTrain.HIGH_GEAR);
    } else {
      driveTrain.setGear(driveTrain.LOW_GEAR);
    }
    
    driveTrain.publishDataToSmartDash();
    
    // Pickup Arm
    if (rightStick.getRawButton(DEPLOY_ARM_BUTTON)) {
      pickupArm.moveArm(pickupArm.DEPLOY);
    } else if (rightStick.getRawButton(RETRACT_ARM_BUTTON)) {
      pickupArm.moveArm(pickupArm.RETRACT);
    }
    
    // Pickup Wheels
    if (rightStick.getRawButton(INTAKE_BUTTON)) {
      pickupArm.spinIntake(pickupArm.DEFAULT_INTAKE_SPEED);
    } else if (rightStick.getRawButton(EXPELL_BUTTON)) {
      //pickupArm.spinIntake(-1 * pickupArm.DEFAULT_INTAKE_SPEED);
    } else {
      pickupArm.spinIntake(0);
    }
    
    // Winch
    if (rightStick.getRawButton(WINCH_CLIMB_BUTTON)) {
      winch.spin(winch.DEFAULT_CLIMB_SPEED);
    } else {
      winch.spin(0);
    }
    
    // Tuning Mode
    if (leftStick.getRawButtonPressed(TOGGLE_TUNING_MODE_ENABLED_BUTTON)) {
      tuningModeActive = !tuningModeActive;
      shooter.stop();
    }
    SmartDashboard.putBoolean("Tuning Mode Active", tuningModeActive);
    
    if (tuningModeActive) {
      runTuningCode();
      return;
    }
    
    // Shooter
    if (leftStick.getRawButtonPressed(TOGGLE_SHOOTER_CONTROL_BUTTON)) {
      shooter.stop();
      shooterControlMode = (shooterControlMode + 1) % 2;
    }
    
    /* shooterControlMode = 0 = manual control
     * shooterControlMode = 1 = full auto tracking/shooting */
    if (shooterControlMode == 0) {   
      shooter.manualControl(leftStick);
    } else if (shooterControlMode == 1) {      
      if (!shooter.pidIsEnabled()) {
        shooter.enablePIDControllers();
      }
      shooter.fullAutoShooting();
    }
    
    shooter.publishDataToSmartDash();
    
    // Cameras (note that most img processing code is already called by shooter.fullAutoShooting())
    if (rightStick.getRawButtonPressed(TOGGLE_ACTIVE_CAMERA_BUTTON)) {
      selectedStreamingSource = (selectedStreamingSource + 1) % 4;
    }
    
    if (selectedStreamingSource == 0) {
      camController.getGearCamFrame(outputFrame);
      SmartDashboard.putString("Selected Streaming Source", "gearCam");
    } else if (selectedStreamingSource == 1) {
      camController.getTurretCamFrame(outputFrame);
      SmartDashboard.putString("Selected Streaming Source", "turretCam");
    } else if (selectedStreamingSource == 2) {
      outputFrame = imgProcessor.getOriginalFrame();
      SmartDashboard.putString("Selected Streaming Source", "Processed Img (overlay)");
    } else if (selectedStreamingSource == 3) {
      outputFrame = imgProcessor.getProcessedFrame();
      SmartDashboard.putString("Selected Streaming Source", "Processed Img (no overlay)");
    }
    camController.pushFrameToDash(outputFrame);
    
    imgProcessor.publishDataToSmartDash();
  }
  
  
  
  
  
  public void autonomousInit() {
	    
  }

  public void autonomousPeriodic() {
    
  }
  
  
  
  
  public void disabledInit() {
    shooter.stop();
  }
	  
  public void disabledPeriodic() {
		  
  }
  
  
  
  
  
  public void runTuningCode() {
    /* Note: To have preferences show up in the appropriate shuffleboard widget, 
     * they must first be added to network tables through Outline Viewer */
    
    if (leftStick.getRawButtonPressed(CHANGE_CURRENT_TUNING_MODE_BUTTON)) {
      shooter.stop();
      tuningMode = (tuningMode + 1) % 4;
    }
    
    /* tuningMode = 0 = turret PID tuning
     * tuningMode = 1 = flywheel PID tuning
     * tuningMode = 2 = HSV filter tuning
     * tuningMode = 3 = contour filter tuning */
    if (tuningMode == 0) {
      if (!shooter.pidIsEnabled()) {  
        double turretP = prefs.getDouble("turretP", 0);
        double turretI = prefs.getDouble("turretI", 0);
        double turretD = prefs.getDouble("turretD", 0);
        turret.getPIDController().setPID(turretP, turretI, turretD);
        
        // turret error is in [degrees]
        double turretErrorTolerance = prefs.getDouble("turretErrorTolerance", 0);
        turret.getPIDController().setAbsoluteTolerance(turretErrorTolerance);
        
        turret.getPIDController().enable();
      }
      shooter.trackTarget();
      
    } else if (tuningMode == 1) {
      if (!shooter.pidIsEnabled()) {
        double flywheelP = prefs.getDouble("flywheelP", 0);
        double flywheelI = prefs.getDouble("flywheelI", 0);
        double flywheelD = prefs.getDouble("flywheelD", 0);
        flywheel.getPIDController().setPID(flywheelP, flywheelI, flywheelD);
        
        // flywheel error is in [revolutions/second]
        double flywheelErrorTolerance = prefs.getDouble("flywheelErrorTolerance", 0);
        flywheel.getPIDController().setAbsoluteTolerance(flywheelErrorTolerance);
        
        flywheel.getPIDController().enable();
      }
      double flywheelSetpoint = prefs.getDouble("flywheelSetpoint", 0);
      flywheel.getPIDController().setSetpoint(flywheelSetpoint);
      
    } else if (tuningMode == 2) {
      shooter.manualControl(leftStick);
      
      double hMin = prefs.getDouble("hMin", 0);
      double sMin = prefs.getDouble("sMin", 0);
      double vMin = prefs.getDouble("vMin", 0);
      Scalar minRange = new Scalar(hMin, sMin, vMin);
      
      double hMax = prefs.getDouble("hMax", 180);
      double sMax = prefs.getDouble("sMax", 255);
      double vMax = prefs.getDouble("vMax", 255);
      Scalar maxRange = new Scalar(hMax, sMax, vMax);
      
      Mat result = imgProcessor.getHSVFilter(minRange, maxRange);
      camController.pushFrameToDash(result);
      
    } else if (tuningMode == 3) {
      shooter.manualControl(leftStick);
      
      double minArea = prefs.getDouble("minArea", 0);
      double minShapeScore = prefs.getDouble("minShapeScore", 0);
      double maxShapeScore = prefs.getDouble("maxShapeScore", 2);
      
      Scalar minHsvRange = imgProcessor.DEFAULT_HSV_LOWER_BOUNDS;
      Scalar maxHsvRange = imgProcessor.DEFAULT_HSV_UPPER_BOUNDS;
      Mat result = imgProcessor.getHSVFilter(minHsvRange, maxHsvRange);
      
      ArrayList<MatOfPoint> contours = imgProcessor.findContours(result);
      for (int i = contours.size()-1; i >= 0; i--) {
        boolean passesAreaTest = imgProcessor.passesAreaTest(contours.get(i), minArea);
        boolean passesShapeTest = imgProcessor.passesShapeTest(contours.get(i), minShapeScore, maxShapeScore);
        if (!(passesAreaTest && passesShapeTest)) {
          contours.remove(i);
        }
      }
      
      result = imgProcessor.drawContours(true, contours);
      camController.pushFrameToDash(result);
    }
    
    // Publish all data to smart dash
    shooter.publishDataToSmartDash();
    imgProcessor.publishDataToSmartDash();
  }
  
  
  
  
  
  public void testInit() {
    
  }

  public void testPeriodic() {
    
  }
}