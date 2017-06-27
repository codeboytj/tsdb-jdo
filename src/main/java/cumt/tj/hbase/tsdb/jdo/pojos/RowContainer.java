package cumt.tj.hbase.tsdb.jdo.pojos;

import org.apache.hadoop.hbase.client.Row;

import java.util.List;

/**
 * Created by sky on 17-6-18.
 * 用来存储Row的容器
 */
public class RowContainer {

    //用来批量插入到tsdb-uid表的Row列表
    private List<Row> uidList;
    //用来批量插入到tsdb表的Row列表
    private List<Row> tsdbList;
    //用来记录tsdb-uid表中的tagv总数
    private int counter;

    public List<Row> getUidList() {
        return uidList;
    }

    public void setUidList(List<Row> uidList) {
        this.uidList = uidList;
    }

    public List<Row> getTsdbList() {
        return tsdbList;
    }

    public void setTsdbList(List<Row> tsdbList) {
        this.tsdbList = tsdbList;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    /**
     * 将另一个RowContainer对象中的所有东西一次性加入到当前RowContainer之中
     * @param rowContainer
     */
    public void addAll(RowContainer rowContainer){
        this.uidList.addAll(rowContainer.uidList);
        this.tsdbList.addAll(rowContainer.tsdbList);
        this.counter=rowContainer.counter;
    }
}
