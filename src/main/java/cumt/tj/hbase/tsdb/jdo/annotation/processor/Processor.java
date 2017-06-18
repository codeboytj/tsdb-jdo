package cumt.tj.hbase.tsdb.jdo.annotation.processor;

import cumt.tj.hbase.tsdb.jdo.pojos.RowContainer;
import org.apache.hadoop.hbase.client.Row;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by sky on 17-6-18.
 */
public interface Processor<T> {

    //org.apache.hadoop.hbase.client.Table.batch能使用的List<Row>
//    Map<String,List<Row>> getRows(T tSData, Integer counter);
    RowContainer getRows(T tSData, int counter);
//    Map<String,List<Row>> getRows(T tSData, int counter);

    //org.apache.hadoop.hbase.client.Table.batch能使用的List<Row>，这是对T的集合进行操作
    Map<String,List<Row>> getRows(Collection<T> tSDatas, int counter);

}
