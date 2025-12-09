package demo.core.configuration;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProtocolConfig {

    private String universityName = "Университет Поставьте зачет!";

    private String directiorName = "Иванов Иван";

    public Map<String, Object> getProtocolProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("universityName", universityName);
        properties.put("directiorName", directiorName);
        return properties;
    }
}