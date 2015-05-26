package org.restnucleus.test;

import org.restnucleus.dao.GenericRepository;
import org.restnucleus.dao.Model;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class DbHelper {

    final private GenericRepository dao;

    public DbHelper(GenericRepository genericRepository) {
        dao = genericRepository;
    }

    public GenericRepository getDao() {
        return dao;
    }

    public <E extends Model> void persist(Map<Class<? extends Model>, List<? extends Model>> data) {
        if (null != data) {
            for (Entry<Class<? extends Model>, List<? extends Model>> e : data.entrySet()) {
                List<? extends Model> items = e.getValue();
                if (null != items) {
                    for (Model m : items) {
                        if (m.getId() == null)
                            dao.add(m);
                    }
                }
            }
        }
    }

    public static void clearInMemoryDb() {
        InputStream resourceAsStream = DbHelper.class.getClassLoader().getResourceAsStream("datanucleus.properties");
        if (resourceAsStream != null) {
            try {
                Properties cfg = new Properties();
                cfg.load(resourceAsStream);
                String url = cfg.getProperty("javax.jdo.option.ConnectionURL");
                String user = cfg.getProperty("javax.jdo.option.ConnectionUserName", "sa");
                String password = cfg.getProperty("javax.jdo.option.ConnectionPassword", "");
                if (url != null && url.startsWith("jdbc:hsqldb:mem:")) {
                    try (Connection c = DriverManager.getConnection(url, user, password)) {
                        try (Statement statement = c.createStatement();) {
                            statement.execute("DROP SCHEMA PUBLIC CASCADE");
                        }
                    }
                }
            } catch (IOException | SQLException e) {
                LoggerFactory.getLogger(DbHelper.class).info("clear db fail", e);
            }
        }
    }
}
