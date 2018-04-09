package MethodImplementation;

import Domain.Particle;
import SearchMethods.EA;
import javafx.scene.chart.XYChart;

import java.util.*;

public class PSO extends EA<Double> {

    private int dimPop,repetitions;

    private List < Double > problem;

    private List <Particle> swarm = new ArrayList<>();

    private double socialFactor,cognitiveFactor,inertiaFactor;

    private double initialVelocity;

    private int step;

    private List < XYChart.Data <String,Number> > dataList = new ArrayList<>();


    private void checkParameters() throws  Exception{
        String error = "";
        if(dimPop > problem.size()) error += "The problem size  is smaller than the current  population dimension.Please decrease the population dimension\n";
        if(!error.equals(""))throw  new Exception(error);
    }


    /**
     * @param particle ->the particle witch we want to evaluate
     */

    private void singleEvaluate(Particle particle){
        particle.setFitness(
                problem.get(particle.getPosition())//the value is the number(real number) from the original vector
        );
    }


    /**
     *
     * @return the position of the best element form population
     */

    private int getBestFromPop(){

        return swarm.stream().min(Comparator.comparingDouble(Particle::getFitness)).get().getPosition();
    }

    /**
     *
     * @param poz ->the particle position
     * @return the particle fitness
     */

    private double getFitness(int poz){
        return problem.get(poz);
    }


    /**
     *
     * @param particle ->the current particle
     * @param bestFromPop ->the best particle's position (particle from the swarm (entire))
     */

    private void mutate(Particle particle,int bestFromPop){

        Random random = new Random(System.nanoTime());

        particle.setGlobalBestPosition(bestFromPop);

        double rand1 = random.nextDouble();
        double rand2 = random.nextDouble();

        while (rand1 == 0)rand1 = random.nextDouble();
        while(rand2 == 0) rand2 = random.nextDouble();

        double velocity = inertiaFactor *  particle.getVelocity() + cognitiveFactor * rand1 * (particle.getParticleBestPosition() - particle.getPosition()) + socialFactor * rand2 * (particle.getGlobalBestPosition() - particle.getPosition());

        int newPoz = particle.getPosition() + (int)Math.round(velocity);

        if(newPoz < 0 || newPoz >= problem.size())return;


        double newFitness = getFitness(newPoz);

        particle.setParticleBestPosition(newFitness < particle.getFitness() ? newPoz : particle.getPosition());
        particle.setFitness(newFitness);
        particle.setPosition(newPoz);
    }




    /**
     * Initialize the particle population(position is a random number from the [0,problem.size() ) interval
     */

    @Override
    protected void initializePopulation(){

        Random random = new Random(System.nanoTime());

        BitSet bitSet = new BitSet(problem.size() + 1);

        for(int i = 1; i <= dimPop; ++i){

            int index = random.nextInt(problem.size());

            if(bitSet.get(index)){
                --i;
                continue;
            }

            bitSet.set(index);

            swarm.add(new Particle(index,0,initialVelocity));//the velocity is 0 and the fitness is uninitialized

            swarm.get(swarm.size() - 1).setParticleBestPosition(index);//considering that it's best position is the position on witch the particle is in this very moment
        }

    }

    @Override
    protected void evaluatePopulation(){

        swarm.forEach(this::singleEvaluate);//evaluating the population
    }

    @Override
    public Double search(List<Double> inWhat) throws  Exception {
        problem = inWhat;
        dataList.clear();

        checkParameters();

        initializePopulation();

        evaluatePopulation();

        for(int i = 1; i <= repetitions; ++i){
            int best = getBestFromPop();
            for(Particle p : swarm)mutate(p,best);
            if(i % step == 0 || i == 1 )dataList.add(new XYChart.Data<>(i + "",problem.get(best)));
        }


        return swarm.stream().min(Comparator.comparingDouble(Particle::getFitness)).get().getFitness();
    }

    @Override
    public List<XYChart.Data<String, Number>> evolution() {
        return dataList;
    }

    @Override
    public void setEvolutionStep(int step) {
        this.step = step;
    }







    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public void setSocialFactor(double socialFactor) {
        this.socialFactor = socialFactor;
    }

    public void setCognitiveFactor(double cognitiveFactor) {
        this.cognitiveFactor = cognitiveFactor;
    }

    public void setInertiaFactor(double inertiaFactor) {
        this.inertiaFactor = inertiaFactor;
    }

    public void setInitialVelocity(double initialVelocity) {
        this.initialVelocity = initialVelocity;
    }

    public void setDimPop(int dimPop) {
        this.dimPop = dimPop;
    }
}
