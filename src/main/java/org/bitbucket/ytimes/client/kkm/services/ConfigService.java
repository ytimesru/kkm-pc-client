package org.bitbucket.ytimes.client.kkm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.DefaultPropertiesPersister;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

@Component
public class ConfigService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, String> config = new HashMap<String, String>();

    @PostConstruct
    public void init() throws Exception {
        read();
        if (config.get("moduleUUID") == null) {
            config.put("moduleUUID", UUID.randomUUID().toString());
            save();
        }
    }

    public String getValue(String name, String defaultValue) {
        String v = config.get(name);
        return v != null ? v : defaultValue;
    }

    public String getValue(String name) {
        String v = config.get(name);
        if (v != null) {
            return v;
        }
        throw new IllegalArgumentException("Config with name '" + name + "' is not found");
    }

    public int getIntValue(String name) {
        String value = getValue(name);
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean contains(String name) {
        return config.containsKey(name);
    }

    public void setValue(String name, String value) {
        config.put(name, value);
    }

    private void read() throws IOException {
        try {
            File f = new File(getFileName());
            InputStream in = new FileInputStream(f);
            try {
                Properties props = new Properties();

                DefaultPropertiesPersister p = new DefaultPropertiesPersister();
                p.load(props, in);

                config = new HashMap<String, String>();
                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    config.put((String) entry.getKey(), (String) entry.getValue());
                }
            } finally {
                in.close();
            }
        }
        catch (FileNotFoundException e) {
            logger.warn("File " + getFileName() + " is not found.");
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    synchronized public void save() throws IOException {
        Properties props = new Properties();
        for(String keys: config.keySet()) {
            String value = config.get(keys);
            props.put(keys, value != null ? value : "");
        }

        File f = new File(getFileName());
        OutputStream out = new FileOutputStream(f);
        // write into it
        DefaultPropertiesPersister p = new DefaultPropertiesPersister();
        p.store(props, out, "");
    }

    private String getFileName() {
        return "config.properties";
    }

    public Set<String> getAllKeys() {
        return config.keySet();
    }
}
