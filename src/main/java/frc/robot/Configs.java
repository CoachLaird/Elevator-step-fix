package frc.robot;

import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;


public final class Configs {

public static final class Elevator {

    public static final SparkMaxConfig rightElevatorConfig = new SparkMaxConfig();
    public static final SparkMaxConfig leftElevatorConfig = new SparkMaxConfig();    

    static {
        rightElevatorConfig
            .closedLoopRampRate(.05)
            .inverted(true)
            .smartCurrentLimit(40);
        rightElevatorConfig.closedLoop
            .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
            .pidf(.25, 0, 0, 0)
            .outputRange(-.5, 1)
            .positionWrappingEnabled(false);
    
        
        leftElevatorConfig
        .idleMode(IdleMode.kCoast)
        .smartCurrentLimit(40)
        .closedLoopRampRate(.05)
        .follow(1, true);
        
        leftElevatorConfig.closedLoop
        .pidf(.25, 0, 0, 0)
        .outputRange(-.5, 1)
        .positionWrappingEnabled(false);
        
        
    }
}    
public static final class Endeffector {
    
    public static final SparkMaxConfig EndeffectorConfig = new SparkMaxConfig();

    static {
        EndeffectorConfig
        .idleMode(IdleMode.kCoast)
        .smartCurrentLimit(40)
        .inverted(false);

       EndeffectorConfig.closedLoop
            .pidf(1, 0, 0, 0)
            .outputRange(-1, 1);
    }
}
}
