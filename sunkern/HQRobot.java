package sunkern;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.engine.GameState;

public class HQRobot extends BaseRobot {
    MapLocation myLoc;
    MapLocation enemyHQLoc;
    boolean firstSoldier;
    boolean mapRecon;
    Robot[] nearbyEnemies;
    boolean shootEm;

    public HQRobot(RobotController myRC) {
        super(myRC);
        myLoc = rc.senseHQLocation();
        enemyHQLoc = rc.senseEnemyHQLocation();
        firstSoldier = false;
        mapRecon = false;
    }

    @Override
    public void run() throws GameActionException {
        if (rc.isActive()) {
            // Spawn a soldier
            if (!firstSoldier) {
                Direction desiredDir = myLoc.directionTo(enemyHQLoc);
                Direction dir = getSpawnDirection(rc, desiredDir);
                if (dir != null) {
                    rc.spawn(dir);
                }
                firstSoldier = true;
            } else if (!mapRecon) {
                navi.HQinit();
            } else {
                if (Clock.getRoundNum() % 10 == 0 || shootEm) {
                    nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,
                            10000, rc.getTeam().opponent());
                }
                if (nearbyEnemies.length > 0) {
                    shootEm = true;
                } else {
                    shootEm = false;
                }

                if (shootEm) {
                    rc.attackSquare(rc.senseRobotInfo(nearbyEnemies[0]).location);
                } else {
                    Direction desiredDir = myLoc.directionTo(enemyHQLoc);
                    Direction dir = getSpawnDirection(rc, desiredDir);
                    if (dir != null) {
                        rc.spawn(dir);
                    }
                }

            }
        }

    }

    private static Direction getSpawnDirection(RobotController rc, Direction dir) {
        Direction canMoveDirection = null;
        int desiredDirOffset = dir.ordinal();
        int[] dirOffsets = new int[] { 0, 1, -1, 2, -2, 3, -3, 4 };
        for (int dirOffset : dirOffsets) {
            Direction currentDirection = Direction.values()[(desiredDirOffset
                    + dirOffset + 8) % 8];
            if (rc.canMove(currentDirection)) {
                if (canMoveDirection == null) {
                    canMoveDirection = currentDirection;
                }
            }
        }
        // Otherwise, let's just spawn in the desired direction, and make sure
        // to clear out a path later
        return canMoveDirection;
    }
}
