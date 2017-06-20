package cumt.tj.tsdb.jdo.annotation.processor;

import cumt.tj.hbase.tsdb.jdo.annotation.processor.Processor;
import cumt.tj.hbase.tsdb.jdo.annotation.processor.TSDataProcessor;
import cumt.tj.hbase.tsdb.jdo.pojos.RowContainer;
import cumt.tj.tsdb.jdo.pojos.Gas;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.io.IOException;
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
        assertEquals(5,gasMap.getCounter());


        Admin admin;
        Configuration config;


        //Hbase配置，暂时写在这里，是为了dao层与service层的逻辑分离，有可能要拿到service里面去
        // Create a connection to the cluster.
        Configuration conf = HBaseConfiguration.create();
        try (Connection connection = ConnectionFactory.createConnection(conf);
            Table tsdb_uid = connection.getTable(TableName.valueOf("gas_tsdb_uid"));
            Table tsdb = connection.getTable(TableName.valueOf("gas_tsdb"))) {
//            tsdb_uid.put(new Put(Bytes.toBytes("")));
            tsdb_uid.batch(gasMap.getUidList(),null);
            tsdb.batch(gasMap.getTsdbList(),null);
            // use table as needed, the table returned is lightweight
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void insertRows(){

    }

}
