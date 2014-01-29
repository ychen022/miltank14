package sunkern;

import battlecode.common.RobotController;

public abstract class BaseRobot {
    public RobotController rc;
    public int id;
    public Navigator navi;

    public BaseRobot(RobotController myRC) {
        rc = myRC;
        id = rc.getRobot().getID();
        navi = new Navigator(rc);
    }

    abstract public void run() throws Exception;

    public void loop() {
        while (true) {
            try {
                run();
                rc.yield();
            } catch (Exception e) {
                // Deal with exception
            }
        }
    }

}
