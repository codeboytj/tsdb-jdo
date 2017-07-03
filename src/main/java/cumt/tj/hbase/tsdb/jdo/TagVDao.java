package cumt.tj.hbase.tsdb.jdo;

/**
 * Created by sky on 17-7-3.
 */
public interface TagVDao {
    /**
     * 根据标签名寻找tsdb-uid表中标签相应的id
     * @param tagVName 标签名
     * @return 标签id
     */
    String getTagVId(String tagVName);

    /**
     * 让tagVCount自增
     */
    void tagVCountIncrease();

    int getTagVCount();
}
