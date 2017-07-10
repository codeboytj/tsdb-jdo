package cumt.tj.hbase.tsdb.jdo.annotation.processor;

import cumt.tj.hbase.tsdb.jdo.TagVDao;
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
 * 负责解析TSData注解标注类的解析器
 */
public class TSDataRowGenerator<T> implements RowGenerator<T>{

    //存储“方法名-方法”的键值对，以便获取方法，比如方法名“getTime”为key的方法getTime()
    Map<String,Method> methodMap=new HashMap<>();
    Map<Field,Integer> indexMap=new HashMap<>();
    List<Field> metricFieldList=new ArrayList<>();
    List<Field> tagFieldList=new ArrayList<>();
    Field timeField;
    TagVDao tagVDao;

    //记录已生成Row的标签
    Map<String,String> tagMap=new HashMap<>();

    private TSDataRowGenerator() {
    }

    /**
     * 根据实现了@TSData注解的类，创建相应的Processor
     * @param clz
     * @param <E>
     * @return
     */
    public static <E> RowGenerator<E> createByClass(Class<E> clz,TagVDao tagVDao){

        TSDataRowGenerator<E> processor=new TSDataRowGenerator<>();

        //查看clz是否标注了TSData注解
        if(clz.getAnnotation(TSData.class)==null){
            throw new RuntimeException("类并不是@TSData注解的类");
        }

        //充填methodMap
        Method[] ms=clz.getDeclaredMethods();
        for (Method m:ms) {
            processor.methodMap.put(m.getName(),m);
        }

        //获取Tag域与Metric域
        for (Field field:clz.getDeclaredFields()) {
            if(field.getAnnotation(Tag.class) != null) {
                processor.tagFieldList.add(field);
                processor.indexMap.put(field,field.getAnnotation(Tag.class).index());
            } else if (field.getAnnotation(Metric.class) != null) {
                processor.metricFieldList.add(field);
                processor.indexMap.put(field,field.getAnnotation(Metric.class).index());
            } else if(field.getAnnotation(Time.class)!=null){
                processor.timeField=field;
                processor.indexMap.put(field,field.getAnnotation(Time.class).index());
            }
        }

        //设置tagVDao
        processor.tagVDao=tagVDao;
        return processor;

    }

    /**
     * 根据一个@TSData标注的pojo对象，生成可以分别存储到tsdb-uid的2个Row与tsdb的1个Row
     * @param tSData 以@TSData注解的pojo对象
     * @return 存储了uidList与tsdbList以及插入这些数据后的counter的RowContainer实例
     */
    @Override
    public RowContainer getRows(T tSData) {

        //这个是要返回的东东
        RowContainer rowContainer=new RowContainer();
        List<Row> uidList=new LinkedList<>();
        List<Row> tsdbList=new LinkedList<>();

        String value;String counterStr=null;String[] rowkeyStr=new String[indexMap.size()];

        //对于标签，这是要插入到tsdb-uid表中的Row
        for (Field tagField: tagFieldList) {
            //获取属性值
            value=getFieldValue(methodMap,tagField,tSData);
            counterStr = tagMap.get(value);
            if(tagMap.get(value)==null) {
                //还没有为标签生成Row
                //从数据库中获取该标签的id
                counterStr = tagVDao.getTagVId(value);
                if (counterStr == null || counterStr.equals("")) {
                    //如果数据库中还没有该标签
                    tagVDao.tagVCountIncrease();
                    counterStr = String.format("%05d", tagVDao.getTagVCount());
                    tagMap.put(value, counterStr);

                    //这时，要将还没有的标签加到数据库去
                    //对于标注了@Tag注解的field
                    //可以为这个建立2个Row了，一个是name:tagv，一个是id:tagv
                    uidList.add(new Put(Bytes.toBytes(counterStr)).addColumn(Bytes.toBytes("name"), Bytes.toBytes("tagv"), Bytes.toBytes(value)));
                    uidList.add(new Put(Bytes.toBytes(value)).addColumn(Bytes.toBytes("id"), Bytes.toBytes("tagv"), Bytes.toBytes(counterStr)));
                }
            }
            //设置tsdb表rowkey相应位置的值
//            rowkeyStr[indexMap.get(tagField)]=String.format("%05d",indexMap.get(tagField))+counterStr;
            //由于tagk是写死的，所以没有必要要了
            rowkeyStr[indexMap.get(tagField)]=counterStr;
        }

        //对于指标，这是要插入到tsdb表中的Row
        for (Field metricField: metricFieldList) {
            //获取指标值
            value=getFieldValue(methodMap,metricField,tSData);

            //该指标值对应的时间
            String time=getFieldValue(methodMap,timeField,tSData);

            //设置tsdb表指标、时间rowkey相应位置的值
            rowkeyStr[indexMap.get(timeField)]=time;
            rowkeyStr[indexMap.get(metricField)]=String.format("%05d",indexMap.get(metricField));

            //生成tsdb表中的rowkey
            StringBuilder sb=new StringBuilder();
            for (String s:rowkeyStr) {
                sb.append(s);
            }

            //对于标注了@Metric注解的field
            //可以为这个建立1个Row了，是tsdb表中的t:value，此时的rowkey要特别注意，关系到HBase操作的性能
            tsdbList.add(new Put(Bytes.toBytes(sb.toString())).addColumn(Bytes.toBytes("t"),Bytes.toBytes("value"),Bytes.toBytes(value)));
        }

        rowContainer.setUidList(uidList);
        rowContainer.setTsdbList(tsdbList);
        return rowContainer;
    }

    /**
     * 根据一个@TSData标注的pojo对象，生成可以分别存储到tsdb-uid的2个Row与tsdb的1个Row
     * @param tSDatas 以@TSData注解的pojo对象组成的集合
     * @return 存储了uidList与tsdbList以及插入这些数据后的counter的RowContainer实例
     */
    @Override
    public RowContainer getRows(Collection<T> tSDatas) {

        //这个是要返回的东东
        RowContainer rowContainer=new RowContainer();
        rowContainer.setUidList(new LinkedList<>());
        rowContainer.setTsdbList(new LinkedList<>());

        for (T tsData:tSDatas) {
            rowContainer.addAll(getRows(tsData));
        }

        return rowContainer;
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
