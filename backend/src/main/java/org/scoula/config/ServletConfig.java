package org.scoula.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@EnableWebMvc
@ComponentScan(basePackages = {
        "org.scoula",
        "org.scoula.oauth.controller",
})
public class ServletConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/")
                .setViewName("forward:/resources/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");

        registry.addResourceHandler("/assets/**")
                .addResourceLocations("/resources/assets/");

        // Swagger UI 리소스를 위한 핸들러 설정
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // Swagger WebJar 리소스 설정
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        // Swagger 리소스 설정
        registry.addResourceHandler("/swagger-resources/**")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/v2/api-docs")
                .addResourceLocations("classpath:/META-INF/resources/");
    }
    // 배포 CORS처리를 위한 코드 추가
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")   // 우리 API 경로 패턴
                .allowedOrigins("*")     // 개발 중이라면 전체 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false) // '*' 쓸 때는 false 유지
                .maxAge(3600);
    }

    @Bean
    public MultipartResolver multipartResolver() {
        StandardServletMultipartResolver resolver
                = new StandardServletMultipartResolver();
        return resolver;
    }
}
