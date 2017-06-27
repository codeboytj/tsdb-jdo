package cumt.tj.hbase.tsdb.jdo.annotation;

import java.lang.annotation.*;

/**
 * Created by sky on 17-6-17.
 * 这个用来放在以@TSData注解类的内部表示指标的域上面
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Metric {
    //这个表示name:metric的值，比如叫“监测值”
    public String name();
    //用来指定生成tsdb表中的rowkey的时候，该属性的位置
    public int index();
}
