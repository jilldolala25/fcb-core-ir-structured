package tw.com.fcb.dolala.core.ir.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.fcb.dolala.core.ir.application.dto.IRCaseDto;

/**
 * Copyright (C),2022-2022,FirstBank
 * FileName: AutoPassCheckService
 * Author: Han-Ru
 * Date: 2022/3/25 下午 02:18
 * Description:檢核是否可自動放行
 */
@Service
@Transactional
@Slf4j
public class AutoPassCheckService {

    public String checkAutoPass(IRCaseDto irCaseDto) {
        // check 相關欄位
        // update IRCaseDto
        log.info("呼叫checkAutoPassServie, check是否可自動放行");
        boolean checkBranch = this.checkBranch(irCaseDto.getBeAdvBranch());
        boolean checkCurrencyNotTWD = this.checkCurrencyNotTWD(irCaseDto.getCurrency());
        boolean checkClosed = this.checkIRClosed(irCaseDto.getProcessStatus());
        log.debug("checkBranch = " + checkBranch);
        log.debug("checkCurrencyNotTWD = " + checkCurrencyNotTWD);
        log.debug("checkClosed = " + checkClosed);
        if (checkBranch == true && checkCurrencyNotTWD == true && checkClosed == true) {
            return "Y";
        } else {
            return "N";
        }
    }

    public boolean checkCurrencyNotTWD(String currency) {
        if (currency.equals("TWD")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkBranch(String branch) {
        if (branch == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkIRClosed(String processStatus) {
        if (processStatus.equals("7")) {
            return false;
        } else {
            return true;
        }
    }
}
