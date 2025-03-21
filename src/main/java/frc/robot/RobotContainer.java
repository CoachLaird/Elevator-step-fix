// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.Endeffector;
import edu.wpi.first.cameraserver.CameraServer;

public class RobotContainer {
    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second
                                                                                      // max angular velocity

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController joystick = new CommandXboxController(0);
    private final CommandXboxController joystickOperator = new CommandXboxController(1);

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
    private final Endeffector m_endeffector;
    private final ElevatorSubsystem m_elevatorSubsystem;

    /* Path follower */
    private final SendableChooser<Command> autoChooser;

    public RobotContainer() {
        
        m_endeffector = new Endeffector();
        m_elevatorSubsystem = new ElevatorSubsystem();

        NamedCommands.registerCommand("HomePosition", new InstantCommand(
                () -> m_elevatorSubsystem.ElevatorToSetpoint(0)));
        NamedCommands.registerCommand("Elavator L2", new InstantCommand(
                () -> m_elevatorSubsystem.ElevatorToSetpoint(1)));
        NamedCommands.registerCommand("Shoot", new InstantCommand(
                () -> m_endeffector.RunEndeffector(0.5)));
        NamedCommands.registerCommand("Stop Shoot", new InstantCommand(
                () -> m_endeffector.RunEndeffector(0.0)));
        NamedCommands.registerCommand("Score Elevator L3", new InstantCommand(
                () -> m_elevatorSubsystem.ElevatorToSetpoint(3)));
        autoChooser = AutoBuilder.buildAutoChooser("Tests");
        SmartDashboard.putData("Auto Mode", autoChooser);
        import edu.wpi.first.cameraserver.CameraServer;
        configureBindings();

    }

    private void configureBindings() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
                // Drivetrain will execute this command periodically
                drivetrain.applyRequest(() -> drive.withVelocityX(-joystick.getLeftY() * MaxSpeed)// Drive forward with
                                                                                                   // negative Y
                                                                                                   // (forward)
                        .withVelocityY(-joystick.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                        .withRotationalRate(-joystick.getRightX() * MaxAngularRate) // Drive counterclockwise with
                                                                                    // negative X (left)
                ));

        joystick.a().whileTrue(drivetrain.applyRequest(() -> brake));
        joystick.b().whileTrue(drivetrain.applyRequest(
                () -> point.withModuleDirection(new Rotation2d(-joystick.getLeftY(), -joystick.getLeftX()))));

        // Run SysId routines when holding back/start and X/Y.
        // Note that each routine should be run exactly once in a single log.
        joystick.back().and(joystick.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        joystick.back().and(joystick.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        joystick.start().and(joystick.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        joystick.start().and(joystick.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        // reset the field-centric heading on left bumper press
        joystick.leftBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

        drivetrain.registerTelemetry(logger::telemeterize);

        joystickOperator.a()
                .onTrue(new InstantCommand(
                        () -> m_elevatorSubsystem.ElevatorToSetpoint(0)));
        joystickOperator.b()
                .onTrue(new InstantCommand(
                        () -> m_elevatorSubsystem.ElevatorToSetpoint(1)));

        joystickOperator.y()
                .onTrue(new InstantCommand(
                        () -> m_elevatorSubsystem.ElevatorToSetpoint(2)));
       

        joystickOperator.leftBumper()
                .onTrue(new InstantCommand(
                        () -> m_endeffector.RunEndeffector(0.5)))
                .onFalse(new InstantCommand(
                        () -> m_endeffector.StopEndeffector()));
        joystickOperator.rightBumper()
                .onTrue(new InstantCommand(
                        () -> m_endeffector.RunEndeffector(-0.5)))
                .onFalse(new InstantCommand(
                        () -> m_endeffector.StopEndeffector()));

        // Add joystick controls for elevator
       drivetrain.setDefaultCommand(
        drivetrain.applyRequest(() -> {
             double rightY = joystickOperator.getRightY();
        double precisionFactor = 0.1; // Adjust this factor to control the speed
        if (Math.abs(rightY) > 0.1) { // Add deadband to avoid unintentional movements
            m_elevatorSubsystem.ElevatorToSetpoint(m_elevatorSubsystem.getCurrentPosition() + (rightY * precisionFactor));
        }
    })
);
        
                }

    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
