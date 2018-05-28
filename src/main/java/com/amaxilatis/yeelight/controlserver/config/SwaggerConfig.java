package com.amaxilatis.yeelight.controlserver.config;

import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@Import({springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration.class})
public class SwaggerConfig {
    
    @Value("${api.version}")
    String apiVersion;
    @Value("${api.title}")
    String apiTitle;
    @Value("${api.description}")
    String apiDescription;
    
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select().paths(paths()).build().apiInfo(getApiInfo());
    }
    
    private Predicate<String> paths() {
        return and(regex("/v" + apiVersion + "/.*"), not(regex("/v" + apiVersion + "/profile")));
    }
    
    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder().validatorUrl(null).docExpansion(DocExpansion.NONE).operationsSorter(OperationsSorter.ALPHA).defaultModelRendering(ModelRendering.EXAMPLE).displayOperationId(true).defaultModelsExpandDepth(1).defaultModelsExpandDepth(1).build();
    }
    
    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder().title(apiTitle).description(apiDescription).version("v" + apiVersion).build();
    }
}