package com.rgy.codebuilder.service;

import com.rgy.codebuilder.dao.AppDao;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/4.
 */
public class AppService {

    private AppDao appDao;

    public boolean tableIsNotExists(){
        appDao = new AppDao();
        String [] tableNames = new String[]{"TEMP","TEMP_DATA"};
        boolean isNotExists = false;
        for (String tableName:tableNames) {
            isNotExists = appDao.tableIsNotExists(tableName);
        }
        if(isNotExists){
            appDao.initTable();
        }
        return isNotExists;
    }

    public Map<String,List<String>> getLabelAndValue(ObservableList<Node> children) {
        Map<String,List<String>> result = new HashMap<>();

        //获取页面上动态添加的值
        List<String> labels = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            HBox hbox = (HBox)children.get(i);
            for (int j = 0; j < hbox.getChildren().size(); j++) {
                ObservableList<Node> chil = hbox.getChildren();
                if(j==0){
                    Label label = (Label)chil.get(j);
                    labels.add(label.getText());

                }
                if(j==1){
                    TextField value = (TextField)chil.get(j);
                    values.add(value.getText());
                }

            }
        }
        result.put("labels", labels);
        result.put("values", values);
        return result;
    }



    public String getFileData(String tempValue, List<String> labels, List<String> values) {
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            String value = values.get(i);
            tempValue = tempValue.replaceAll("\\$\\{("+label+")\\}", value);
        }
        return tempValue;
    }
}
