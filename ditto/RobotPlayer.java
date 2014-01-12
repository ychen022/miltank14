package ditto;

import java.util.Random;

import battlecode.common.RobotController;

public class RobotPlayer {
	static Random rand;
	
	public static void run(RobotController rc) {
        BaseRobot robot = null;
        
        try {
            switch(rc.getType()) {
            case HQ:
                robot = new HQRobot(rc);
                break;
            case SOLDIER:
                robot = new FighterRobot(rc);
                break;
            case NOISETOWER:
                robot = new TowerRobot(rc);
                break;
            case PASTR:
                robot = new PASTRRobot(rc);
                break;
            default:
                break;
            }
            robot.loop();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
