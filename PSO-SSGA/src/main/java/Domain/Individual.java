package Domain;

public class Individual <Position,Fitness>{

    private Position value;

    private Fitness fitness;

    public Individual(){}

    public Individual(Position value){
        this.value = value;
    }

    public Position getValue() {
        return value;
    }

    public void setValue(Position value) {
        this.value = value;
    }

    public Fitness getFitness() {
        return fitness;
    }

    public void setFitness(Fitness fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString(){
        return value + " " + fitness;
    }
}
