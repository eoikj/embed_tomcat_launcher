package com.jeffery.tomcat;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;

public class Launcher {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        Host host = tomcat.getHost();
        host.setAutoDeploy(true);
        Context context = tomcat.addWebapp("/test", "E:/learn/embed_tomcat/tomcatsuit/war/target/war-0.0.1-SNAPSHOT");
        context.setReloadable(true);
        tomcat.setPort(8080);
        AprLifecycleListener lifecycleListener = new AprLifecycleListener();
        tomcat.getServer().addLifecycleListener(lifecycleListener);

        Connector connector=new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(8080);
        tomcat.getService().addConnector(connector);

        NamingResourcesImpl globalNamingResources = tomcat.getServer().getGlobalNamingResources();
        globalNamingResources.addResource(jndiDb());
        globalNamingResources.addResource(jmsConnFactory(10081,"sample","brkSample"));
        globalNamingResources.addResource(jmsQueue("sample","P.Sample"));
        tomcat.enableNaming();
        tomcat.start();
        tomcat.getServer().await();
    }

    private static ContextResource jndiDb() {
        ContextResource jndi = new ContextResource();
        jndi.setName("jdbc/db");
        jndi.setAuth("Container");
        jndi.setType("javax.sql.DataSource");
        jndi.setScope("Sharable");
        jndi.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        jndi.setProperty("url", "jdbc:mysql://localhost:3306");
        return jndi;
    }

    public static ContextResource jmsConnFactory(final int port, final String name, final String brokerName){
        ContextResource contextResource = new ContextResource();
        contextResource.setName("jms/"+name);
        contextResource.setAuth("Container");
        contextResource.setType("org.apache.activemq.ActiveMQConnectionFactory");
        contextResource.setProperty("factory","org.apache.activemq.jndi.JNDIReferenceFactory");
        contextResource.setProperty("brokerURL","tcp://localhost:"+port);
        contextResource.setProperty("brokerName",brokerName);
        contextResource.setProperty("useEmbeddedBroker","true");
        return contextResource;
    }

    public static ContextResource jmsQueue(final String name, final String physicalName){
        ContextResource contextResource = new ContextResource();
        contextResource.setName("jms/queue/"+name);
        contextResource.setAuth("Container");
        contextResource.setType("org.apache.activemq.command.ActiveMQQueue");
        contextResource.setProperty("factory","org.apache.activemq.jndi.JNDIReferenceFactory");
        contextResource.setProperty("physicalName",physicalName);
        return contextResource;
    }

    public static BrokerService mq(int port,String embedMQ) throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName(embedMQ);
        brokerService.addConnector("tcp://localhost:"+port);
        brokerService.setManagementContext(new ManagementContext());
        return brokerService;
    }
}
