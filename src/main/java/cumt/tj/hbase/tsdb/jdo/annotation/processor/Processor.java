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
    RowContainer getRows(T tSData, int counter);

}
