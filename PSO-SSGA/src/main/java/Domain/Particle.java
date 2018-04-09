package Domain;

public class Particle {

    private int position;

    private double fitness;

    private double velocity;

    private int particleBestPosition;

    private int globalBestPosition;


    public Particle(int position, double fitness, double velocity) {
        this.position = position;
        this.fitness = fitness;
        this.velocity = velocity;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public int getParticleBestPosition() {
        return particleBestPosition;
    }

    public int getGlobalBestPosition() {
        return globalBestPosition;
    }

    public void setGlobalBestPosition(int globalBestPosition) {
        this.globalBestPosition = globalBestPosition;
    }

    public void setParticleBestPosition(int particleBestPosition) {
        this.particleBestPosition = particleBestPosition;
    }

    @Override
    public String toString(){
        return position + " " + fitness + " "  + velocity;
    }
}
