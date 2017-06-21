package cumt.tj.hbase.tsdb.jdo.pojos;

import org.apache.hadoop.hbase.client.Row;

import java.util.List;

/**
 * Created by sky on 17-6-18.
 */
public class RowContainer {

    private List<Row> uidList;
    private List<Row> tsdbList;
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

    public void addAll(RowContainer rowContainer){
        this.uidList.addAll(rowContainer.uidList);
        this.tsdbList.addAll(rowContainer.tsdbList);
        this.counter=rowContainer.counter;
    }
}
