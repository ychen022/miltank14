package sunkern;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class FighterRobot extends BaseRobot {
    boolean knowCoarseMapSize;
    boolean knowCoarseMap;
    boolean commandReceived;
    int commandAge;
    MapLocation bossSaid;
    MapLocation iSay;
    int bigSize;

    public FighterRobot(RobotController myRC) {
        super(myRC);
    }

    @Override
    public void run() throws GameActionException {
        if (!knowCoarseMapSize){
            bigSize = rc.readBroadcast(2001);
        }else if (!knowCoarseMap){
            String s1 = Integer.toString(rc.readBroadcast(2003));
            if (bigSize==3){
                navi.parseMap(new String[]{s1});
            }
            else if (bigSize==4){
                String s2 = Integer.toString(rc.readBroadcast(2004));
                navi.parseMap(new String[]{s1,s2});
            }else if (bigSize==5){
                String s2 = Integer.toString(rc.readBroadcast(2004));
                String s3 = Integer.toString(rc.readBroadcast(2004));
                navi.parseMap(new String[]{s1,s2,s3});
            }else if (bigSize==6){
                String s2 = Integer.toString(rc.readBroadcast(2004));
                String s3 = Integer.toString(rc.readBroadcast(2005));
                String s4 = Integer.toString(rc.readBroadcast(2006));
                navi.parseMap(new String[]{s1,s2,s3,s4});
            }
        }
    }

}
