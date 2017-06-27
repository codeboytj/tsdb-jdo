package cumt.tj.hbase.tsdb.jdo.annotation.processor;

import cumt.tj.hbase.tsdb.jdo.pojos.RowContainer;

import java.util.Collection;

/**
 * Created by sky on 17-6-18.
 * 注解解析接口
 */
public interface Processor<T> {

    //org.apache.hadoop.hbase.client.Table.batch能使用的List<Row>
    RowContainer getRows(T tSData, int counter);
    RowContainer getRows(Collection<T> tSDatas, int counter);

}
