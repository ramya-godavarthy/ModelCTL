package modelCTL_java.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import javafx.scene.layout.*;
import modelCTL_java.Model.CtlFormula;
import modelCTL_java.Model.KripkeStructure;
import modelCTL_java.Model.State;
import modelCTL_java.Model.Transition;
import java.util.List;

public class ViewGUI {
    public File selectedFile;
    //public KripkeStructure ks;
    public String kripkeStructureDefinition;
    public Stage stage;
    private static String readLineByLineJava8(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    private String fileChooser() throws Exception{
        FileChooser file_chooser = new FileChooser();
        file_chooser.setTitle("Load Model");
        file_chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        selectedFile = file_chooser.showOpenDialog(stage);
        if (selectedFile != null) {
            kripkeStructureDefinition = readLineByLineJava8(selectedFile.getPath());
            modelText.setText("Selected Model: " + selectedFile.getName());
            modelDescription();
            return selectedFile.getName();
        }
       else
           return "No File Selected";
    }

    private void modelDescription(){
        try {
            KripkeStructure ks = new KripkeStructure(kripkeStructureDefinition);
            String s1 = "STATES";
            String s2 = "--------------------------";
            String s3 = "TRANSITIONS";
            String st = "";
            String tr = "";
            for (State state : ks.getStates()) {
                String stName = state.getStateName();
                List atom = state.getAtoms();
                st = st + stName + atom + "\n";
            }
            for (Transition transition : ks.getTransitions()) {
                tr = tr + transition.getTransitionName() +"(" + transition.getFromState().getStateName() + "->" +
                        transition.getToState().getStateName() +")" + "\n";
            }
            modelText.appendText("\n" + s2 + "\n" + s1 + "\n" + st + "\n"+ s2);
            modelText.appendText("\n" + s3 + "\n" + tr + "\n" + s2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String ResultMessage(boolean isSatisfy)
    {
        if (isSatisfy)
            return "holds ";
        else
            return "does not hold ";
    }

    @FXML
    private MenuBar menuBar;

    @FXML
    public VBox VirtualBox;

    @FXML
    private void handleExit(ActionEvent event){
        System.exit(0);
    }

    @FXML
    private void handleCloseModel(ActionEvent event){
        modelText.clear();
        resultText.clear();
        tf1.getSelectionModel().clearSelection();
        tf1.getItems().clear();
        tf2.clear();
    }

    @FXML
    private SplitPane sp;

    @FXML
    private SplitPane splitPane;

    @FXML
    public void initialize() {
        sp.setDividerPositions(0.3f,0.9f);
        splitPane.setDividerPositions(0.18f,0.9f);
        modelText.setPrefHeight(400);
        modelText.setPrefWidth(400);
        resultText.setPrefHeight(400);
        resultText.setPrefWidth(400);
        resultText.setWrapText(true);
        resultText.setEditable(false);
    }

    @FXML
    private void handleAboutUsage(ActionEvent event){
        Dialog dialog = new Dialog();
        dialog.setTitle("CTL Model Equation");
        dialog.setContentText("phi ::= T | p | (!phi) | (phi && phi) | (phi || ph) | (phi -> phi)\n    | AXphi | EXphi | AFphi | EFphi | AGphi | EGphi | A[phiUphi] | E[phiUphi]\n\n" +
                "Parentheses are strictly enforced and white space is ignored.");
        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(ok);
        dialog.showAndWait();
    }

    @FXML
    private void handleAboutAction(ActionEvent event){
        Dialog author = new Dialog();
        author.setTitle("About Authors");
        author.setContentText("This application is contributed by:\n\n" +
                "Satya Raghava Sai Siva Srikar Kandarpa\n   bpw22@txstate.edu      A05098178\n\n" +
                "Govind Chowdary Narra\n   gcn16@txstate.edu      A04715642\n\n" +
                "Rancy Thankachan\n   r_t295@txstate.edu     A05091075\n\n" +
                "Udaya Ramya Godavarthy\n       u_g25@txstate.edu      A05070102\n\n" );

        ButtonType ok2 = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        author.getDialogPane().getButtonTypes().add(ok2);
        author.showAndWait();
    }
    @FXML
    private ComboBox<String> tf1;

    @FXML
    private TextField tf2;

    @FXML
    private TextArea resultText;

    @FXML
    private TextArea modelText;

    @FXML
    private void handleCheck(ActionEvent event) {
        try {
            KripkeStructure ks = new KripkeStructure(kripkeStructureDefinition);
            CtlFormula ctlFormula = new CtlFormula(tf2.getText(), new State(tf1.getValue().toString()), ks);
            boolean isSatisfy = ctlFormula.isSatisfy();
            String result = ResultMessage(isSatisfy);
            resultText.setText("Property " + tf2.getText() + " " + result + " in state " + tf1.getValue().toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("CTL Syntax Error");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
    }

    @FXML
    private void handleLoadModel(ActionEvent event)
    {
            try {
                String fileName = fileChooser();
                KripkeStructure ks = new KripkeStructure(kripkeStructureDefinition);
                for (State state : ks.getStates()) {
                    tf1.getItems().add(state.getStateName());
                }

            }catch (Exception e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Error Occurred");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
    }

    }
