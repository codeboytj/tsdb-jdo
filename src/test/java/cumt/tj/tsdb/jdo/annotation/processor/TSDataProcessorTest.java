package cumt.tj.tsdb.jdo.annotation.processor;

import cumt.tj.hbase.tsdb.jdo.annotation.processor.Processor;
import cumt.tj.hbase.tsdb.jdo.annotation.processor.TSDataProcessor;
import cumt.tj.hbase.tsdb.jdo.pojos.RowContainer;
import cumt.tj.tsdb.jdo.pojos.Gas;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by sky on 17-6-18.
 */
public class TSDataProcessorTest {

    Processor processor=TSDataProcessor.createByClass(Gas.class);

    @Test
    public void getRows(){

        Gas gas=new Gas();
        gas.setType("甲烷");
        gas.setPointNum("123456789");
        gas.setMineName("城郊矿");
        gas.setTime("2017-6-21 14:52:60");
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
    public void insertGases(){
        Gas gas=new Gas();
        gas.setType("甲烷");
        gas.setPointNum("1223456789");
        gas.setMineName("城郊矿");
        gas.setTime("2017-6-21 14:52:45");
        gas.setUnit("%");
        gas.setConcentration("0.23");
        gas.setInstAddress("345工作面");

        Gas gas1=new Gas();
        gas1.setType("甲烷");
        gas1.setPointNum("12334456789");
        gas1.setMineName("城郊矿");
        gas1.setTime("2017-6-21 14:23:60");
        gas1.setUnit("%");
        gas1.setConcentration("0.564");
        gas1.setInstAddress("97工作面");

        Gas gas2=new Gas();
        gas2.setType("甲烷");
        gas2.setPointNum("12345566789");
        gas2.setMineName("城郊矿");
        gas2.setTime("2017-6-21 13:52:60");
        gas2.setUnit("%");
        gas2.setConcentration("0.21");
        gas2.setInstAddress("5677工作面");

        List<Gas> gases=new ArrayList<>();
        gases.add(gas);
        gases.add(gas1);
        gases.add(gas2);

        int counter=0;
        RowContainer gasMap= processor.getRows(gases,counter);
        assertEquals(15,gasMap.getCounter());

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

}
