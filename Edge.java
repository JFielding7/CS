
class Edge implements Comparable<Edge> {

    int destination;
    private int weight;

    public Edge(int destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }

    public Edge(int destination) {
        this.destination = destination;
    }

    public int getWeight(){
        return this.weight;
    }

    @Override
    public int compareTo(Edge other) {

        return Double.compare(this.weight, other.getWeight());
    }

}
