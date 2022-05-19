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

    public Street street1(){
        return this.streets[0];
    }

    public Street street2() {
        return this.streets[1];
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

        for(Street street : this.streets){
            if(contains(other.getStreets(), street)) return true;
        }
        return false;
        
    }

    public Street match(Intersection other){

        for(Street street : this.streets){
            if(contains(other.getStreets(), street)) return street;
        }
        return null;
    }

    public Street difference(Intersection other){

        for(Street street : this.streets){
            if(!contains(other.getStreets(), street)) return street;
        }
        return null;
    }

    static boolean contains(Street[] streets, Street target){

        for(Street s : streets){
            
            if(s.same(target)) return true;
        }
        return false;
    }

    public void addStreet(Street s){

        Street[] temp = new Street[streets.length + 1];
        for(int i = 0; i < temp.length - 1; i++){
            temp[i] = streets[i];
        }
        temp[temp.length - 1] = s;
        this.streets = temp;
    }

    public String toString(){

        String str = "";
        for(Street s : streets){
            str += s.getName() + " ";
        }
        return str;
    }
}