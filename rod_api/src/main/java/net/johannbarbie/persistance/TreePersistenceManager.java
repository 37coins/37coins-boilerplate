package net.johannbarbie.persistance;

import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import org.apache.log4j.*;

public class TreePersistenceManager {
	
	 private static Logger log = Logger.getLogger(net.johannbarbie.persistance.TreePersistenceManager.class); 
	  
	  private static final TreePersistenceManager singleton = new TreePersistenceManager();
	  
	  
	  protected PersistenceManagerFactory emf;
	  
	  public static TreePersistenceManager getInstance() {
	    
	    return singleton;
	  }
	  
	  private TreePersistenceManager() {
	  }
	 
	  public PersistenceManagerFactory getEntityManagerFactory() {
	    
	    if (emf == null)
	      createEntityManagerFactory();
	    return emf;
	  }
	  
	  public void closeEntityManagerFactory() {
	    
	    if (emf != null) {
	      emf.close();
	      emf = null;
	      log.info("n*** Persistence finished at " + new java.util.Date());
	    }
	  }
	  
	  protected void createEntityManagerFactory() {
		  if (null!= System.getProperty("TPLOCAL")){
			  Properties properties = new Properties();
				properties.setProperty("javax.jdo.PersistenceManagerFactoryClass",
		                "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
			  properties.setProperty("javax.jdo.option.ConnectionDriverName","org.hsqldb.jdbcDriver");
			  properties.setProperty("javax.jdo.option.ConnectionURL",System.getProperty("JDBC_CONNECTION_STRING"));
			  properties.setProperty("javax.jdo.option.ConnectionUserName",System.getProperty("RDS_USERNAME"));
			  properties.setProperty("datanucleus.autoCreateSchema", "true");
			  properties.setProperty("datanucleus.validateTables", "true");
			  properties.setProperty("datanucleus.validateConstraints", "true");
			  this.emf = JDOHelper.getPersistenceManagerFactory(properties);
		  }else if (null!= System.getProperty("RDS_DB_NAME") && "" != System.getProperty("RDS_DB_NAME")){
			log.info("loading PersistenceManagerFactory properties from AWS environment variables.");
			log.info("connectionstring: "+"jdbc:mysql://"+System.getProperty("RDS_HOSTNAME")+":"+System.getProperty("RDS_PORT")+"/"+System.getProperty("RDS_DB_NAME"));
			log.info("RDS_USERNAME: "+System.getProperty("RDS_USERNAME"));
			Properties properties = new Properties();
			properties.setProperty("javax.jdo.PersistenceManagerFactoryClass",
			                "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
			properties.setProperty("javax.jdo.option.ConnectionDriverName","com.mysql.jdbc.Driver");
			properties.setProperty("javax.jdo.option.ConnectionURL","jdbc:mysql://"+System.getProperty("RDS_HOSTNAME")+":"+System.getProperty("RDS_PORT")+"/"+System.getProperty("RDS_DB_NAME"));
			properties.setProperty("javax.jdo.option.ConnectionUserName",System.getProperty("RDS_USERNAME"));
			properties.setProperty("javax.jdo.option.ConnectionPassword",System.getProperty("RDS_PASSWORD"));
			properties.setProperty("datanucleus.autoCreateSchema", "true");
			properties.setProperty("datanucleus.validateTables", "true");
			properties.setProperty("datanucleus.validateConstraints", "true");
			this.emf = JDOHelper.getPersistenceManagerFactory(properties);
		}else{
			log.info("loading PersistenceManagerFactory properties from classpath.");
			this.emf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
		}
		log.info("n*** Persistence started at " + new java.util.Date());
	  }
	}