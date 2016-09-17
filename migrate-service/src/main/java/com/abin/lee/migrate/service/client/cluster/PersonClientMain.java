//package com.abin.lee.migrate.service.client.cluster;
//
//import com.linkedin.databus.client.DatabusHttpClientImpl;
///**
// * Created with IntelliJ IDEA.
// * User: abin
// * Date: 16-9-16
// * Time: 上午10:51
// * To change this template use File | Settings | File Templates.
// */
//
//
//public class PersonClientMain
//{
//    static final String PERSON_SOURCE = "com.linkedin.events.example.person.Person";
//
//    public static void main(String[] args) throws Exception
//    {
//        DatabusHttpClientImpl.Config configBuilder = new DatabusHttpClientImpl.Config();
//
//        //Try to connect to a relay on localhost
//        configBuilder.getRuntime().getRelay("1").setHost("localhost");
//        configBuilder.getRuntime().getRelay("1").setPort(11115);
//        configBuilder.getRuntime().getRelay("1").setSources(PERSON_SOURCE);
//
//        //Instantiate a client using command-line parameters if any
//        DatabusHttpClientImpl client = DatabusHttpClientImpl.createFromCli(args, configBuilder);
//
//        //register callbacks
//        PersonConsumer personConsumer = new PersonConsumer();
//        client.registerDatabusStreamListener(personConsumer, null, PERSON_SOURCE);
//        client.registerDatabusBootstrapListener(personConsumer, null, PERSON_SOURCE);
//
//        //fire off the Databus client
//        client.startAndBlock();
//    }
//
//}
