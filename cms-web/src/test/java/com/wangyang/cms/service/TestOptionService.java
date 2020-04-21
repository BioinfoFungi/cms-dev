package com.wangyang.cms.service;

import com.wangyang.model.pojo.enums.PropertyEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestOptionService extends AbstractServiceTest
{

    @Test
    public void test(){
        Integer value = optionService.getPropertyIntegerValue(PropertyEnum.CATEGORY_PAGE_SIZE);
        System.out.println(value);
    }
}
