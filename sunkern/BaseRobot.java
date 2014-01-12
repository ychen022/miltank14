package sunkern;

import battlecode.common.RobotController;

public abstract class BaseRobot {
    public RobotController rc;
    public int id;

    public BaseRobot(RobotController myRC) {
        rc = myRC;
        id = rc.getRobot().getID();
    }

    abstract public void run();

    public void loop() {
        while (true) {
            try {
                run();
            } catch (Exception e) {
                // Deal with exception
            }
            rc.yield();
        }
    }

}
