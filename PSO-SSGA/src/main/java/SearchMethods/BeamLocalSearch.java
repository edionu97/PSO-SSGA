package SearchMethods;

public abstract class BeamLocalSearch <returnType> extends LocalSearch<returnType> {

    protected abstract void initializePopulation();

    protected abstract void evaluatePopulation();

}
