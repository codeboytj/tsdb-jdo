package cumt.tj.tsdb.jdo.pojos;

import cumt.tj.hbase.tsdb.jdo.annotation.*;

/**
 * Created by sky on 17-5-10.
 */
@TSData
public class Gas {
    //矿名
    @Tag(key = "矿名",index = 1)
    private String mineName;
    //点号
    @Tag(key = "点号",index = 3)
    private String pointNum;
    //安装地址
    @Tag(key = "安装地址",index = 4)
    private String instAddress;
    //检测类型
    @Tag(key = "类型",index = 2)
    private String type;
    //监测值
    @Metric(name = "监测值",index = 0)
    private String concentration;
    //单位
    @Tag(key = "单位",index = 5)
    private String unit;
    //上传时间
    @Time(index = 6)
//    @Tag(key = "上传时间")
    private String time;

    public static void setItem(Gas gas,String key,String value){

        switch (key){
            case "矿名":gas.setMineName(value);
            case "点号":gas.setPointNum(value);
            case "安装地址":gas.setInstAddress(value);
            case "类型":gas.setType(value);
            case "监测值":gas.setConcentration(value);
            case "单位":gas.setUnit(value);
            case "上传时间":gas.setTime(value);
        }

    }


    public String getMineName() {
        return mineName;
    }

    public void setMineName(String mineName) {
        this.mineName = mineName;
    }

    public String getPointNum() {
        return pointNum;
    }

    public void setPointNum(String pointNum) {
        this.pointNum = pointNum;
    }

    public String getInstAddress() {
        return instAddress;
    }

    public void setInstAddress(String instAddress) {
        this.instAddress = instAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
