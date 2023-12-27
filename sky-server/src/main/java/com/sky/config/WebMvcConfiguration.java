package com.sky.config;

import com.sky.constant.FilePathConstant;
import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");
    }

    /**
     * 通过knife4j生成接口文档  knife4j是Java MVC框架集成Swagger生成Api文档
     * admin端
     * @return
     */
    @Bean
    public Docket docket_01() {
        log.info("生成接口文档");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("外卖项目接口文档")
                .version("2.0")
                .description("外卖项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("管理端接口")
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller.admin")) //指定扫描包
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * 通过knife4j生成接口文档  knife4j是Java MVC框架集成Swagger生成Api文档
     * user端
     * @return
     */
    @Bean
    public Docket docket_02() {
        log.info("生成接口文档");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("外卖项目接口文档")
                .version("2.0")
                .description("外卖项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户端接口")
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller.user")) //指定扫描包
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * 设置静态资源映射
     * @param registry 重写父类WebMvcConfigurationSupport的addResourceHandlers()方法
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("对接口文档进行映射");
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        log.info("对本地文件进行映射");
        registry.addResourceHandler("/uploadFile/**") //虚拟url路径
                .addResourceLocations("file:"+ FilePathConstant.IMAGE_PATH); //真实本地路径
    }

    /**
     * 消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建一个消息转换器对象，注意不要导错包是Jackson2Http
        MappingJackson2HttpMessageConverter converter =  new MappingJackson2HttpMessageConverter();
        //需要为消息转换器设置一个对象转换器，对象转换器可以将Java对象序列化为json数据，反之依然。
        converter.setObjectMapper(new JacksonObjectMapper());
        //将自己的消息转化器加入容器中，converters容器里是通过ArrayList存储的，框架会自带一些消息转换器，所以需要通过索引设置为第一个执行的
        converters.add(0, converter);
    }



}
