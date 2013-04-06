package rdb2rdfmappingbuilder;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class TemplateUtil {

    private static Map<String, Template> cache = new HashMap<>();

    public static String applyTemplate(String serviceName, Map<String, Object> map) throws Exception {

        String templateKey = serviceName;

        if (!cache.containsKey(templateKey)) {

            loadTemplate(serviceName);
        }

        VelocityContext context = new VelocityContext();

        Set<String> keys = map.keySet();

        for (String key : keys) {

            context.put(key, map.get(key));
        }

        StringWriter message = new StringWriter(1024);

        Template template = cache.get(templateKey);

        synchronized (template) {

            template.merge(context, message);
        }

        return message.toString();
    }

    private static synchronized void loadTemplate(String serviceName) throws Exception {

        Velocity.setProperty(Velocity.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath." + Velocity.RESOURCE_LOADER + ".class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init();

        Template template = Velocity.getTemplate("/templates/" + serviceName + ".vm");

        synchronized (cache) {
            cache.put(serviceName, template);
        }
    }
}