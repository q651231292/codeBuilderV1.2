package com.rgy.codebuilder.service;

import com.rgy.codebuilder.dao.TempDao;
import com.rgy.codebuilder.model.Temp;
import com.rgy.codebuilder.util.ValiTool;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/4.
 */
public class TempService {

    private TempDao tempDao;

    public ObservableList<Temp> queryTemps() {
        tempDao = new TempDao();
        List<Map<String, String>> temps = tempDao.queryTemps();
        ObservableList<Temp> obList = FXCollections.observableArrayList();
        for (Map<String, String> temp : temps) {
            StringProperty tempId = new SimpleStringProperty(temp.get("TEMP_ID"));
            StringProperty tempName = new SimpleStringProperty(temp.get("TEMP_NAME"));
            obList.add(new Temp(tempId, tempName));
        }
        return obList;
    }
    public Map<String,List<String>> getLabelAndValue(ObservableList<Node> children){
        Map<String,List<String>> result = new HashMap<>();
        //获取页面上动态添加的值
        List<String> labels = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<String> tempDataIds = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            HBox hbox = (HBox)children.get(i);
            for (int j = 0; j < hbox.getChildren().size(); j++) {
                //模板数据ID
                if(j==0){
                    TextField label = (TextField)hbox.getChildren().get(j);
                    tempDataIds.add(label.getText());

                }
                //模板名称,作用是生成文件作为后缀
                if(j==1){
                    TextField label = (TextField)hbox.getChildren().get(j);
                    labels.add(label.getText());
                }
                //模板的值
                if(j==2){
                    TextArea value = (TextArea)hbox.getChildren().get(j);
                    values.add(value.getText());
                }
            }
        }
        result.put("labels", labels);
        result.put("values", values);
        result.put("tempDataIds", tempDataIds);
        return result;

    }
    public boolean saveTempAndData(String tempName, List<String> labels, List<String> values) {
        tempDao = new TempDao();
        String tempId = UUID.randomUUID().toString();
        boolean isSuccess = tempDao.saveTemp(tempId, tempName);
        if (isSuccess) {
            for (int i = 0; i < labels.size(); i++) {
                String label = labels.get(i);
                String value = values.get(i);
                String tempDataId = UUID.randomUUID().toString();
                isSuccess = tempDao.saveTempData(tempDataId, tempId, label, value);
            }

        }
        return isSuccess;
    }

    public Map<String,String> queryTemp(String id) {
        tempDao = new TempDao();
        return tempDao.queryTemp(id);
    }

    public List<Map<String,String>> queryTempDatas(String id) {
        tempDao = new TempDao();
        return tempDao.queryTempDatas(id);
    }

    public boolean updateTempAndDatas(String tempId, String tempName, List<String> tempDataIds, List<String> labels, List<String> values) {
        tempDao = new TempDao();
        boolean isSuccess = tempDao.updateTemp(tempId,tempName);
        if(isSuccess){
            for (int i = 0; i < tempDataIds.size(); i++) {
                String tempDataId = tempDataIds.get(i);
                String label = labels.get(i);
                String value = values.get(i);
                if(ValiTool.strIsNull(tempDataId)){
                    tempDataId =UUID.randomUUID().toString();
                    isSuccess = tempDao.saveTempData(tempDataId,tempId,label,value);
                }else{
                    isSuccess = tempDao.updateTempData(label,value,tempDataId);
                }

            }

        }
        return isSuccess;
    }

    public boolean deleteTemp(String id) {
        tempDao = new TempDao();
        return tempDao.deleteTemp(id);
    }

    public Set<String> getValueMarks(List<Map<String, String>> tempDatas) {
        String values = "";
        for (int i = 0; i < tempDatas.size(); i++) {
            Map<String,String> tempData = tempDatas.get(i);
            String value = tempData.get("VALUE");
            values+=value;
        }
        Set<String> set = new TreeSet<>();
        String regex = "\\$\\{(.*?)\\}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(values); // 获取 matcher 对象
        while(m.find()) {
            String mark = m.group(1);
            set.add(mark);
        }
        return set;
    }

    public boolean deleteTempData(String id) {
        tempDao = new TempDao();
        return tempDao.deleteTempData(id);
    }
}