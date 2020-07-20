/**
 * Copyright 2018 众链科技 http://www.pchain.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.aeert.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author l'amour solitaire
 * @Description TODO
 * @Date 2020/7/16 上午9:00
 **/
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    List<Parameter> pars = new ArrayList<>(Arrays.asList(new ParameterBuilder().name("version").description("API 版本号").defaultValue("v1").modelRef(new ModelRef("string")).parameterType("header").required(false).build()));

    @Bean
    public Docket baseApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).groupName("基础服务")
                .apiInfo(new ApiInfoBuilder().title("基础服务API").description("基础服务接口文档").build()).pathMapping("/")
                .enable(true).select()
                .apis(RequestHandlerSelectors.basePackage("com.aeert.demo.controller.base"))
                .paths(PathSelectors.any()).build()
                .globalOperationParameters(pars)
                .securitySchemes(security());
    }

    @Bean
    public Docket luceneApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).groupName("lucene服务")
                .apiInfo(new ApiInfoBuilder().title("lucene服务API").description("lucene服务接口文档").build()).pathMapping("/")
                .enable(true).select()
                .apis(RequestHandlerSelectors.basePackage("com.aeert.demo.controller.lucene"))
                .paths(PathSelectors.any()).build()
                .globalOperationParameters(pars)
                .securitySchemes(security());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("swagger版本控制demo").description("swagger版本控制demo").termsOfServiceUrl("https://www.aeert.com")
                .contact(new Contact("l'amour solitaire", "https://www.aeert.com", "mayunkunwork@163.com")).version("3.0.0")
                .build();
    }

    private List<SecurityScheme> security() {
        return new ArrayList<SecurityScheme>(Arrays.asList(new ApiKey("token", "token", "header")));
    }

}