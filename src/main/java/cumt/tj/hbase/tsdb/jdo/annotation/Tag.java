package cumt.tj.hbase.tsdb.jdo.annotation;

import java.lang.annotation.*;

/**
 * Created by sky on 17-6-17.
 * 这个用来放在以@TSData注解类的内部表示标签的域上面
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tag {
    //这个表示name:tagk的值，比如叫“矿名”
    public String key();
    //用来指定生成tsdb表中的rowkey的时候，该属性的位置
    public int index();
}
