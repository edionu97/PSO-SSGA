package SearchMethods;

import javafx.scene.chart.XYChart;

import java.util.List;

public interface SearchMethod<returnType> {

    returnType search(List<Double> inWhat) throws  Exception;

    List< XYChart.Data <String,Number> >evolution();

    void setEvolutionStep(int step);

}

