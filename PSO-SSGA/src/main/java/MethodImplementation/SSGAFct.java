package MethodImplementation;

import Domain.Individual;
import SearchMethods.GA;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import java.util.*;
import java.util.function.Function;

public class SSGAFct extends GA<Double,List<Double>,Double>{

    private int dimCrom,selectionNumber;
    private Function<List < Double> , Double > toEvaluate;
    private double b,a,eps,mutateProb;

    private int step;

    private List < XYChart.Data <String,Number> > dataList = new ArrayList<>();

    /**
     *
     * @return the position of the element which,based on its fitness,is considered to be the worst individual from the population
     */

    private int worst(){
        int poz = 0;

        for(int i = 1; i < dimPop; ++i) {
            if (population.get(i).getFitness() > population.get(poz).getFitness()) poz = i;
        }

        return poz;
    }


    @Override
    protected Individual<List<Double>, Double> select() {

        Random random = new Random(System.nanoTime());

        BitSet bitSet = new BitSet(dimPop + 1);

        int index = random.nextInt(dimPop);

        bitSet.set(index);

        for(int i = 1; i < selectionNumber; ++i){

            int newIndex = random.nextInt(dimPop);

            if(bitSet.get(newIndex)){
                --i;
                continue;
            }

            bitSet.set(newIndex);

            if(population.get(index).getFitness() <= population.get(newIndex).getFitness())continue;

            index = newIndex;
        }

        return population.get(index);
    }

    private Individual<List<Double>, Double> crossOver(Function < Pair<Double,Double>, Double > fct, List <Double> momList,List < Double > dadLst){

        Individual < List <Double > ,Double > child = new Individual<>();
        child.setValue(new ArrayList<>());

        for(int i = 0; i < dimCrom; ++i){
            double newValue = fct.apply(new Pair<>(momList.get(i),dadLst.get(i)));

            if(newValue < a || newValue > b)return  null;
            child.getValue().add(newValue);
        }

        return child;
    }

    @Override
    protected Pair<Individual<List<Double>, Double>, Individual<List<Double>, Double>> crossOver(Individual<List<Double>, Double> mom, Individual<List<Double>, Double> dad) {

        Individual<List<Double>, Double> firstChild = crossOver((x) -> (x.getValue() + x.getKey()) / 2, mom.getValue(), dad.getValue());

        Random random = new Random(System.nanoTime());

        int negative = random.nextDouble() <= 0.5 ? -1 : 1 ;

        Individual<List<Double>, Double> secondChild = crossOver((x) -> negative * Math.sqrt(Math.abs(x.getValue() * x.getKey())), mom.getValue(), dad.getValue());

        if (firstChild == null) firstChild = mom;
        if (secondChild == null) secondChild = dad;

        return new Pair<>(firstChild, secondChild);
    }

    @Override
    protected Individual<List<Double>, Double> mutate(Individual<List<Double>, Double> individual, double probability) {

        Random random = new Random(System.nanoTime());

        for(int i = 0; i < dimCrom; ++i){

            double p = random.nextDouble(),newV;

            if(p > probability)continue;

            newV = random.nextDouble() <= 0.5 ? individual.getValue().get(i) - eps : individual.getValue().get(i) + eps;

            if(newV < a || newV > b)continue;

            individual.getValue().set(i,newV);
        }

        return  individual;
    }

    @Override
    protected void initializePopulation() {

        Random random = new Random(System.nanoTime());

        for(int i = 1; i <= dimPop; ++i){

            Individual<List < Double >, Double> individual = new Individual<>();
            individual.setValue(new ArrayList<>());

            for(int j = 1; j <= dimCrom; ++j){

                individual.getValue().add(random.nextDouble()* (b - a) + a);
            }

            population.add(individual);
        }

    }

    private double evaluateSingle(Individual < List < Double >, Double > individual){
        return toEvaluate.apply(individual.getValue());
    }


    @Override
    protected void evaluatePopulation() {
        population.forEach(x->x.setFitness(evaluateSingle(x)));
    }



    @Override
    public Double search(List<Double> inWhat) throws Exception {

        initializePopulation();
        evaluatePopulation();

        for(int i = 1; i <= numberOfGenerations; ++i){

            Individual <List <Double>,Double> first = select(),second = select(),childA,childB,worst;

            Pair < Individual <List <Double>,Double>,Individual <List <Double>,Double> > children = crossOver(first,second);

            childA = mutate(children.getKey(),mutateProb);

            childB = mutate(children.getValue(),mutateProb);

            childA.setFitness(evaluateSingle(childA));
            childB.setFitness(evaluateSingle(childB));

            if(childA.getFitness() > childB.getFitness()){
                childA.setFitness(childB.getFitness());
                childA.setValue(childB.getValue());
            }

            if(i == 1 || i % step == 0) dataList.add(new XYChart.Data<>(i+"",population.stream().min(Comparator.comparing(Individual::getFitness)).get().getFitness()));

            worst = population.get(worst());

            if(worst.getFitness() <= childA.getFitness())continue;


            worst.setValue(childA.getValue());
            worst.setFitness(childA.getFitness());
        }

        return population.stream().min(Comparator.comparing(Individual::getFitness)).get().getFitness();
    }

    @Override
    public List< XYChart.Data<String, Number> > evolution() {
        return dataList;
    }

    @Override
    public void setEvolutionStep(int step) {
        this.step = step;
    }


    public void setDimCrom(int dimCrom) {
        this.dimCrom = dimCrom;
    }

    public void setToEvaluate(Function<List<Double>, Double> toEvaluate) {
        this.toEvaluate = toEvaluate;
    }

    public void setB(double b) {
        this.b = b;
    }

    public void setA(double a) {
        this.a = a;
    }

    public void setEps(double eps) {
        this.eps = eps;
    }

    public void setSelectionNumber(int selectionNumber){
        this.selectionNumber = selectionNumber;
    }

    public void setMutateProb(double mutateProb) {
        this.mutateProb = mutateProb;
    }

    public  void setDimPop(int dimPop){
        this.dimPop = dimPop;
    }

    public void setNumberOfGenerations(int nr){
        numberOfGenerations = nr;
    }
}
