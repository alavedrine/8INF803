package com.bd.front;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchController {
    private ArrayList<String> keywords = new ArrayList<>();
    private ArrayList allClasses = new ArrayList(Arrays.asList("Adept", "Alchemist", "Antipaladin", "Bard", "Bloodrager", "Cleric", "Druid", "Inquisitor", "Magus", "Oracle", "Paladin", "Ranger", "Shaman", "Sorcerer", "Summoner", "Witch", "Wizard"));
    private ArrayList<String> selectedClasses = new ArrayList<>();

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

        return tfName.getText() != null ? tfName.getText() : "No particular name";
    }

    public String getLogic() {
        // default logic is "OR"
        return tgLogic.getSelectedToggle() == null ? "OR" : ((ToggleButton)tgLogic.getSelectedToggle()).getText();
    }

    public int getLevel() {
        return (int)sliderLevel.getValue();
    }

    private void addComponent(ToggleButton tb, List<String> arr) {
        if (tb.isSelected()) {
            arr.add(tb.getText());
        }
    }

    @FXML
    protected void deleteLastKeyword(ActionEvent actionEvent) {
        if (keywords.size() > 0) {
            keywords.remove(keywords.size() - 1);
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
    }

    @FXML
    public void reset(ActionEvent actionEvent) {
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
}