package cumt.tj.hbase.tsdb.jdo.annotation;

import java.lang.annotation.*;

/**
 * Created by sky on 17-6-18.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Time {
    public int index();
}
