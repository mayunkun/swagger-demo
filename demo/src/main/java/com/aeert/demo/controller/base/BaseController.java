package com.aeert.demo.controller.base;

import com.aeert.demo.annotation.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author l'amour solitaire
 * @Description 基础接口
 * @Date 2020/7/16 上午9:31
 **/
@RestController
@RequestMapping("/api/{version}/")
@Api(tags = "基础接口")
public class BaseController {

    @ApiVersion(1)
    @GetMapping("base")
    @ApiOperation("基础")
    public String base1(@PathVariable String version) {
        return "base1";
    }

    @ApiVersion(2)
    @GetMapping("base")
    @ApiOperation("基础")
    public String base2(@PathVariable String version) {
        return "base2";
    }

}
