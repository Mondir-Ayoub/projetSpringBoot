package ma.xproce.springbootproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Chemin absolu vers  dossier images
        String projetPath = System.getProperty("user.dir");

        // Correction pour Windows (remplacer les antislashs \ par des slashs /)
        String imagesPath = "file:///" + projetPath.replace("\\", "/") + "/src/main/resources/static/images/";

        // Configuration
        registry.addResourceHandler("/images/**")
                .addResourceLocations(imagesPath);
    }
}
