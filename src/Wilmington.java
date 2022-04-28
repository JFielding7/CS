import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

public class Wilmington {

    private static final double LATITUDE50FT = 0.00013724198;
    private static final double LONGITUDE50FT = 0.000173437674;

    //private static Address[] places;
    static ArrayList<Street> streets;
    private static ArrayList<Intersection> intersections;

    //main method 
    //calls the wilmington method to set up the map
    //calls shortest path to find the route between 2 different locations
    public static void findRoute(String str1, String str2) throws FileNotFoundException {
        
        // Intersection i1 = find(streets.get(3), streets.get(0));
        // Intersection i2 = find(streets.get(44), streets.get(64));
        // for(int i = 0; i < intersections.size() - 1; i++){
        //     for(int j = i+1; j < intersections.size(); j++){
        //         Intersection i1 = intersections.get(i);
        //         Intersection i2 = intersections.get(j);
        //         System.out.println(i + " " + i1);
        //         System.out.println(j + " " + i2);
        //         Group g = shortestPath(i1, i2); 
        //         ArrayList<Instruction> p = g.path.compilePath(i1);
                
        //     }
        // }
        // System.out.println("Done");

        // Scanner scan = new Scanner(System.in);
        // System.out.print("Enter starting location: ");
        // String str1 = scan.nextLine();
        Coordinate c1 = new Coordinate(str1.substring(0, str1.indexOf(",")), str1.substring(str1.indexOf(",") + 2));
        Intersection i1 = find(c1);

        // System.out.print("Enter destination: ");
        // String str2 = scan.nextLine();
        Coordinate c2 = new Coordinate(str2.substring(0, str2.indexOf(",")), str2.substring(str2.indexOf(",") + 2));
        Intersection i2 = find(c2);

        System.out.println("Start:\n" + i1.street1().getName());
        System.out.println(i1.street2().getName() + "\n");

        System.out.println("Destination:\n" + i2.street1().getName());
        System.out.println(i2.street2().getName() + "\n");
        // System.out.println(i2.getLocation());
        
        Group g = shortestPath(i1, i2); 

        ArrayList<Instruction> p = g.path.compilePath(i1);

        for(Instruction inter : p){

            System.out.println(inter.continueDist);
            
            System.out.println();
            System.out.println(inter.i.street1().getName());
            System.out.println(inter.i.street2().getName());
            System.out.println(inter.turn);
        }
        //total distance
        System.out.println("Distance: " + (int)(g.dist * 100 + 0.5) / 100.0 + " miles");
        System.out.println("Time: " + Math.round(g.time) + " minutes");
        //scan.close();
        
    }

    public static void wilmington() throws FileNotFoundException {
        
        streets = new ArrayList<>();
        intersections = new ArrayList<>();

        File street = new File("C:\\Users\\220700jf\\Documents\\WilmingtonMap\\src\\WilmingtonStreets.txt");
        
        Scanner scan = new Scanner(street);

        while(scan.hasNext()){
            String[] road = scan.nextLine().split("  |, "); 
            
            int oneWay = road.length > 5 ? Integer.parseInt(road[5]) : 0;
            int speedLimit = road.length > 6 ? Integer.parseInt(road[6]) : 25;

            HashMap<Coordinate, Integer> zIntervals = new HashMap<>();
            for(int i = 7; i < road.length; i++){
                String interval = road[i];
                zIntervals.put(new Coordinate(interval.substring(interval.indexOf(":") + 1, interval.indexOf(",")), 
                interval.substring(interval.indexOf(",") + 1)),
                Integer.parseInt(interval.substring(0, interval.indexOf(":"))));
            }
            
            streets.add(new Street(road[0], new Coordinate(road[1], road[2]),
            new Coordinate(road[3], road[4]), oneWay, zIntervals, speedLimit));
            
        }

        scan.close();

        findIntersections();
    }

    public static void findIntersections() {
        
        for(int i = 0; i < streets.size()-1; i++){
            Street s1 = streets.get(i);
            for(int j = i+1; j < streets.size(); j++){
                Street s2 = streets.get(j);
                Coordinate inter = s1.getEqu().intersection(s2.getEqu());
                if(doesInter(s1, s2, inter)){
                    
                    Intersection inters = new Intersection(inter, s1, s2);
                    s1.addIntersection(inters);
                    s2.addIntersection(inters);
                    intersections.add(inters);
                }
            }
            if(s1.getIntersections().size() > 1) s1.sortIntersections(0, s1.getIntersections().size() - 1);
        }
        Street last = streets.get(streets.size() - 1);
        last.sortIntersections(0, last.getIntersections().size() - 1);
        for(Intersection i : intersections){
            i.compileBlocks();
        }
    }

    //finds the intersections between roads with and error of 50ft
    public static boolean doesInter(Street s1, Street s2, Coordinate inter) {
        
        return s1.zPosition(inter.x) == s2.zPosition(inter.x) && (inter.x >= s1.xLowerBound() - LONGITUDE50FT && inter.x <= s1.xUpperBound() + LONGITUDE50FT 
        && inter.y >= s1.yLowerBound() - LATITUDE50FT && inter.y <= s1.yUpperBound() + LATITUDE50FT
        && inter.x >= s2.xLowerBound() - LONGITUDE50FT && inter.x <= s2.xUpperBound() + LONGITUDE50FT 
        && inter.y >= s2.yLowerBound() - LATITUDE50FT && inter.y <= s2.yUpperBound() + LATITUDE50FT);
    }
    
    //finds the closest intersection to the given Coordinate
    static Intersection find(Coordinate coordinate){
        
        double minDist = 10000;
        Intersection closest = null;
        for(Intersection i : intersections) {
            double dist = calcDistance(coordinate, i.getLocation());
            if(dist < minDist){
                minDist = dist;
                closest = i;
            }
        }
        return closest;
    }

    //finds an intersection between 2 streets
    //return null if they do not intersect
    static Intersection find(Street s1, Street s2){
        for(Intersection i : s1.getIntersections()){
            if((i.street1() == s1 && i.street2() == s2) || (i.street1() == s2 && i.street2() == s1))
                return i;
        }
        return null;
    }

    public static Group shortestPath(Intersection start, Intersection end){
        //set used to keep track of all the Streets that have been visited
        Set<Block> visited = new HashSet<>();

        //priority queue weighted by the weight of the Streets in each group
        PriorityQueue<Group> queue = new PriorityQueue<>(streets.size(), new Comparator<Group>() {

            @Override
            public int compare(Group g1, Group g2) {
                return g1.time > g2.time ? 1 : -1;
            }

        });

        //adds the starting node to the queue, along with a distance of zero
        PathBuilder startPath = new PathBuilder(null, start, null, 0);
        queue.offer(new Group(start, startPath, 0, 0));
        
        while(!queue.isEmpty()){
            
            //pulls the next node, distance, and path from the queue
            Group next = queue.poll();
            Intersection node = next.node;
            double dist = next.dist;
            double time = next.time;
            PathBuilder path = next.path;

            //base case, once the end node is reached
            if(node == end) 
                return next;

            //loops through all the neighbor nodes of this node and adds them to the queue if they are not yet visited
            Block[] blocks = node.getBlocks();
            
            for(int i = 0; i < blocks.length; i++) {
                Block road = blocks[i];
                if(road == null) continue;
                Intersection nextInter = road.destination;

                //node is enqueued if this Street is not already visited
                //the new distance is the sum of the previous distance and the weight of this Street
                if(!visited.contains(road)) {
                    
                    double distance = calcDistance(node.getLocation(), nextInter.getLocation());
                    double newDist = dist + distance;
                    int speedLimit = road.street.getSpeed();
                    double t = distance / speedLimit * 48;
                    
                    PathBuilder p = new PathBuilder(path, nextInter, road, newDist);
                    Group g = new Group(nextInter, p, newDist, time + t);
                    visited.add(road);
                    queue.offer(g);
                }
            }
        }
        //return null if no such path exists between the start and the end
        System.out.println("No path");
        return null;
    }

    //finds the distance in miles between two intersections
    static double calcDistance(Coordinate c1, Coordinate c2){

        double disY = (c1.y - c2.y) * 54.6;
        double disX = (c1.x - c2.x) * 69;                          
        return Math.sqrt(Math.pow(disX, 2) + Math.pow(disY, 2));
    }

}

//group class used to bundle a node, distance from the start, 
//and an ArrayList containg the path from the start to this node
class Group {

    Intersection node;
    double dist;
    double time;
    PathBuilder path;
    

    public Group(Intersection node, PathBuilder path, double dist, double time){

        this.node = node;
        this.dist = dist;
        this.path = path;
        this.time = time;
    }

    @Override
    public String toString(){
        return dist + " " + node.getLocation();
    }

}   

class PathBuilder {

    PathBuilder previous;
    Intersection inter;
    Block block;
    double distance;
  
    public PathBuilder(PathBuilder previous, Intersection current, Block b, double distance){
    
        this.inter = current;
        this.previous = previous;
        this.block = b;
        this.distance = distance;
    }

    public ArrayList<Instruction> compilePath(Intersection start) {

        PathBuilder p, prev, currPath;
        p = prev = currPath = this;
        
        Instruction prevInstruction = new Instruction(currPath.inter, "Arrive at your destination.", p);
        ArrayList<Instruction> path = new ArrayList<>();
        path.add(prevInstruction);
        
        //creates the instructions by following through the entire path, starting at the end
        while(p != null){
            
            String turn = p.turn(p);
            
            if(turn.charAt(0) == 'T' || turn.charAt(0) == 'C'){
                
                currPath = p.previous;
                Instruction currInstruction = new Instruction(currPath.inter, turn, currPath);
                path.add(0, currInstruction);
                prevInstruction.setDist(currPath, prev);
                App.highLight(currPath.inter, prev.inter);

                prevInstruction = currInstruction;
                prev = currPath;
            }
            p = p.previous;
        }

        PathBuilder firstTurn = path.get(0).path;
        App.highLight(firstTurn.inter, start);
        path.get(0).setDist(null, firstTurn);
        
        return path;
    }

    private String turn(PathBuilder p){
        
        PathBuilder prev = p.previous;
        if(prev == null || prev.previous == null) return "Straight";

        PathBuilder prev2 = p.previous.previous;
        if(prev2.block == null){ 
            if(p.inter.onSameStreet(prev2.inter))
                return "Straight";
        }
        
        else if(prev2.block.street == p.block.street ||
        prev.inter.onSameStreet(p.inter) && prev2.inter.onSameStreet(p.inter))
            return "Straight";
        
        boolean movingUp = (prev.inter.getLocation().x - prev2.inter.getLocation().x) > 0;
        double longitudeDiff = p.inter.getLocation().y - prev.block.street.getEqu().value(p.inter.getLocation().x);

        if(Math.abs(longitudeDiff) < 0.00005){
            System.out.println("Continue on to " + p.block.street.getName());
            System.out.println(longitudeDiff);
            return "Continue on to " + p.block.street.getName();
        }
        else if((movingUp && longitudeDiff > 0) || 
        (!movingUp && longitudeDiff < 0))
            return "Turn Right on to " + p.block.street.getName();
        
        return "Turn Left on to " + p.block.street.getName(); 
    }

  }

class Instruction{

    Intersection i;
    String turn;
    String continueDist;
    PathBuilder path;

    Instruction(Intersection i, String turn, PathBuilder path){
        
        this.i = i;
        this.turn = turn;
        this.path = path;
    }

    // void setDist(double dist){
        
    //     if(dist < 0.01) this.continueDist = "";
    //     else if(dist < 0.1) this.continueDist = "Continue for " + Math.round(dist * 5280) + " ft";
    //     else this.continueDist = "Continue for " + (int) (dist * 100 + 0.5) / 100.0 + " miles";

    // }

    void setDist(PathBuilder p1, PathBuilder p2){

        double dist = 0;
        if(p1 == null) dist = p2.distance;
        else dist = p2.distance - p1.distance;
        
        if(dist < 0.01) this.continueDist = "";
        else if(dist < 0.1) this.continueDist = "Continue for " + Math.round(dist * 5280) + " ft";
        else this.continueDist = "Continue for " + (int) (dist * 100 + 0.5) / 100.0 + " miles";

    }

}


