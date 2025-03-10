// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configs;

public class Endeffector extends SubsystemBase {
  /** Creates a new Endeffector. */

  private final SparkMax Endeffector;

  public final SparkClosedLoopController PIDController;

  // WORK CODE WORK!

  public Endeffector() {

    Endeffector = new SparkMax(3, MotorType.kBrushed);
    PIDController = Endeffector.getClosedLoopController();

    Endeffector.configure(Configs.Endeffector.EndeffectorConfig,
        ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void RunEndeffector(double speed) {
    Endeffector.set(speed);
  }

  public void StopEndeffector() {
    Endeffector.set(0);
  }

}
