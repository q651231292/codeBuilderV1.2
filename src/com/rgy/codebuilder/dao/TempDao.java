package com.rgy.codebuilder.dao;

import com.rgy.codebuilder.App;
import com.rgy.codebuilder.util.Jdbc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2017/8/4.
 */
public class TempDao {

    private Jdbc jdbc;

    public List<Map<String,String>> queryTemps(){
        jdbc = new Jdbc();
        return jdbc.query("select * from temp");
    }

    public boolean saveTemp(String tempId,String tempName){
        jdbc = new Jdbc();

        return jdbc.add("insert into temp values('"+tempId+"','"+tempName+"')");
    }

    public boolean saveTempData(String tempDataId, String tempId, String label, String value) {
        jdbc = new Jdbc();
        return jdbc.add("insert into temp_data values('"+tempDataId+"','"+tempId+"','"+label+"','"+value+"')");
    }

    public Map<String,String> queryTemp(String id) {
        jdbc = new Jdbc();
        return jdbc.queryOne("select * from temp where temp_id = '"+id+"'");
    }

    public List<Map<String,String>> queryTempDatas(String id) {
        jdbc = new Jdbc();
        return jdbc.query("SELECT * FROM TEMP_DATA WHERE TEMP_ID = '"+id+"' ");
    }

    public boolean updateTemp(String tempId, String tempName) {
        jdbc = new Jdbc();
        return jdbc.mod("update temp set temp_name = '"+tempName+"' where temp_id='"+tempId+"'");
    }

    public boolean updateTempData(String label, String value, String tempDataId) {
        jdbc = new Jdbc();
        return jdbc.mod("update temp_data set label='"+label+"',value='"+value+"' where temp_data_id='"+tempDataId+"'");
    }

    public boolean deleteTemp(String id) {

        jdbc = new Jdbc();
        boolean isSuccess = jdbc.del("delete from temp_data where temp_id='"+id+"'");
        isSuccess = jdbc.del("delete from temp where temp_id='"+id+"'");
        return isSuccess;

    }
}
