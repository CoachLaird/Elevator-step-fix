// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs;
import frc.robot.Constants.ElevatorConstants;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
public class ElevatorSubsystem extends SubsystemBase {
  /** Creates a new driversubsystem. */

 private final SparkMax rightSparkMax;
 private final SparkMax leftSparkMax;
 public final RelativeEncoder rightEncoder;
 public final RelativeEncoder leftEncoder;
 public final SparkClosedLoopController rightPIDController;
 public final SparkClosedLoopController leftPIDController;

 private static final double STEP_DISTANCE = 0.1; // Distance to move the elevator per step the elevator in meters
 private double currentHeight = 0.0; // Current height of the elevator in meters

 public ElevatorSubsystem() {
  rightSparkMax = new SparkMax(1, MotorType.kBrushless);
  leftSparkMax = new SparkMax(4, MotorType.kBrushless);

  rightEncoder = rightSparkMax.getEncoder();
  leftEncoder = leftSparkMax.getEncoder();
  rightPIDController = rightSparkMax.getClosedLoopController();
  leftPIDController = leftSparkMax.getClosedLoopController();

  rightSparkMax.configure(Configs.Elevator.rightElevatorConfig, ResetMode.kResetSafeParameters,
        PersistMode.kPersistParameters);

  leftSparkMax.configure(Configs.Elevator.leftElevatorConfig, ResetMode.kResetSafeParameters,
        PersistMode.kPersistParameters);
 }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber("Elevator Position", rightEncoder.getPosition());
  }

  public void ElevatorToSetpoint(int goalSetpoint) {
    rightPIDController.setReference(ElevatorConstants.elevatorPos[goalSetpoint], SparkMax.ControlType.kPosition, ClosedLoopSlot.kSlot0);
  }

  public void stepUp() {
    currentHeight += STEP_DISTANCE;
    moveToHeight(currentHeight);

    // Logic to step the elevator up
  }

  public void stepDown() {
    currentHeight -= STEP_DISTANCE;
    moveToHeight(currentHeight);    // Logic to step the elevator down
  }
  private void moveToHeight(double height) {
    rightPIDController.setReference(height, SparkMax.ControlType.kPosition, ClosedLoopSlot.kSlot0);
    leftPIDController.setReference(height, SparkMax.ControlType.kPosition, ClosedLoopSlot.kSlot0);
} 
}
