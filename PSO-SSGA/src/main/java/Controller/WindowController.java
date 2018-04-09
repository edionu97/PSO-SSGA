package Controller;

import MethodImplementation.PSO;
import MethodImplementation.SSGA;
import MethodImplementation.SSGAFct;
import SearchMethods.SearchMethod;
import Utils.FileParser;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WindowController implements Initializable{

    @FXML
    private LineChart <String,Number> lineChart;

    @FXML
    private JFXButton buttonSearch;

    @FXML
    private JFXComboBox <String> comboBox;


    @FXML
    private AnchorPane anchorPaneSSGA;

    @FXML
    private AnchorPane anchorPanePSO;

    @FXML
    private JFXTextField textFieldPop;

    @FXML
    private JFXTextField textFieldIt;

    @FXML
    private JFXTextField textFieldSplit;

    @FXML
    private JFXTextField textFieldSel;

    @FXML
    private JFXTextField textFieldProb;

    @FXML
    private JFXTextField textFieldVeloc;

    @FXML
    private JFXTextField textFieldC1;

    @FXML
    private JFXTextField textFieldC2;

    @FXML
    private JFXTextField textFieldW;

    @FXML
    private JFXTextField textFieldChromo;

    @FXML
    private JFXTextField textFieldScale;

    @FXML
    private Label resultLabel;

    @FXML
    private JFXButton buttonLocation;

    @FXML
    private JFXCheckBox checkBox1D;

    @FXML
    private JFXCheckBox checkBox2D;

    @FXML
    private JFXCheckBox checkBoxFct;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXProgressBar progressBar;

    private XYChart.Series<String,Number> series = new XYChart.Series<>();

    private FileChooser chooser = new FileChooser();

    private String location;

    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        resultLabel.setText("Choose a data source");

        series.setName("Iteration");

        lineChart.getData().add(series);

        buttonSearch.setOnAction(this::solve);

        comboBox.getItems().addAll("PSO","SSGA");

        comboBox.getSelectionModel().select(1);

        textFieldIt.setText("1000");
        textFieldSplit.setText("2");
        textFieldSel.setText("10");
        textFieldProb.setText("0.8");

        textFieldVeloc.setText("0");
        textFieldC1.setText("1.5");
        textFieldC2.setText("2.3");
        textFieldW.setText("0.7");

        textFieldChromo.setText("default");

        comboBox.setOnAction(x->{
            if(comboBox.getValue().equals("PSO")){
                anchorPanePSO.setDisable(false);
                anchorPaneSSGA.setDisable(true);
                return;
            }

            anchorPaneSSGA.setDisable(false);
            anchorPanePSO.setDisable(true);
        });

        anchorPanePSO.setDisable(true);


        buttonLocation.setOnAction(this::handleLocation);

        checkBox1D.setOnAction(ev->{
            checkBox2D.setSelected(false);
            if(this.location == null)resultLabel.setText("Choose a data source");
            checkBoxFct.setSelected(false);
            checkBox1D.setSelected(true);
            textFieldSplit.setDisable(false);
            comboBox.setDisable(false);
            buttonLocation.setDisable(false);
        });

        checkBox2D.setOnAction(ev->{
            if(this.location == null)resultLabel.setText("Choose a data source");
            checkBox1D.setSelected(false);
            checkBoxFct.setSelected(false);
            checkBox2D.setSelected(true);
            textFieldSplit.setDisable(false);
            comboBox.setDisable(false);
            buttonLocation.setDisable(false);
        });

        checkBoxFct.setOnAction(ev->{

            if(checkBoxFct.isSelected()){
                resultLabel.setText("Function: sum(x(i) ^ 2), i=1: dimCrom , x(i) in [-5.12,5.12]");
                textFieldSplit.setDisable(true);
                comboBox.setValue("SSGA");
                comboBox.setDisable(true);
                buttonLocation.setDisable(true);
            }
            checkBox1D.setSelected(false);
            checkBox2D.setSelected(false);
            checkBoxFct.setSelected(true);
        });
        checkBox1D.setSelected(true);

        progressBar.setVisible(false);
    }

    private void handleLocation(ActionEvent event){
        File file  = chooser.showOpenDialog(stage);

        if(file == null)return;

        location = file.getPath();

        resultLabel.setText("Date from: " + location);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private List < Double > readData(){

        List <Double> list = new ArrayList<>();

        try {
                FileParser fileReader = new FileParser(1024 * 1024,location);

                int N = Integer.parseInt(fileReader.getBufferedReader().readLine().trim());

                N  = checkBox2D.isSelected() ?  N * Integer.parseInt(fileReader.getBufferedReader().readLine().trim()) : N;

               list.addAll(fileReader.readFile());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private SearchMethod <Double> buildSolver(List <Double> list){

        if(checkBoxFct.isSelected()){

            SSGAFct fct = new SSGAFct();

            fct.setEvolutionStep(Integer.parseInt(textFieldScale.getText()));
            fct.setNumberOfGenerations(Integer.parseInt(textFieldIt.getText()));
            fct.setSelectionNumber(Integer.parseInt(textFieldSel.getText()));
            fct.setDimPop(Integer.parseInt(textFieldPop.getText()));
            fct.setMutateProb(Double.parseDouble(textFieldProb.getText()));
            fct.setDimCrom(Integer.parseInt(textFieldChromo.getText()));

            fct.setEps(0.5);
            fct.setA(-5.12);
            fct.setB(5.12);

            fct.setToEvaluate(x->{
                double sum = 0.0;
                for(double aX : x) sum +=aX * aX;
                return sum;
            });

            return fct;
        }

        if(comboBox.getValue().equals("SSGA")){

            SSGA ssga = new SSGA();

            if(textFieldChromo.getText().equals("default")){
                ssga.setDimCrom(SSGA.getDefaultChromosomeDimension(list));
            }
            else ssga.setDimCrom(Integer.parseInt(textFieldChromo.getText()));


            ssga.setSplitPointsNumber(Integer.parseInt(textFieldSplit.getText()));
            ssga.setDimPop(Integer.parseInt(textFieldPop.getText()));
            ssga.setSelectionNumber(Integer.parseInt(textFieldSel.getText()));
            ssga.setMutationProbability(Float.parseFloat(textFieldProb.getText()));
            ssga.setNumberOfGenerations(Integer.parseInt(textFieldIt.getText()));
            ssga.setEvolutionStep(Integer.parseInt(textFieldScale.getText()));

            return ssga;
        }

        PSO pso = new PSO();

        pso.setDimPop(Integer.parseInt(textFieldPop.getText()));
        pso.setInitialVelocity(Double.parseDouble(textFieldVeloc.getText()));
        pso.setRepetitions(Integer.parseInt(textFieldIt.getText()));
        pso.setCognitiveFactor(Double.parseDouble(textFieldC1.getText()));
        pso.setInertiaFactor(Double.parseDouble(textFieldW.getText()));
        pso.setSocialFactor(Double.parseDouble(textFieldC2.getText()));
        pso.setEvolutionStep(Integer.parseInt(textFieldScale.getText()));



        return pso;
    }

    private void handleError(String errorMessage){

        JFXDialogLayout layout = new JFXDialogLayout();

        layout.setHeading(new Text("Error"));

        layout.setBody(new Text(errorMessage));

        Button button = new JFXButton("Okay");

        layout.setActions(button);

        JFXDialog dialog = new JFXDialog(stackPane,layout,JFXDialog.DialogTransition.CENTER);

        button.setOnAction(ev->dialog.close());

        dialog.show();
    }

    private void solve(ActionEvent event){

        if(location == null && !checkBoxFct.isSelected()){
            handleError("You must select a location in order to proceed");
            return;
        }


        progressBar.setVisible(true);
        buttonSearch.setDisable(true);
        buttonLocation.setDisable(true);

        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                try {
                    List<Double> list = checkBoxFct.isSelected() ? null : readData();

                    SearchMethod<Double> solver = buildSolver(list);


                    Platform.runLater(() -> {

                        try {
                            resultLabel.setText("Min: " + String.format("%f", solver.search(list)));
                        } catch (Exception e) {
                            handleError(e.getMessage());
                        }

                        series.getData().clear();
                        series.getData().addAll(solver.evolution());

                        progressBar.setVisible(false);
                        buttonLocation.setDisable(false);
                        buttonSearch.setDisable(false);
                    });

                }catch (Exception e){
                    Platform.runLater(()-> {
                        handleError(e.getMessage());
                        progressBar.setVisible(false);
                        buttonSearch.setDisable(false);
                        buttonSearch.setDisable(false);
                    });
                }

                return  null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());

        new Thread(task).start();
    }
}
