package tw.com.fcb.dolala.core.ir.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.fcb.dolala.core.common.web.CommonFeignClient;
import tw.com.fcb.dolala.core.common.web.dto.CustomerDto;
import tw.com.fcb.dolala.core.ir.application.command.IRSaveCmd;
import tw.com.fcb.dolala.core.ir.application.dto.IRAdvicePrintListDto;
import tw.com.fcb.dolala.core.ir.application.dto.IRCaseDto;
import tw.com.fcb.dolala.core.ir.application.dto.IRDto;
import tw.com.fcb.dolala.core.ir.application.mapper.mapper.IRMasterMapper;
import tw.com.fcb.dolala.core.ir.domain.repository.IRMasterRepository;
import tw.com.fcb.dolala.core.ir.infra.entity.IRMaster;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Copyright (C),2022-2022,FirstBank
 * FileName: IRService
 * Author: sinjen
 * Date: 2022/3/10 下午 03:32
 * Description: 匯入匯款service
 * Hisotry:
 * <author>     <time>       <version>     <desc>
 * 作者姓名       修改時間       版本編號       描述
 */
@Slf4j
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
@Service
public class IRService {
    @Autowired
    IRMasterRepository irMasterRepository;

    @Autowired
    CommonFeignClient commonFeignClient;

    @Autowired
    IRMasterMapper irMasterMapper;

    private final String systemType = "IR";
    private final String noCode = "S";

    // 新增匯入匯款主檔
    public IRMaster insertIRMaster(IRSaveCmd irSaveCmd) throws Exception {
        IRMaster irMaster ;

        // 自動將saveCmd的屬性，對應到entity裡
        irMaster = irMasterMapper.irSaveCmdToIRMaster(irSaveCmd);
//        BeanUtils.copyProperties(irSaveCmd, irMaster);
        irMaster.setExchangeRate(commonFeignClient.isGetFxRate("B", irSaveCmd.getCurrency(), "TWD"));
        //取號
        irMaster.setIrNo(commonFeignClient.getFxNo(noCode, systemType, irSaveCmd.getBeAdvBranch()));
        if (irMaster.getIrNo() != null) {
            irMasterRepository.save(irMaster);
        } else {
            throw new Exception("S106");
        }


        return irMaster;
    }

    //電文進電，判斷可自動放行，新增至主檔
    public IRDto autoPassInsertIRMaster(IRCaseDto irCaseDto) throws Exception {
        IRSaveCmd irSaveCmd = new IRSaveCmd();
        irSaveCmd = irMasterMapper.irCaseDtoToIRSaveCmd(irCaseDto);
//        BeanUtils.copyProperties(irCaseDto, irSaveCmd);
        //自動放行新增進irMaster
        this.setIRMaster(irSaveCmd);

        IRMaster irMaster = this.insertIRMaster(irSaveCmd);
//		return "電文可自動放行，新增IRMaster成功，編號：" + irMaster.getIrNo();
        IRDto irDto = new IRDto();
        irDto = irMasterMapper.irMasterToIRDto(irMaster);
//        BeanUtils.copyProperties(irMaster, irDto);
        return irDto;
    }

    // 傳入匯入匯款編號查詢案件
    public IRDto getByIrNo(String irNo) throws Exception {

        IRMaster irMaster = irMasterRepository.findByIrNo(irNo).orElseThrow(() -> new Exception("S101"));
        IRDto irDto = new IRDto();

        if (irMaster != null) {
            // 自動將entity的屬性，對應到dto裡
            BeanUtils.copyProperties(irMaster, irDto);
        }
        return irDto;
    }

    //傳入匯入匯款編號查詢待處理案件
    public IRDto findByIrNoAndPaidStats(String irNo) throws Exception {

        IRMaster irMaster = irMasterRepository.findByIrNoAndPaidStats(irNo, 0).orElseThrow(() -> new Exception("S101"));
        IRDto irDto = new IRDto();

        if (irMaster != null) {
            // 自動將entity的屬性，對應到dto裡
            irDto = irMasterMapper.irMasterToIRDto(irMaster);
//            BeanUtils.copyProperties(irMaster, irDto);
        } else {
            throw new Exception("S109");
        }
        return irDto;
    }

    //傳入受通知單位查詢案件數
    public Integer getIrCaseCount(String branch) {
        Integer count = 0;
        count = irMasterRepository.findByBeAdvBranchAndPaidStatsAndPrintAdvMk(branch, 0, "N").size();
        return count;
    }

    //print 列印通知書
    public void print(String irNo) throws Exception {
        IRDto irDto = this.findByIrNoAndPaidStats(irNo);

        if (irDto != null) {
            irDto.setPrintAdvMk("Y");
            irDto.setPrintAdvDate(LocalDate.now());
            this.updateMaster(irDto);
        } else {
            throw new Exception("S108");
        }
    }

    //settle 解款
    public void settle(String irNo) throws Exception {
        IRDto irDto = this.findByIrNoAndPaidStats(irNo);
        log.debug("{執行settle解款}", irDto);
        if (irDto != null) {
            //外存入帳
            commonFeignClient.updateFpmBalance
                    (irDto.getReceiverAccount(), irDto.getCurrency(), irDto.getIrAmount());
            log.debug("外存入帳");
            try {
                //update irMaster
                irDto.setPaidStats(4);    //4:已解款
                this.updateMaster(irDto);
            } catch (Exception e) {
                //外存補償
                commonFeignClient.updateFpmBalance
                        (irDto.getReceiverAccount(),
                                irDto.getCurrency(),
                                irDto.getIrAmount().multiply(new BigDecimal("-1")));
                log.debug("外存入帳回沖");


            }
        } else {
            throw new Exception("S107");
        }
    }


    public void updateMaster(IRDto irDto) {

        IRMaster irMaster = irMasterMapper.irDtoToIRMaster(irDto);
        if (irMaster == null) {
            new Exception("D001");
        }
//        BeanUtils.copyProperties(irDto, irMaster);
        irMasterRepository.save(irMaster);

    }

    // set IRMaster相關欄位資料
    public IRSaveCmd setIRMaster(IRSaveCmd irSaveCmd) {
        CustomerDto customer = commonFeignClient.getCustomer(irSaveCmd.getReceiverAccount()).getData();        //初始值 0
        irSaveCmd.setPaidStats(0);
        //印製通知書記號
        irSaveCmd.setPrintAdvMk("N");
        irSaveCmd.setProcessBranch(irSaveCmd.getBeAdvBranch());
        // process-Date
        LocalDateTime currentDateTime = LocalDateTime.now();
        irSaveCmd.setProcessDate(currentDateTime.toLocalDate());
        irSaveCmd.setAdvDate(currentDateTime.toLocalDate());
        //是否為本行客戶
        irSaveCmd.setOurCust("Y");
        irSaveCmd.setCustTelNo(customer.getCustTelNo());
        irSaveCmd.setCusBirthDate(customer.getCusBirthDate());
        //受款人身份別
        irSaveCmd.setBeneKind(customer.getBeneKind());
        return irSaveCmd;
    }

    //S131R 「處理種類」為(0、1、2、7或8) 之發查電文。 ==>進行通知書列印(多筆)
    public List<IRDto> qryAdvicePrint(String branch) {
        List<IRMaster> listData = new ArrayList<IRMaster>();
        List<IRDto> listDto = new ArrayList<IRDto>();
        listData = irMasterRepository.findByBeAdvBranchAndPaidStatsAndPrintAdvMk(branch, 0, "N");
        IRMaster irMaster;
        IRDto irDto;

        for (int i = 0; i < listData.size(); i++) {
            //update IRMaster PrintAdvMk = Y ：已列印通知書
            irMaster = listData.get(i);
            irMaster.setPrintAdvMk("Y");
            irMasterRepository.save(irMaster);
            //add to irDto
            irDto = new IRDto();
            irDto = irMasterMapper.irMasterToIRDto(irMaster);
//            BeanUtils.copyProperties(irMaster, irDto);
            listDto.add(irDto);
        }

        return listDto;
    }

    // S131I1 "「處理種類」為(3或4) 之發查電文。==>回傳「受通知筆數」、「已印製通知書筆數」欄位"
    public int[] qryAdviceCount(String branch) {
        int[] adviceCount = new int[2];

        //「受通知筆數」
        List<IRMaster> listData = new ArrayList<IRMaster>();
        listData = irMasterRepository.findByBeAdvBranchAndPaidStats(branch, 0);

        if (listData != null)
            adviceCount[0] = listData.size();

        //「已印製通知書筆數」
        listData = irMasterRepository.findByBeAdvBranchAndPaidStatsAndPrintAdvMk(branch, 0, "Y");

        if (listData != null)
            adviceCount[1] = listData.size();

        return adviceCount;
    }

    // S131I2 "「處理種類」為(5或6) 之發查電文。==>回傳S1311畫面"
    public List<IRAdvicePrintListDto> qryAdviceList(String branch) {
        List<IRMaster> listData = new ArrayList<IRMaster>();
        List<IRAdvicePrintListDto> i2ListData = new ArrayList<IRAdvicePrintListDto>();
        listData = irMasterRepository.findByBeAdvBranchAndPaidStats(branch, 0);
        IRMaster irMaster;
        IRAdvicePrintListDto irAdvicePrintListDto;

        for (int i = 0; i < listData.size(); i++) {
            irAdvicePrintListDto = new IRAdvicePrintListDto();
            irMaster = listData.get(i);
            irAdvicePrintListDto = irMasterMapper.irMasterToIrAdvicePrintDto(irMaster);
//            BeanUtils.copyProperties(irMaster, irAdvicePrintListDto);
            i2ListData.add(irAdvicePrintListDto);
        }

        return i2ListData;
    }

    // S211A 執行原幣解款資料新增
    @Transactional(timeout = 1000)
    public IRDto exeRelaseIRMaster(IRSaveCmd irSaveCmd) throws Exception {
        IRMaster irMaster = irMasterRepository.findByIrNoAndPaidStats(irSaveCmd.getIrNo(), 0).orElseThrow(() -> new Exception("S102"));

        log.debug("PaidStats" + irMaster.getIrNo() + "," + irMaster.getPaidStats());
        if (irMaster.getPaidStats().equals(4)) {
            throw new Exception("S102");
        }
        IRDto irDto = new IRDto();

        if (irMaster != null) {
            // 將傳入值對應至irMaster
            irMaster = irMasterMapper.updateIRMasterFromirSaveCmd(irSaveCmd,irMaster);
//            irMaster.setCaseSeqNo(irMasterId);
//            BeanUtils.copyProperties(irSaveCmd, irMaster);
            irMaster.setPaidStats(4); // 4:已解款
            //取得賣匯匯率
            irMaster.setExchangeRate(commonFeignClient.isGetFxRate("S", irMaster.getCurrency(), "TWD"));
            //取得對美元匯率
            irMaster.setToUsFxrate(commonFeignClient.isGetFxRate("S", "USD", "TWD"));
            //取得匯款行名稱地址
            log.debug("{}", irMaster);
            if (irMaster.getRemitBank() != null) {

                irMaster.setRemitBkName1(commonFeignClient.getBank(irMaster.getRemitBank()).getName());
                irMaster.setRemitBkName2(commonFeignClient.getBank(irMaster.getRemitBank()).getAddress());
            }
            //外存入帳
            commonFeignClient.updateFpmBalance
                    (irMaster.getReceiverAccount(), irMaster.getCurrency(), irMaster.getIrAmount());
            log.debug("外存入帳" + irMaster.getReceiverAccount() + irMaster.getCurrency() + irMaster.getIrAmount());
            try {
                // 更新匯入匯款主檔
                irMasterRepository.save(irMaster);
                // 自動將entity的屬性，對應到dto裡
                irDto = irMasterMapper.irMasterToIRDto(irMaster);
                //      BeanUtils.copyProperties(irMaster, irDto);
                if (irMaster.getFxDeposit().equals(BigDecimal.valueOf(0))) {
                    log.debug("主檔更新失敗，須執行外存補償");
                    throw new Exception("S107");
                }
            } catch (Exception e) {
                //外存補償
                log.error("執行外存補償", e.getMessage());
                e.printStackTrace();
                commonFeignClient.updateFpmBalance
                        (irDto.getReceiverAccount(),
                                irDto.getCurrency(),
                                irDto.getIrAmount().multiply(new BigDecimal("-1")));
                log.error("外存補償完成，外存帳務回沖");
                throw e;
            }
        } else {
            throw new Exception("S107");
        }
        return irDto;
    }

    // S211D 執行原幣解款資料剔除 (D:剔除)
    public IRDto deleteRelaseIRMaster(String irNo) throws Exception {
        IRMaster irMaster = irMasterRepository.findByIrNoAndPaidStats(irNo, 4).orElseThrow(() -> new Exception("S101"));
        IRDto irDto = new IRDto();

        if (irMaster != null) {
            irMaster.setPaidStats(0); // 0:初值
            // 更新匯入匯款主檔
            irMasterRepository.save(irMaster);
            // 自動將entity的屬性，對應到dto裡
            irDto = irMasterMapper.irMasterToIRDto(irMaster);
//            BeanUtils.copyProperties(irMaster, irDto);
        }
        return irDto;
    }

    // S611A 新增退匯交易
    public IRDto exeReturnIRMaster(String irNo) throws Exception {
        IRMaster irMaster = irMasterRepository.findByIrNoAndPaidStats(irNo, 0).orElseThrow(() -> new Exception("S101"));
        IRDto irDto = new IRDto();

        if (irMaster != null) {
            irMaster.setPaidStats(5); // 5:已退匯
            // 更新匯入匯款主檔
            irMasterRepository.save(irMaster);
            // 自動將entity的屬性，對應到dto裡
            irDto = irMasterMapper.irMasterToIRDto(irMaster);
//            BeanUtils.copyProperties(irMaster, irDto);
        }
        return irDto;
    }

    // S611D 剔除退匯交易
    public IRDto delReturnIRMaster(String irNo) throws Exception {
        IRMaster irMaster = irMasterRepository.findByIrNoAndPaidStats(irNo, 5).orElseThrow(() -> new Exception("S101"));
        IRDto irDto = new IRDto();

        if (irMaster != null) {
            irMaster.setPaidStats(0); // 0:初值
            // 更新匯入匯款主檔
//            irMasterRepository.save(irMaster);
            // 自動將entity的屬性，對應到dto裡
            irDto = irMasterMapper.irMasterToIRDto(irMaster);
//            BeanUtils.copyProperties(irMaster, irDto);
        }
        return irDto;
    }
}
