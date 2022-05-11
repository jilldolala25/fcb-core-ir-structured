package tw.com.fcb.dolala.core.ir.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tw.com.fcb.dolala.core.ir.domain.service.IRMessageCheckSerivce;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Copyright (C),2022-2022,FirstBank
 * FileName: IRDtoMessageCheckSerivceTest
 * Author: Han-Ru
 * Date: 2022/3/29 下午 01:26
 * Description:
 * Hisotry:
 * <author>     <time>       <version>     <desc>
 * 作者姓名       修改時間       版本編號       描述
 */
@SpringBootTest
class IRMessageCheckSerivceTest {

    @Autowired
    IRMessageCheckSerivce irMessageCheckService;

    @Test
    void getAccountNo() throws Exception {
        String account = "/12209301111";
        assertEquals("12209301111",irMessageCheckService.getAccountNo(account));
    }

    @Test
    void checkAccountNo() {

        assertEquals(true,irMessageCheckService.checkAccountNo("/09320123456"));
        assertEquals(true,irMessageCheckService.checkAccountNo("09320123456"));
        assertEquals(false,irMessageCheckService.checkAccountNo("/0932012345"));
        assertEquals(false,irMessageCheckService.checkAccountNo("093201234567"));
    }

    @Test
    void checkValueDate() {
    }

    @Test
    void checkAmount() {
        assertEquals(true,irMessageCheckService.checkAmount(BigDecimal.valueOf(100)));
        assertEquals(false,irMessageCheckService.checkAmount(BigDecimal.valueOf(-100)));
    }

    @Test
    void checkCurrency() throws Exception {
        assertEquals(true,irMessageCheckService.checkCurrency("USD"));
        assertEquals(false,irMessageCheckService.checkCurrency("ABCD"));
    }

    @Test
    void checkChargeType() {
        assertEquals(true,irMessageCheckService.checkChargeType("SHA"));
        assertEquals(true,irMessageCheckService.checkChargeType("OUR"));
        assertEquals(true,irMessageCheckService.checkChargeType("BEN"));
        assertEquals(false,irMessageCheckService.checkChargeType("ABC"));
    }

}