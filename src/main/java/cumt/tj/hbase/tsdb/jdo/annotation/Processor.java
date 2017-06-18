package cumt.tj.hbase.tsdb.jdo.annotation;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.util.Bytes;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by sky on 17-6-17.
 */
public class Processor<T> {

    //将一条T解析成uid与tsdb的插入
//    public Map<String,List<Row>> getRows(Collection<T> tCollection){
//
//        Map<String,List<Row>> map=new HashMap<>();
//
//        List<Row> uidList=new LinkedList<>();
//        List<Row> tsdbList=new LinkedList<>();
//
//        Class tc=t.getClass();
//        if(tc.getAnnotation(TSData.class)!=null){
//            //t有@Data注解，可以解析了
//
//            //首先，获取它的所有方法，形成“方法名-方法”的键值对，以便获取方法
//            Map<String,Method> methodMap=new HashMap<>();
//            for (Method m:tc.getMethods()
//                 ) {
//                methodMap.put(m.getName(),m);
//            }
//
//            String value;
//            //获取fields
//            for (Field field:tc.getDeclaredFields()
//                 ) {
//                if(field.getAnnotation(Tag.class)!=null){
//                    //对于标注了@Tag注解的field
//                    //可以为这个建立2个Row了，一个是name:tagv，一个是id:tagv
//                    value=getFieldValue(methodMap,field,t);
//                    uidList.add(new Put(Bytes.toBytes("000")).addColumn(Bytes.toBytes("name"),Bytes.toBytes("tagv"),Bytes.toBytes(value)));
//                    uidList.add(new Put(Bytes.toBytes(value)).addColumn(Bytes.toBytes("id"),Bytes.toBytes("tagv"),Bytes.toBytes("000")));
//                }else if(field.getAnnotation(Metric.class)!=null){
//                    //对于标注了@Metric注解的field
//                    //可以为这个建立1个Row了，是tsdb表中的t:value，此时的rowkey要特别注意，关系到HBase操作的性能
//                    value=getFieldValue(methodMap,field,t);
//                    tsdbList.add(new Put(Bytes.toBytes("000")).addColumn(Bytes.toBytes("t"),Bytes.toBytes("value"),Bytes.toBytes(value)));
//                }
//            }
//
//        }
//
//        map.put("uidList",uidList);map.put("tsdbList",tsdbList);
//        return map;
//    }

    /**
     * 通过反射机制调用getter方法，获得某个域的值
     * 这里假设每个field都是String类型的，由于到HBase的数据都是字符串，所以这个假设是合理的
     * @param methodMap
     * @param field
     * @param t
     * @return
     */
    public String getFieldValue(Map<String,Method> methodMap,Field field,T t){
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
