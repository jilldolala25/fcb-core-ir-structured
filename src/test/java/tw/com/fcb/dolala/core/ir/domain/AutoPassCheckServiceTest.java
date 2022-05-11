package tw.com.fcb.dolala.core.ir.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tw.com.fcb.dolala.core.ir.domain.service.AutoPassCheckService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Copyright (C),2022,FirstBank
 * FileName: AutoPassCheckServiceTest
 * Author: Han-Ru
 * Date: 2022/4/4 上午 12:30
 * Description:
 */
@SpringBootTest
class AutoPassCheckServiceTest {
    @Autowired
    AutoPassCheckService autoPassCheckService;

    @Test
    void checkAutoPass() {

    }

    @Test
    void checkCurrencyNotTWD() {

        assertEquals(false, autoPassCheckService.checkCurrencyNotTWD("TWD"));

        assertEquals(true, autoPassCheckService.checkCurrencyNotTWD("USD"));
    }

    @Test
    void checkBranch() {

        assertEquals(false,autoPassCheckService.checkBranch(null));

        assertEquals(true,autoPassCheckService.checkBranch("093"));
    }

    @Test
    void checkIRClosed() {

        assertEquals(false,autoPassCheckService.checkIRClosed("7"));

        assertEquals(true,autoPassCheckService.checkIRClosed("0"));

    }
}