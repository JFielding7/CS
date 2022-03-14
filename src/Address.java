
public class Address {

    private String name;
    private Street street;
    private int number;

    public Address(String name, Street street, int number) {
        this.name = name;
        this.street = street;
        this.number = number;
    }

    public String getName(){
        return this.name;
    }

    public Street getStreet(){
        return this.street;
    }

    public int getNumber(){
        return this.number;
    }

}
