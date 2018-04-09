package MethodImplementation;

import Domain.Individual;
import SearchMethods.GA;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import java.util.*;


public class SSGA  extends GA<Double,Integer,Double> {

    private List < Double > concreteProblem;

    private int selectionNumber;

    private int dimCrom;

    private int splitPointsNumber = 2;

    private float mutationProbability;

    private int step;

    private List < XYChart.Data <String,Number> > dataList = new ArrayList<>();

    /**
     *
     * @throws Exception if one of the parameter does not respect the problem constraints
     */

    private void checkAllParameters() throws  Exception{

        String error = "";

        if(selectionNumber > dimPop) error += "The dimension of the population is to small compared to the selection number.Increase the dimension or decrease the selection number\n";

        if(splitPointsNumber > dimCrom - 2) error += "The chromosome dimension is to small compared to the split point number.Increase or decrease it's size in order to proceed\n";

        if(mutationProbability > 1 || mutationProbability < 0) error += "The mutation probability should be a number from the [0,1]\n";

        if(dimPop > concreteProblem.size()) error += "The dimension of the population is too big compared to the actual data\n";

        if(error.equals(""))return;

        throw  new Exception(error);
    }



    /**
     * This function evaluates an individual,setting his fitness accordingly
     * @param individual->the entity that we want to evaluate
     */

    private void singleEvaluate(Individual<Integer,Double> individual){
        individual.setFitness(
                concreteProblem.get(individual.getValue())
        );
    }


    /**
     * This function ensure that all the individual representations have the same name of chromosomes (@dimCrom)
     * @param rep the individual binary representation
     * @return the resized string(the string with zeros on the first position or the substring of length @dimCrom)
     */

    private String resize(String rep){//ensure that the mom and the dad cromozome is the same length

        if(rep.length() > dimCrom)return rep.substring(0,dimCrom);

        StringBuilder builder = new StringBuilder(rep);

        while(builder.length() < dimCrom)builder.insert(0,'0');

        return builder.toString();
    }

    /**
     *
     * @param size->the chromosomes size(equal with both mom and dad length)
     * @param splitNr->the number of splitting points
     * @return ->a list with numbers in an increasing order which represents the mutation intervals
     */

    private  List < Integer > getSplitPoints(int size,int splitNr){
        List < Integer > list = new ArrayList<>();

        Random random = new Random(System.nanoTime());

        BitSet bitSet = new BitSet(size + 1);

        for(int i = 1;i <= splitNr; ++i){
            int number = random.nextInt(size - 1);

            if(bitSet.get(number) || number == 0){
                --i;
                continue;
            }

            bitSet.set(number);

            list.add(number);
        }

        list.sort(Integer::compare);


        return list;
    }

    /**
     *
     * @param firstParent->the first parent
     * @param secondParent  ->the secondParent
     * @return a string witch represents the parents child
     */

    private  String combine(String firstParent,String secondParent){

        List <Integer> splitPoints = getSplitPoints(firstParent.length(), splitPointsNumber);

        StringBuilder builder = new StringBuilder("");

        boolean firstParentTime = false;

        int left = 0;

        for(int point : splitPoints){

            firstParentTime = !firstParentTime;

            if(firstParentTime){
                builder.append(firstParent.substring(left,point));
                left = point;
                continue;
            }

            builder.append(secondParent.substring(left,point));
            left = point;
        }

        firstParentTime = !firstParentTime;

        if(firstParentTime){
            builder.append(firstParent.substring(left,firstParent.length()));
            return  builder.toString();
        }

        builder.append(secondParent.substring(left,secondParent.length()));

        return  builder.toString();
    }



    /**
     *
     * @param a ->first individual
     * @param b ->second individual
     * @return the individual with the lowest fitness
     */

    private Individual<Integer,Double> best(Individual<Integer,Double> a, Individual<Integer,Double> b){
        return a.getFitness() > b.getFitness() ? b : a;
    }

    /**
     *
     * @return the individual with the biggest fitness
     */

    private Individual<Integer,Double> getWorst(){

        int index = 0;

        for(int i = 1; i < population.size(); ++i){
            if(population.get(i).getFitness() > population.get(index).getFitness())index = i;
        }

        return population.get(index);
    }


    /**
     * This function is used to initialize the initial population
     */

    @Override
    protected void initializePopulation(){

        Random random = new Random(System.currentTimeMillis());

        BitSet bitSet = new BitSet(concreteProblem.size() + 1);

        for(int i = 1; i <= dimPop; ++i){

            int index = random.nextInt(concreteProblem.size());

            if(bitSet.get(index)) {
                --i;
                continue;
            }

            bitSet.set(index);

            population.add(new Individual<>(index));
        }

    }

    /**
     * This function evaluates all the population individual
     */

    @Override
    protected void evaluatePopulation(){
        population.forEach(this::singleEvaluate);
    }

    /**
     * It selects the most suitable individual from population
     * Uses the q-tournament selection method
     * @return the most suitable individual
     */

    @Override
    protected Individual<Integer,Double> select() {

        Random random = new Random(System.nanoTime());

        BitSet bitSet = new BitSet(population.size() + 1);

        int index = random.nextInt(population.size());

        bitSet.set(index);


        for (int i = 1; i < selectionNumber; ++i) {

            int secondIndex = random.nextInt(population.size());

            if (bitSet.get(secondIndex)) {
                --i;
                continue;
            }

            bitSet.set(secondIndex);

            if (population.get(index).getFitness() > population.get(secondIndex).getFitness()) index = secondIndex;
        }



        return population.get(index);
    }


    /**
     *
     * @param mom->the first parent
     * @param dad->the second parent
     * @return two children (the result of parents crossOver) or the same parents if the crossOver haven't took place
     */

    @Override
    protected Pair<Individual <Integer,Double>, Individual<Integer,Double> > crossOver(Individual<Integer,Double> mom, Individual<Integer,Double> dad){

        String momBinaryRep = Integer.toString(mom.getValue(),2);//representing mom binary
        String dadBinaryRep = Integer.toString(dad.getValue(),2);//representing dad binary

        momBinaryRep = resize(momBinaryRep);
        dadBinaryRep = resize(dadBinaryRep);


        String first = combine(momBinaryRep,dadBinaryRep);
        String second = combine(dadBinaryRep,momBinaryRep);


        if(Integer.parseInt(first,2) >= concreteProblem.size())first = momBinaryRep;//see if the child is from the same population
        if(Integer.parseInt(second,2) >= concreteProblem.size())second = dadBinaryRep;

        return  new Pair<>(new Individual<>(Integer.parseInt(first,2)),new Individual<>(Integer.parseInt(second,2)));
    }


    /**
     * uses the random resetting method
     * @param individual ->the subject
     * @param probability->the probability that the subject to be modified
     * @return a new individual with another characteristics
     */

    @Override
    protected Individual<Integer,Double> mutate(Individual<Integer,Double> individual, double probability){

        Random random = new Random(System.nanoTime());

        StringBuilder builder = new StringBuilder( Integer.toString(individual.getValue()) );

        for(int i = 0; i < builder.length(); ++i){
            float p = random.nextFloat();

            int gen = random.nextInt(10);

            if(p > probability )continue;

            char value = builder.charAt(i);

            builder.replace(i,i + 1,gen+"");

            if(Integer.parseInt(builder.toString()) >= concreteProblem.size()){
                builder.replace(i,i+1,value+"");
                --i;
            }
        }

        return  new Individual<>(Integer.parseInt(builder.toString()));
    }


    @Override
    public Double search(List<Double> inWhat) throws  Exception {

        concreteProblem = inWhat;
        dataList.clear();

        checkAllParameters();

        initializePopulation();
        evaluatePopulation();

        for(int i = 1; i <= numberOfGenerations; ++i){

            Individual<Integer,Double> mom = select(),dad = select();//selecting the mom and the dad

            Pair <Individual<Integer,Double>, Individual<Integer,Double> > child = crossOver(mom,dad);//crossover mom and dad

            Individual<Integer,Double> firstInd = mutate(child.getKey(),mutationProbability); //mutate first child
            Individual<Integer,Double> secondInd = mutate(child.getValue(),mutationProbability); //mutate last child

            singleEvaluate(firstInd);//evaluate the first child
            singleEvaluate(secondInd);//evaluate the second child

            Individual<Integer,Double> best = best(firstInd,secondInd),worst = getWorst();//choose the best  from the parent's children and the worst individual from the population


            if(i % step == 0 || i == 1)dataList.add(new XYChart.Data<>(i+"",population.stream().min(Comparator.comparingDouble(Individual<Integer,Double>::getFitness)).get().getFitness()));


            if(worst.getFitness() <= best.getFitness()){
                continue;
            }


            worst.setFitness(best.getFitness());//replacing the worst from the population with the best child
            worst.setValue(best.getValue());
        }

        return  population.stream().min(Comparator.comparingDouble(Individual<Integer,Double>::getFitness)).get().getFitness();//getting the best solution from the last pop
    }

    @Override
    public List<XYChart.Data<String, Number>> evolution() {
        return dataList;
    }

    @Override
    public void setEvolutionStep(int step) {
        this.step = step;
    }


    /**
     * Set's the selection  number
     * @param selectionNumber->the value of selectionNumber
     */

    public void setSelectionNumber(int selectionNumber) {
        this.selectionNumber = selectionNumber;
    }

    /**
     * Sets the number of points in witch the chromosome will be divided
     * @param splitPointsNumber ->that number
     */

    public void setSplitPointsNumber(int splitPointsNumber) {
        this.splitPointsNumber = splitPointsNumber;
    }

    /**
     * It is recommanded that the dimension of the chromosome to be the log2 (currentProblem.size() + 1)
     * @param dimCrom ->the dimension of a chromosome
     */

    public void setDimCrom(int dimCrom) {
        this.dimCrom = dimCrom;
    }

    public void setMutationProbability(float mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public void setNumberOfGenerations(int numberOfGenerations) {
        this.numberOfGenerations = numberOfGenerations;
    }

    public void setDimPop(int dimPop) {
        this.dimPop = dimPop;
    }


    /**
     *
     * @param list->the problems that we want to solve
     * @return a number representing the best value for a chromosome dimension
     */

    public static int getDefaultChromosomeDimension(List <?> list){

        int number = list.size(),log2 = 0;

        while(number > 0) {
            number /= 2;
            ++log2;
        }

        return log2;
    }
}
