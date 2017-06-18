package cumt.tj.hbase.tsdb.jdo.annotation;

import java.lang.annotation.*;

/**
 * Created by sky on 17-6-17.
 * 用于使用在pojo类上的注解，表示这个需要存储的时间序列数据(Time Series Data)对象
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TSData {

}
