package com.aeert.demo.controller.test;

import com.aeert.demo.annotation.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author l'amour solitaire
 * @Description 测试
 * @Date 2020/7/16 上午9:31
 **/
@RestController
@RequestMapping("/api/{version}/")
@Api(tags = "测试")
public class TestController {

    @ApiVersion(1)
    @GetMapping("test")
    @ApiOperation("测试")
    public String test1(@PathVariable String version) {
        return "test1";
    }

    @ApiVersion(2)
    @GetMapping("test")
    @ApiOperation("测试")
    public String test2(@PathVariable String version) {
        return "test2";
    }

}
