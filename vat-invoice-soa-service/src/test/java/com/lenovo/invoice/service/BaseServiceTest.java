package com.lenovo.invoice.service;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by admin on 2017/9/4.
 */
//表示整合JUnit4进行测试
@RunWith(SpringJUnit4ClassRunner.class)
//加载spring配置文件
@ContextConfiguration(locations = {
        "classpath*:spring-config-resource.xml",
        "classpath*:spring-config-service.xml",
        "classpath*:spring-config-dao.xml",
        "classpath*:spring-config-dubbo.xml",
        "classpath*:spring-redis-shard.xml"
})
public class BaseServiceTest {
}
