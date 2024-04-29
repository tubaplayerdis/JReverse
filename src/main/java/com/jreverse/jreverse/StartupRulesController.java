package com.jreverse.jreverse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class StartupRulesController {
    //New Rules
    @FXML
    private TextArea NewRuleBytecodesPreviewBox;
    @FXML
    private TextField NewRuleNameField;

    //Old Rules
    @FXML
    private ListView<String> RulesListView;

    @FXML
    private TextArea OldRulePreviewBox;

    @FXML
    private TextField OldRuleNameField;

    //Important
    public static List<StartupRule> rulesList = new ArrayList<>();

    public static StartupRule[] getRules(){
        StartupRule[] returnlist = rulesList.toArray(new StartupRule[0]);
        return  returnlist;
    }


    //Old Rule Methods
    @FXML
    private void SelectOldRule(){
        String CurrentRuleName = null;
        CurrentRuleName = RulesListView.getSelectionModel().getSelectedItem();
        if(CurrentRuleName.isBlank() || CurrentRuleName.isEmpty() || Objects.isNull(CurrentRuleName)){
            OldRuleNameField.setText("Please Select a Rule!");
            OldRulePreviewBox.setText("Please Select a Rule!");
            return;
        }

        StartupRule currentRule = null;

        if(rulesList.isEmpty()) return;

        for(StartupRule rule : rulesList){
            if(Objects.equals(rule.ClassName, CurrentRuleName)) currentRule = rule;
        }

        if(Objects.isNull(currentRule)){
            OldRuleNameField.setText("Rule not found!");
            OldRulePreviewBox.setText("Rule not found!");
            return;
        }

        //Everything Successful
        OldRuleNameField.setText(currentRule.ClassName);
        String res = JReverseDecompiler.DecompileBytecodes(currentRule.ByteCodes);
        OldRulePreviewBox.setWrapText(false);
        OldRulePreviewBox.setText(res);

    }

    private String TempBytecodes = "";

    @FXML
    private void UploadBytecodes() throws IOException {
        JFileChooser j = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Class Files", "class");
        j.setFileFilter(filter);
        j.setFileSelectionMode(JFileChooser.FILES_ONLY);


        if(j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            //Get Data of file
            File file = j.getSelectedFile();
            FileInputStream fis = new FileInputStream(file);
            StringBuilder hexBuilder = new StringBuilder();

            int data;
            while ((data = fis.read()) != -1) {
                // Convert each byte to hexadecimal representation
                String hex = Integer.toHexString(data);

                // Append leading zero if necessary
                if (hex.length() == 1) {
                    hexBuilder.append('0');
                }

                hexBuilder.append(hex);
            }

            fis.close();

            String hexString = hexBuilder.toString().toUpperCase();
            TempBytecodes = hexString;

            String res = JReverseDecompiler.DecompileBytecodes(hexString);
            NewRuleBytecodesPreviewBox.setText(res);
        }
    }

    @FXML
    private void CreateRule() {
        StartupRule create = new StartupRule();
        create.ClassName = NewRuleNameField.getText();


        create.ByteCodes = TempBytecodes;
        TempBytecodes = "";

        if(create.ClassName.isBlank() || create.ClassName.isEmpty() || create.ByteCodes.isBlank() || create.ByteCodes.isEmpty()){
            NewRuleBytecodesPreviewBox.setText("Error with bytecodes or class name");
            return;
        }

        //Add it
        rulesList.add(create);

        RefreshRules();
    }

    @FXML
    private void RefreshRules()
    {
        ObservableList<String> names = FXCollections.observableArrayList();
        for(StartupRule rule: rulesList){
            names.add(rule.ClassName);
        }
        RulesListView.setItems(names);
    }
}
