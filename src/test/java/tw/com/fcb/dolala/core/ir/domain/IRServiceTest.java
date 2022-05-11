package tw.com.fcb.dolala.core.ir.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tw.com.fcb.dolala.core.ir.domain.service.IRService;

/**
 * Copyright (C),2022,FirstBank
 * FileName: IRServiceTest
 * Author: Han-Ru
 * Date: 2022/5/4 下午 11:42
 * Description:
 */
@SpringBootTest
class IRServiceTest {

    @Autowired
    IRService irService;

    @Test
    void testGetByIrNo() throws Exception {

        irService.getByIrNo("S1NHA00947");
    }

    @Test
    void tesGetIrCaseCount(){
        irService.getIrCaseCount("094");
    }
}