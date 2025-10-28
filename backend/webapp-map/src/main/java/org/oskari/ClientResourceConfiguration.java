package org.oskari;

import fi.nls.oskari.util.PropertyUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class ClientResourceConfiguration implements WebMvcConfigurer {

    public ClientResourceConfiguration(){
        System.out.println("Loading oskari-override.properties");
        PropertyUtil.loadProperties("/oskari-docker.properties");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("Registering /Oskari as static resource location");
        registry.addResourceHandler("/Oskari/**")
                .addResourceLocations("/Oskari/")
                .setCachePeriod(60*60*8) // Cache static files for 8 hours
                .resourceChain(true) // cache resource lookups
                // Gzip and cache static resources for faster delivery times.
                .addResolver(new EncodedResourceResolver())
                .addResolver(new PathResourceResolver());

    }
}
