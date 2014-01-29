package sunkern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Navigator {
    MapLocation finalTarget;
    MapLocation curTarget;
    int targetNum;
    List<MapLocation> targets;
    RobotController rc;
    int maxX;
    int maxY;
    int[][] coarseMap;
    int coarseSize;

    public Navigator(RobotController myRC){
        finalTarget = null;
        curTarget = null;
        targetNum = 0;
        targets = null;
        rc = myRC;
        maxX = rc.getMapWidth();
        maxY = rc.getMapHeight();
    }
    
    public void setTarget(MapLocation target){
        finalTarget = target;
        
    }
    
    public void Navigate(){
        
    }
    
    public void BFSNavigate(){
        MapLocation startGrid = locateInCoarse(rc.getLocation());
        MapLocation endGrid = locateInCoarse(finalTarget);
        Queue<List<MapLocation>> queue = new LinkedList<List<MapLocation>>();
        Queue<Integer> queueVal = new LinkedList<Integer>();
        Set<MapLocation> travelled = new HashSet<MapLocation>();
        List<MapLocation> path = new LinkedList<MapLocation>();
        int pathVal = -10000;
        
        travelled.add(new MapLocation(startGrid.x,startGrid.y));
        for (int i=startGrid.x-1;i<startGrid.x+2;i++){
            for (int j=startGrid.y-1;j<startGrid.y+2;j++){
                if (i>=0 && i<coarseSize && j>=0 && j<coarseSize && (i!=j)){
                    queue.add(Arrays.asList(new MapLocation[]{new MapLocation(i,j)}));
                    queueVal.add(coarseMap[i][j]);
                }
            }
        }
        while (!queue.isEmpty()){
            List<MapLocation> disList = queue.remove();
            int curVal = queueVal.remove();
            MapLocation disPlace = disList.get(disList.size()-1);
            travelled.add(disPlace);
            for (int i=disPlace.x-1;i<disPlace.x+2;i++){
                for (int j=disPlace.y-1;j<disPlace.y+2;j++){
                    if (i>=0 && i<coarseSize && j>=0 && j<coarseSize && (i!=j)){
                        MapLocation ml = new MapLocation(i,j);
                        if (!travelled.contains(ml)){
                            if (ml.equals(endGrid)){
                                if (curVal>pathVal){
                                    path = disList;
                                }
                                continue;
                            }else{
                                MapLocation nextPlace = new MapLocation(i,j);
                                disList.add(nextPlace);
                                queue.add(disList);
                                queueVal.add(curVal+coarseMap[i][j]);
                            }
                        }
                    }
                }
            }
        }
        targets = new ArrayList<MapLocation>();
        double zoom = maxX/coarseSize;
        for (MapLocation ml:path){
            targets.add(new MapLocation((int)(ml.x*zoom), (int)(ml.y*zoom)));
        }
    }
    
    public void HQinit() throws GameActionException{
        int mh = rc.getMapHeight();
        if (mh>79){
            coarseSize = 6;
        }else if (mh>59){
            coarseSize = 5;
        }else if (mh>39){
            coarseSize = 4;
        }else{
            coarseSize = 3;
        }
        coarseMap = assessMap(coarseSize);
        rc.broadcast(2001, coarseSize);
        String[] StringMap = gatherMap(coarseMap,coarseSize);
        for (int i=0;i<StringMap.length;i++){
            rc.broadcast(2003+i, Integer.parseInt(StringMap[i]));
        }
    }
    
    public String[] gatherMap(int[][] map, int bigSize){
        List<String> so = new ArrayList<String>();
        StringBuilder s;
        int curRow = 0;
        int curCol = 0;
        for (int i=0;i<bigSize-2;i++){
            s = new StringBuilder();
            for (int j=0;j<9;j++){
                s.append(map[curRow][curCol]);
                curCol++;
                if (curCol==bigSize){
                    curCol = 0;
                    curRow++;
                }
            }
            so.add(s.toString());
        }
        return so.toArray(new String[0]);
    }
    
    public void parseMap(String[] inp){
        int size = inp.length;
        int mapSize = size+2;
        int [][] ret = new int[mapSize][mapSize];
        int curRow = 0;
        int curCol = 0;
        for (int i=0; i<size; i++){
            char[] curry = inp[i].toCharArray();
            for (int j=0;j<curry.length;j++){
                ret[curRow][curCol] = Integer.parseInt(Character.toString(curry[j]));
                curCol++;
                if (curCol==mapSize){
                    curCol = 0;
                    curRow++;
                }
            }
        }
        coarseMap =  ret;
    }
    
    public MapLocation locateInCoarse(MapLocation loc){
        return new MapLocation(loc.x/(maxX/coarseSize),loc.y/(maxY/coarseSize));
    }
    
    // Should only be called by the HQ. 
    // 
    public int[][] assessMap(int bigBoxSizeIn){
        int coarseWidth = rc.getMapWidth()/bigBoxSizeIn;
        int coarseHeight = rc.getMapHeight()/bigBoxSizeIn;
        int[][] coarseMap = new int[coarseWidth][coarseHeight];
        for(int x=0;x<coarseWidth*bigBoxSizeIn;x++){
            for(int y=0;y<coarseHeight*bigBoxSizeIn;y++){
                coarseMap[x/bigBoxSizeIn][y/bigBoxSizeIn]+=evalTile(x,y);
            }
        }
        return coarseMap;
    }
    
    public int evalTile(int x, int y){//returns a 0 or a 1
        int terrainOrdinal = rc.senseTerrainTile(new MapLocation(x,y)).ordinal();//0 NORMAL, 1 ROAD, 2 VOID, 3 OFF_MAP
        return (terrainOrdinal<2?0:1);
    }
    
    
    
    
    // 2013 winner copypasta
    
//    public static void setupBFSMode(MapLocation endLocation) throws GameActionException {
//        navMode = NavMode.BFSMODE;
//        destination = endLocation;
//        int[][] encArray = NavSystem.populate5by5board();
//        int[] goalCoord = NavSystem.locToIndex(rc.getLocation(), destination, 2);
//        BFSRound = 0;
//        BFSIdle = 0;
//        BFSTurns = NavSystem.runBFS(encArray, goalCoord[1], goalCoord[0]);
////      System.out.println("BFSTurns :" + BFSTurns.length);
//        if (BFSTurns.length == 0) { // if unreachable, tell to HQ and unassign himself
//            System.out.println("unreachable, unassigned: " + robot.unassigned + " soldierstate: " + ((SoldierRobot) robot).soldierState);
//            EncampmentJobSystem.postUnreachableMessage(destination);
//            navMode = NavMode.NEUTRAL;
//            robot.unassigned = true;
//        }
//    }
    
    public static int[] runBFS(int[][] encArray, int goalx, int goaly) {
        int[][] distanceArray = new int[encArray.length][encArray[0].length];
        distanceArray[2][2] = 1;
        for (int y = 0; y<5; y++) {
            for (int x=0; x<5; x++) {
                if (encArray[y][x] > 0) {
                    distanceArray[y][x] = -1;
                }
            }
        }
        
        int currValue = 1;
        boolean reached = false;
        whileLoop: while(currValue < 20) {          
            for (int y = 0; y<5; y++) {
                for (int x=0; x<5; x++) {
                    if (distanceArray[y][x] == currValue) {
                        if (y == goaly && x == goalx) {
                            reached = true;
                            break whileLoop;
                        } else {
                            propagate(distanceArray, x, y, currValue + 1);
                        }
                    }
                }
            }
            currValue++;
        }
        
        if (!reached || currValue == 1) { // if unreachable
            return new int[0]; // return empty list
        }
        
        int shortestDist = distanceArray[goaly][goalx] - 1;
        int[] output = new int[shortestDist];
        currValue = shortestDist;
        int currx = goalx;
        int curry = goaly;
        int turn = 0;
        
        while(currValue > 1) {
            int[][] neighbors = getNeighbors(currx, curry);
            forloop: for (int[] neighbor: neighbors) {
                int nx = neighbor[0];
                int ny = neighbor[1];
                if (ny < 5 && ny >= 0 && nx < 5 && nx >= 0) {
                    if (distanceArray[ny][nx] == currValue) {
                        turn = computeTurn(currx, curry, nx, ny);                       
                        output[currValue-1] = turn;
                        currx = nx;
                        curry = ny;
                        currValue--;
                        break forloop;
                    }
                }
            }
        }
        output[0] = computeTurn(currx, curry, 2, 2);

        return output;
        
    }   
    
    /**
     * given an array and a coordinate and a value, propagate value to the neighbors of the coordinate
     * @param distanceArray
     * @param y
     * @param x
     * @param value
     */
    public static void propagate(int[][] distanceArray, int x, int y, int value) {
        int[][] neighbors = getNeighbors(x,y);
        for (int[] neighbor: neighbors) {
            int ny = neighbor[1];
            int nx = neighbor[0];
            if (ny < 5 && ny >= 0 && nx < 5 && nx >= 0) {
                if (distanceArray[ny][nx] > value || distanceArray[ny][nx] == 0) {
                    distanceArray[ny][nx] = value;
                    
                }
            }
        }
    }

    public static int[][] getNeighbors(int x, int y) {
        int[][] output = {{x-1, y}, {x-1, y+1}, {x, y+1}, {x+1, y+1},{x+1, y}, {x+1, y-1}, {x, y-1}, {x-1, y-1}};
        return output;
    }    
    
    public static int computeTurn(int x, int y, int nx, int ny) {
        if (ny == y+1 && nx == x) {
            return 0;
        } else if (ny == y+1 && nx == x-1) {
            return 1;
        } else if (ny == y && nx == x-1) {
            return 2;
        } else if (ny == y-1 && nx == x-1) {
            return 3;
        } else if (ny == y-1 && nx == x) {
            return 4;
        } else if (ny == y-1 && nx == x+1) {
            return 5;
        } else if (ny == y && nx == x+1) {
            return 6;
        } else if (ny == y+1 && nx == x+1) {
            return 7;
        } else {
            return 8;
        }
    }
}
