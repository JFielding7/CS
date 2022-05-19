import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class App {
    public static void main(String[] args) {
        Graph graph = new Graph(5);
        graph.addEdges(0, new Edge(2));
        graph.addEdges(1, new Edge(0), new Edge(3));
        graph.addEdges(2, new Edge(1));
        graph.addEdges(3, new Edge(2), new Edge(4));
        graph.addEdges(4, new Edge(2));
        /*
        int index = 0;
        for (ArrayList<Edge> a : graph.edges){
            System.out.print(index + "\t");
            for(Edge e : a){
                System.out.print(e.destination + " " + e.getWeight() + "\t"); 
            }
            System.out.println();
            index++;
        }
        */
        System.out.println(Path.shortestPath(0, 4, graph));
    }

    //queue which hold all the nodes that need to be search, but have not been yet
    static Queue<Integer> queue = new LinkedList<>();
    //stores all the nodes that are already visted do that we do not search the same node twice
    static boolean[] visited = new boolean[4];

    static boolean hasPath(int node, int destination, Graph graph){
        //check the neighbors of this node and add them to the queue
        //returns true if one of the neighbors is the the destination node
        if(checkNeighbors(node, destination, graph)) return true;
        //base case, when no more nodes are in the queue
        if(queue.isEmpty()) return false;
        //recursivle call on the node at the front of the queue
        return hasPath(queue.poll(), destination, graph);
    }


    static boolean checkNeighbors(int node, int destination, Graph graph) {
        //loops through all the neighbor nodes of this node and adds them to the queue if they are not yet visited
        ArrayList<Edge> connectedEdges = graph.edges.get(node);
        for(int i = 0; i < connectedEdges.size(); i++) {
            int vertex = connectedEdges.get(i).destination;
            if(vertex == destination) return true;
            else if(!visited[vertex]) {
                visited[vertex] = true;
                queue.offer(vertex);
            }
        }
        return false;
    }

}
