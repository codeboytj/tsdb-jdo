package cumt.tj.hbase.tsdb.jdo.annotation.processor;

import cumt.tj.hbase.tsdb.jdo.annotation.Metric;
import cumt.tj.hbase.tsdb.jdo.annotation.TSData;
import cumt.tj.hbase.tsdb.jdo.annotation.Tag;
import cumt.tj.hbase.tsdb.jdo.annotation.Time;
import cumt.tj.hbase.tsdb.jdo.pojos.RowContainer;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.util.Bytes;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by sky on 17-6-18.
 */
public class TSDataProcessor<T> implements Processor<T>{

    //存储“方法名-方法”的键值对，以便获取方法，比如方法名“getTime”为key的方法getTime()
    Map<String,Method> methodMap=new HashMap<>();
    List<Field> metricFieldList=new ArrayList<>();
    List<Field> tagFieldList=new ArrayList<>();
    Field timeField;

    public TSDataProcessor(Class<T> clz) {

        //查看clz是否标注了TSData注解
        if(clz.getAnnotation(TSData.class)==null){
            throw new RuntimeException("类并不是@TSData注解的类");
        }

        //充填methodMap
        Method[] ms=clz.getDeclaredMethods();
        for (Method m:ms
             ) {
            methodMap.put(m.getName(),m);
        }

        //获取Tag域与Metric域
        for (Field field:clz.getDeclaredFields()
                ) {
            if (field.getAnnotation(Tag.class) != null) {
                tagFieldList.add(field);
            } else if (field.getAnnotation(Metric.class) != null) {
                metricFieldList.add(field);
            } else if(field.getAnnotation(Time.class)!=null){
                timeField=field;
            }
        }
    }

    /**
     * 根据一个@TSData标注的pojo对象，生成可以分别存储到tsdb-uid的2个Row与tsdb的1个Row
     * @param tSData 以@TSData注解的pojo对象
     * @param counter 这个是指tsdb-uid表中的已经存在的tagv数量，用来计算rowkey
     * @return 存储了uidList与tsdbList以及插入这些数据后的counter
     */
    @Override
    public RowContainer getRows(T tSData, int counter) {

        //这个是要返回的东东
        RowContainer rowContainer=new RowContainer();
//        Map<String,List<Row>> map=new HashMap<>();
        List<Row> uidList=new LinkedList<>();
        List<Row> tsdbList=new LinkedList<>();

        String value;String counterStr;
        for (Field tagField: tagFieldList
                ) {
            //获取属性值
            value=getFieldValue(methodMap,tagField,tSData);
            //对于标注了@Tag注解的field
            //可以为这个建立2个Row了，一个是name:tagv，一个是id:tagv
            counter++;
            counterStr=String.format("%04d", counter);
            uidList.add(new Put(Bytes.toBytes(counterStr)).addColumn(Bytes.toBytes("name"),Bytes.toBytes("tagv"),Bytes.toBytes(value)));
            uidList.add(new Put(Bytes.toBytes(value)).addColumn(Bytes.toBytes("id"),Bytes.toBytes("tagv"),Bytes.toBytes(counterStr)));
        }

        for (Field metricField: metricFieldList
             ) {
            //获取属性值
            value=getFieldValue(methodMap,metricField,tSData);
            //对于标注了@Metric注解的field
            //可以为这个建立1个Row了，是tsdb表中的t:value，此时的rowkey要特别注意，关系到HBase操作的性能
            tsdbList.add(new Put(Bytes.toBytes("000")).addColumn(Bytes.toBytes("t"),Bytes.toBytes("value"),Bytes.toBytes(value)));
        }

        rowContainer.setUidList(uidList);
        rowContainer.setTsdbList(tsdbList);
        rowContainer.setCounter(counter);
        return rowContainer;
    }

    @Override
    public Map<String, List<Row>> getRows(Collection<T> tSDatas, int counter) {
        //这个是要返回的东东
        Map<String,List<Row>> map=new HashMap<>();
        List<Row> uidList=new LinkedList<>();
        List<Row> tsdbList=new LinkedList<>();

        for (T tSData:tSDatas
             ) {

        }

        map.put("uidList",uidList);map.put("tsdbList",tsdbList);
        return map;
    }

    /**
     * 通过反射机制调用getter方法，获得某个域的值
     * 这里假设每个field都是String类型的，由于到HBase的数据都是字符串，所以这个假设是合理的
     * @param methodMap
     * @param field
     * @param t
     * @return
     */
    public String getFieldValue(Map<String,Method> methodMap, Field field, T t){
        String value=null;

        //找到该field的getter方法
        Method m=getterMethod(methodMap,field);
        //获取该属性的值
        m.setAccessible(true);
        try {
            value=(String)m.invoke(t,new Object[]{});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return value;
    }


    /**
     * 找到field对应的getter方法
     * @param methodMap
     * @param field
     * @return
     */
    public Method getterMethod(Map<String,Method> methodMap,Field field){
        return methodMap.get(getterMethodName(field.getName()));
    }

    /**
     *相应的getter方法名为get+field名的首字母变大写
     * @param fieldName
     * @return
     */
    protected String getterMethodName(String fieldName){
        StringBuilder sb=new StringBuilder();

        //获取首字母的大写
        char newFirstChar=Character.toUpperCase(fieldName.charAt(0));

        //返回“get"+首字母变为大写的field名
        return sb.append("get").append(newFirstChar).append(fieldName.substring(1)).toString();
    }
}
