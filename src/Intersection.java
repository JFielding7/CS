public class Intersection {

    private Street[] streets;
    private Coordinate location;
    private Block[] blocks;

    public Intersection(Coordinate location, Street...streets){

        this.location = location;
        this.streets = streets;
    }

    public Coordinate getLocation() {
        return location;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
    }

    public Street[] getStreets(){
        return this.streets;
    }

    public void compileBlocks(){
        this.blocks = new Block[4];
        Intersection next0 = streets[0].nextIntersection(this);
        if(next0 != null) blocks[0] = new Block(streets[0], this, next0);
        Intersection next1 = streets[0].prevIntersection(this);
        if(next1 != null) blocks[1] = new Block(streets[0], this, next1);
        Intersection next2 = streets[1].nextIntersection(this);
        if(next2 != null) blocks[2] = new Block(streets[1], this, next2);
        Intersection next3 = streets[1].prevIntersection(this);
        if(next3 != null) blocks[3] = new Block(streets[1], this, next3);
        
    }

    public Block[] getBlocks(){
        return blocks;
    }

    public boolean onSameStreet(Intersection other){
        String curr1 = this.streets[0].getName();
        String curr2 = this.streets[1].getName();
        String other1 = other.streets[0].getName();
        String other2 = other.streets[1].getName();
        return curr1.equals(other1) || curr1.equals(other2) || 
        curr2.equals(other1) || curr2.equals(other2);
    }

    public String toString(){
        String str = "";
        for(Street s : streets){
            str += s.getName() + " ";
        }
        return str;
    }
}
