package cumt.tj.hbase.tsdb.jdo.annotation.processor;

import cumt.tj.hbase.tsdb.jdo.pojos.RowContainer;

import java.util.Collection;

/**
 * Created by sky on 17-6-18.
 * 注解解析接口
 */
public interface RowGenerator<T> {

    RowContainer getRows(T tSData);
    RowContainer getRows(Collection<T> tSDatas);

}
