package SearchMethods;

import Domain.Individual;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class GA <returnType,position,value> extends EA<returnType> {

    protected int numberOfGenerations, dimPop;

    protected  List< Individual<position,value> > population = new ArrayList<>();

    protected  abstract  Individual<position,value>  select();

    protected abstract Pair< Individual<position,value>,Individual<position,value> >  crossOver(Individual<position,value> mom, Individual<position,value> dad);

    protected  abstract  Individual<position,value>  mutate( Individual<position,value> individual,double probability);
}
