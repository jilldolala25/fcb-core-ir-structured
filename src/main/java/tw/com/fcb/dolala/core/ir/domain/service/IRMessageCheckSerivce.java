package tw.com.fcb.dolala.core.ir.domain.service;

import lombok.Builder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.fcb.dolala.core.ir.infra.enums.ChargeType;

import java.math.BigDecimal;

/**
 * Copyright (C),2022-2022,FirstBank
 * FileName: IRMessageCheckSerivce
 * Author: Han-Ru
 * Date: 2022/3/25 下午 03:24
 * Description:電文欄位基本檢核
 * Hisotry:
 * <author>     <time>       <version>     <desc>
 * 作者姓名       修改時間       版本編號       描述
 */
@Service
@Transactional
@Builder
public class IRMessageCheckSerivce {

    public String getAccountNo(String account) {
        String accountNo = "00000000000";
        if (account != null) {
            if (account.substring(0, 1).equals("/")) {
                accountNo = account.substring(1);

            }
        }
        return accountNo;
    }


    public boolean checkAccountNo(String account) {
        boolean checkMK = false;
        // "/09320991111"
        if (account.substring(0, 1).equals("/")) {
            if (account.length() == 12) {
                checkMK = true;

            } else {
                checkMK = false;
            }
        } else {
            if (account.length() == 11) {
                checkMK = true;

            } else {
                checkMK = false;
            }
        }
        return checkMK;
    }

    public boolean checkValueDate() {
        return true;
    }

    public boolean checkAmount(BigDecimal amount) {
        boolean checkMK = false;
        BigDecimal zero = new BigDecimal(0);
        if (amount.compareTo(zero) == -1) {
            checkMK = false;
        } else {
            checkMK = true;
        }
        return checkMK;
    }

    public boolean checkCurrency(String currency) {
        boolean checkMK;
        if (currency.length() > 3) {
            checkMK = false;
        } else {
            checkMK = true;
        }
        return checkMK;
    }

    public boolean checkChargeType(String chargeType) {
        boolean checkMK;
        try {
            ChargeType.valueOf(chargeType);
            checkMK = true;
        } catch (Exception e) {
            checkMK = false;
        }
        return checkMK;
    }

}
