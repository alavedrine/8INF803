package com.bd.front;

import com.bd.front.context.LocalSparkSession;
import com.bd.front.jobs.SearchJob;
import com.bd.front.struct.FilterModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    private ArrayList<String> keywords = new ArrayList<>();
    private ArrayList allClasses = new ArrayList(Arrays.asList("Adept", "Alchemist", "Antipaladin", "Bard", "Bloodrager", "Cleric", "Druid", "Inquisitor", "Magus", "Oracle", "Paladin", "Ranger", "Shaman", "Sorcerer", "Summoner", "Witch", "Wizard"));
    private ArrayList<String> selectedClasses = new ArrayList<>();

    LocalSparkSession sparkSession;
    SearchJob searchJob;

    FilterModel filterModel;

    @FXML
    private ToggleGroup tgLogic;

    @FXML
    private TextField tfKeywords;
    @FXML
    private Label lKeywords;

    @FXML
    private Label sliderValue;
    @FXML
    private Slider sliderLevel;

    @FXML
    private ToggleButton tbV;
    @FXML
    private ToggleButton tbM;
    @FXML
    private ToggleButton tbS;

    @FXML
    private TextField tfName;

    @FXML
    private Label lResult;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    protected void onSliderMoved() {
        sliderValue.setText(String.valueOf(getLevel()));
    }

    @FXML
    public void onEnter(ActionEvent ae){
        keywords.add(tfKeywords.getText());
        filterModel.getKeywords().add(tfKeywords.getText());
        tfKeywords.clear();
        lKeywords.setText(String.join(", ", keywords));
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public ArrayList<String> getSelectedClasses() {
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof CheckBox) {
                if (((CheckBox) node).isSelected() && !((CheckBox) node).getText().equals("ALL")) {
                    selectedClasses.add(((CheckBox) node).getText());
                    filterModel.getClasses().add(((CheckBox) node).getText());
                }
            }
        }
        return selectedClasses;
    }

    public List<String> getComponents() {
        List<String> components = new ArrayList<>();
        addComponent(tbV, components);
        addComponent(tbM, components);
        addComponent(tbS, components);
        return components;
    }

    public String getName() {
        filterModel.setName(tfName.getText());
        return tfName.getText() != null ? tfName.getText() : "No particular name";
    }

    public String getLogic() {
        String logic = tgLogic.getSelectedToggle() == null ? "OR" : ((ToggleButton)tgLogic.getSelectedToggle()).getText();
        filterModel.setClassesLogic(logic);
        // default logic is "OR"
        return logic;
    }

    public int getLevel() {
        filterModel.setMinLevel((int) sliderLevel.getValue());
        return (int)sliderLevel.getValue();
    }

    private void addComponent(ToggleButton tb, List<String> arr) {
        if (tb.isSelected()) {
            filterModel.getComponents().add(tb.getText());
            arr.add(tb.getText());
        }
    }

    @FXML
    protected void deleteLastKeyword(ActionEvent actionEvent) {
        if (keywords.size() > 0) {
            keywords.remove(keywords.size() - 1);
            filterModel.getKeywords().remove(keywords.size() - 1);
            lKeywords.setText(String.join(", ", keywords));
        }
    }

    @FXML
    protected void onCheckBoxChecked(ActionEvent actionEvent) {
        CheckBox selectedCheckBox = ((CheckBox)actionEvent.getSource());
        if (selectedCheckBox.isSelected()) {
            for (Node node : anchorPane.getChildren()) {
                if (node instanceof CheckBox) {
                    ((CheckBox) node).setSelected(true);
                }
            }
        } else {
            for (Node node : anchorPane.getChildren()) {
                if (node instanceof CheckBox) {
                    ((CheckBox) node).setSelected(false);
                }

            }
        }
    }

    @FXML
    public void search(ActionEvent actionEvent) {
        lResult.setText("Searching for : " + getName() + "\n"
                        + "Level range is : " + getLevel() + " - 9\n"
                        + "Components are : " + String.join(", ", getComponents()) + "\n"
                        + "Usable by : " + String.join(", ", getSelectedClasses()) + "\n"
                        + "With logic : " + getLogic() + "\n"
                        + "With keywords : " + String.join(", ", getKeywords()));

        new Thread(() -> searchJob.startJob(filterModel)).start();
    }

    @FXML
    public void reset(ActionEvent actionEvent) {
        filterModel = new FilterModel();
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof Slider) {
                ((Slider) node).setValue(0);
            }
            if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false);
            }
            if (node instanceof TextField) {
                ((TextField) node).clear();
            }
            if (node instanceof ToggleButton) {
                ((ToggleButton) node).setSelected(false);
            }
            if (node instanceof Label) {
                ArrayList<String> labelsToReset = new ArrayList<>(Arrays.asList("lResult", "sliderValue", "lKeywords"));
                if (node.getId() != null && labelsToReset.contains(node.getId())) {
                    ((Label) node).setText("");
                }
            }
        }
        selectedClasses.clear();
        keywords.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sparkSession = LocalSparkSession.getInstance();
        searchJob = new SearchJob(sparkSession.getSparkSession());
        filterModel = new FilterModel();
    }
}