package com.abin.lee.migrate.canal.cluster;

import java.util.List;

import com.abin.lee.migrate.common.JsonUtil;
import com.abin.lee.migrate.model.canal.Abin;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;


import com.alibaba.otter.canal.client.*;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
/**
 * Created with IntelliJ IDEA.
 * User: abin
 * Date: 16-9-15
 * Time: 下午6:17
 * To change this template use File | Settings | File Templates.
 */
public class CanalClientCluster {

    public static void main(String args[]) {
        // 创建链接
        CanalConnector connector = CanalConnectors.newClusterConnector("172.16.2.134:2181", "example", "", "");

        int batchSize = 1;
        int emptyCount = 0;
        while(true) {
            try {
                connector.connect();
                connector.subscribe(".*\\..*");
                while(true) {
                    Message messages = connector.getWithoutAck(1000);
                    long bachId = messages.getId();
                    int size = messages.getEntries().size();
                    if(bachId == -1 || size == 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("No DATA!!!!!!!!!!!!!!!!!!!!!!!!");
                    } else {
                        printEntry(messages.getEntries());
                        connector.ack(bachId);
                    }

                }

            } catch (Exception e) {
                System.out.println("============================================================connect crash");
            } finally {
                connector.disconnect();
            }
        }
    }

    private static void printEntry(@NotNull List<Entry> entrys) {
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));

            for (RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                    exchangeBean(entry, rowData.getBeforeColumnsList()) ;
                } else if (eventType == EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                    exchangeBean(entry, rowData.getAfterColumnsList()) ;
                } else {
                    System.out.println("-------> before");
                    printColumn(rowData.getBeforeColumnsList());
                    exchangeBean(entry, rowData.getBeforeColumnsList()) ;
                    System.out.println("-------> after");
                    printColumn(rowData.getAfterColumnsList());
                    exchangeBean(entry, rowData.getAfterColumnsList()) ;
                }
            }
        }
    }

    private static void printColumn(@NotNull List<Column> columns) {
        for (Column column : columns) {
            System.out.println("columns=" + columns.toString());
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

    private static void exchangeBean(Entry entry, List<Column> columns){
        List<Abin> list = Lists.newArrayList();
         if(StringUtils.equals(entry.getHeader().getSchemaName(), "canal")){
             if(StringUtils.equals(entry.getHeader().getTableName(), "abin")){
                 Abin abin = new Abin();
                 for (Column column : columns) {
                     if(StringUtils.equals(column.getName(), "id"))
                         abin.setId(Ints.tryParse(column.getValue()));
                     if(StringUtils.equals(column.getName(), "name"))
                         abin.setName(column.getValue());
                 }
                 list.add(abin);
             }
         }
        System.out.println("-------------list----start----------");
        System.out.println("list="+ JsonUtil.toJson(list));
        System.out.println("-------------list----end----------");
    }
}