/*
import java.util.ArrayList;
import java.util.LinkedList;

public class BFSMain {

    public static void main(String[] args){
        //Graph, with BFS search it should search nodes 1-10 in that order
        
        //test case 1
        Node node10 = new Node(10);
        Node node9 = new Node(9);
        Node node8 = new Node(8);
        Node node7 = new Node(7);
        Node node6 = new Node(6);
        Node node5 = new Node(5);
        Node node4 = new Node(4);
        Node node3 = new Node(3);
        Node node2 = new Node(2);
        Node node1 = new Node(1, node2, node3, node4);
        node2.setNeighbors(node1);
        node3.setNeighbors(node1, node5);
        node4.setNeighbors(node1, node6, node7);
        node5.setNeighbors(node3, node8, node9);
        node6.setNeighbors(node4);
        node7.setNeighbors(node4);
        node8.setNeighbors(node5, node10);
        node9.setNeighbors(node5);
        node10.setNeighbors(node8);
        System.out.println(BFS(node1));

        //test case 2
        node10 = new Node(10);
        node9 = new Node(9);
        node8 = new Node(8);
        node7 = new Node(7);
        node6 = new Node(6);
        node5 = new Node(5);
        node4 = new Node(4);
        node3 = new Node(3);
        node2 = new Node(2);
        node1 = new Node(1, node2, node3);
        node2.setNeighbors(node1, node4, node5, node6);
        node3.setNeighbors(node1, node7, node8, node9);
        node4.setNeighbors(node2);
        node5.setNeighbors(node2, node10);
        node6.setNeighbors(node2);
        node7.setNeighbors(node3);
        node8.setNeighbors(node3);
        node9.setNeighbors(node3);
        node10.setNeighbors(node5);
        //prints out the order in which the nodes are searched, prints 1-10 in that order
        System.out.println(BFS(node1));

    }

    //queue which hold all the nodes that need to be search, but have not been yet
    static LinkedList<Node> queue = new LinkedList<>();
    //stores all the nodes that are laready visted do that we do not search the same node twice
    static ArrayList<Node> visitedNodes = new ArrayList<>();

    static String BFS(Node node){
        //mark this node as visited
        visitedNodes.add(node);
        //check the neighbors of this node and add them to the queue
        checkNeihghbors(node);
        //base case, when no more nodes are in the queue
        if(queue.isEmpty()) return "" + node.value;
        //recursivley call BFS on the node at the front of the queue
        return node.value + " " + BFS(queue.removeFirst());
    }

    static void checkNeihghbors(Node node) {
        //loops through all the neighbor nodes of this node and adds them to the queue if they are not yet visited
        for(int i = 0; i < node.neighbors.length; i++){
            Node neighbor = node.neighbors[i];
            if(!visitedNodes.contains(neighbor)) queue.addLast(neighbor);
        }
    }
}

//Node class, each node object has 2 atributes: a value, and an array of all its neighbors
class Node{

    Node[] neighbors;
    int value;

    Node(int value, Node...neighbors){
        this.value = value;
        this.neighbors = neighbors;
    }

    Node(int value){
        this.value = value;
    }

    void setNeighbors(Node...neighbors){
        this.neighbors = neighbors;
    }

}
*/