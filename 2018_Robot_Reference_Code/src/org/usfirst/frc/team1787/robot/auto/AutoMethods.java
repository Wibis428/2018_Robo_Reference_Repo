package org.usfirst.frc.team1787.robot.auto;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoMethods {

  private SendableChooser<Integer> autoChooser = new SendableChooser<Integer>();
  private int selectedAuto;
  
  // Keep track of the current action being performed in the selected routine
  private int currStage = 0;
  private boolean currStageComplete = false;
  
  private static AutoMethods instance = new AutoMethods();

  private AutoMethods() {
    // Add options to chooser
    autoChooser.addDefault("auto1", 1);
    autoChooser.addObject("auto2", 2);
    autoChooser.addObject("auto3", 3);
    
    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  public void updateSelectedAutoRoutine() {
    selectedAuto = autoChooser.getSelected();
  }

  public void runSelectedAutoRoutine() {
    if (selectedAuto == 1) {
      auto1();
    } else if (selectedAuto == 2) {
      auto2();
    } else if (selectedAuto == 3) {
      auto3();
    }
  }

  public void auto1() {
	/* The idea:
	 * Have each function return a boolean that indicates
	 * if the requested movement has been completed. 
	 * when that boolean reads true (i.e. the current action is complete),
	 * 1) zero sensors for the next stage
	 * 2) increment currStage counter
	 * 3) reset the currStageComplete boolean. 
	 * */
	
    if (currStage == 0) {
      // currStageComplete = driveTrain.driveDistance(5);
    } else if (currStage == 1) {
      // currStageComplete = driveTrain.turnDegrees(60);
    } else if (currStage == 2) {
      // currStageComplete = drivetrain.driveDistance(2);
    } else if (currStage == 4) {
      // currStageComplete = shooter.ejectCube();
    }
    
    if (currStageComplete) {
      zeroAllSensors();
      currStage++;
      currStageComplete = false;
    }
  }
  
  public void auto2() {
    
  }
  
  public void auto3() {
    
  }
  
  public void zeroAllSensors() {
    
  }
  
  public void reset() {
	zeroAllSensors();
	currStage = 0;
	currStageComplete = false;
  }
  
  public static AutoMethods getInstance() {
    return instance;
  }
}
