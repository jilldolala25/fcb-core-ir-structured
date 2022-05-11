package tw.com.fcb.dolala.core.ir.application.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),2022,FirstBank
 * FileName: IRServiceAspect
 * Author: Han-Ru
 * Date: 2022/5/5 下午 10:36
 * Description: IRServiceAspect
 */
@Aspect
@Component
@Slf4j
public class IRServiceAspect {

    @Before(value = "execution(* tw.com.fcb.dolala.core.ir.domain.service.*Service.*(..))")
    public void beforeServiceMethods(JoinPoint jp) {
        log.debug("============before service to do =============");
        //獲取傳入目標方法的參數對象
        Object[] args = jp.getArgs();
        for (Object arg : args) {
            log.debug("arg = {}", arg);

        }
        //獲取目標的方法位置及所屬的class
        log.debug("signature : {}", jp.getSignature());
    }


    @After(value = "execution(* tw.com.fcb.dolala.core.ir.domain.service.*Service.*(..))")
    public void afterServiceMethods(JoinPoint jp) {
        log.debug("============after service to do =============");


    }
}
