package cumt.tj.tsdb.jdo.annotation.processor;

import cumt.tj.hbase.tsdb.jdo.annotation.processor.Processor;
import cumt.tj.hbase.tsdb.jdo.annotation.processor.TSDataProcessor;
import cumt.tj.hbase.tsdb.jdo.pojos.RowContainer;
import cumt.tj.tsdb.jdo.pojos.Gas;
import org.apache.hadoop.hbase.client.Row;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by sky on 17-6-18.
 */
public class TSDataProcessorTest {

    Processor processor=new TSDataProcessor<Gas>(Gas.class);

    @Test
    public void getRows(){

        Gas gas=new Gas();
        gas.setType("甲烷");
        gas.setPointNum("445454");
        gas.setMineName("城郊矿");
        gas.setTime("14:52:60");
        gas.setUnit("%");
        gas.setConcentration("0.11");
        gas.setInstAddress("工作面");

        int counter=0;
        RowContainer gasMap= processor.getRows(gas,counter);
        assertEquals(6,gasMap.getCounter());

        System.out.println(gasMap.getTsdbList());
        System.out.println(gasMap.getUidList());

    }

}
