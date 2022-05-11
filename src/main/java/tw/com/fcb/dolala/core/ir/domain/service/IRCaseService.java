package tw.com.fcb.dolala.core.ir.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.fcb.dolala.core.common.web.CommonFeignClient;
import tw.com.fcb.dolala.core.common.web.dto.BankDto;
import tw.com.fcb.dolala.core.common.web.dto.CustomerDto;
import tw.com.fcb.dolala.core.ir.application.command.IRSaveCmd;
import tw.com.fcb.dolala.core.ir.application.dto.IRCaseDto;
import tw.com.fcb.dolala.core.ir.application.dto.IRDto;
import tw.com.fcb.dolala.core.ir.application.mapper.mapper.IRCaseMapper;
import tw.com.fcb.dolala.core.ir.domain.repository.IRCaseRepository;
import tw.com.fcb.dolala.core.ir.domain.repository.IRMasterRepository;
import tw.com.fcb.dolala.core.ir.infra.entity.IRCaseEntity;
import tw.com.fcb.dolala.core.ir.infra.entity.IRMaster;

import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * Copyright (C),2022-2022,FirstBank
 * FileName: IRCaseService
 * Author: Han-Ru
 * Date: 2022/3/11 下午 05:30
 * Description: 匯入電文service
 * Hisotry:
 * <author>     <time>       <version>     <desc>
 * 作者姓名       修改時間       版本編號       描述
 */
@Transactional
@Service
@Slf4j
public class IRCaseService {
    @Autowired
    IRCaseRepository irCaseRepository;
    @Autowired
    CommonFeignClient commonFeignClient;
    @Autowired
    IRMessageCheckSerivce irMessageCheckSerivce;
    @Autowired
    IRMasterRepository irMasterRepository;
    @Autowired
    AutoPassCheckService autoPassCheckService;
    @Autowired
    IRService irService;
    @Autowired
    IRCaseMapper irCaseMapper;

    //取號檔 SystemType,branch
    private final String systemType = "IR_SEQ";
    private final String branch = "999";


    public String irCaseInsert(IRCaseDto irCaseDto) throws Exception {
        //beginTx
        String insertIRCaseResult;
//        IRCaseEntity irCaseEntity = new IRCaseEntity();
        //讀取共用服務 set相關欄位
        irCaseDto = this.setIRCaseData(irCaseDto);

        // irCastDto，對應到entity裡
        IRCaseEntity irCaseEntity = irCaseMapper.irCaseDtoToEntity(irCaseDto);
//        BeanUtils.copyProperties(irCaseDto, irCaseEntity);
        irCaseRepository.save(irCaseEntity);

// check 期交所案件
        boolean checkFE = this.checkFuturesExchange(irCaseDto.getReceiverAccount());
        if (checkFE == true) {
            IRMaster irMaster = beFEAutoSettle(irCaseDto.getSeqNo());
            log.info("期交所自動解款成功，IRCase編號：" + irCaseDto.getSeqNo() + ",IRMaster新增成功，編號：" + irMaster.getIrNo());
            insertIRCaseResult = "期交所自動解款成功，IRCase編號：" + irCaseDto.getSeqNo() + ",IRMaster新增成功，編號：" + irMaster.getIrNo();
            //非期交所案件，再繼續判斷是否可自動放行
        } else {
            String autoPassMK = autoPassCheckService.checkAutoPass(irCaseDto);
            irCaseDto = getByIRSeqNo(irCaseDto.getSeqNo());
            irCaseDto.setAutoPassMk(autoPassMK);
            this.updateByIRSeqNo(irCaseDto);
            //可自動放行，寫入IRMaster
            if (irCaseDto.getAutoPassMk().equals("Y")) {
                IRDto irDto = irService.autoPassInsertIRMaster(irCaseDto);
                log.info("IRCase檔新增成功，編號：" + irCaseDto.getSeqNo() + ",電文可自動放行，新增IRMaster成功，編號：" + irDto.getIrNo());
                insertIRCaseResult = "IRCase檔新增成功，編號：" + irCaseDto.getSeqNo() + ",電文可自動放行，新增IRMaster成功，編號：" + irDto.getIrNo();
                //不可自動放行，停留在IRCase
            } else {
                log.info("IRCase檔新增成功，編號：" + irCaseDto.getSeqNo());
                insertIRCaseResult = "IRCase檔新增成功，編號：" + irCaseDto.getSeqNo();
            }
        }

        return insertIRCaseResult;
        //commitTx

    }

    public IRCaseDto setIRCaseData(IRCaseDto irCaseDto) throws Exception {
        // STATUS 七日檔初始狀態
        //      1 ：初值
        //      2 ：印製放行工作單訖(經辦放行) (S111交易)
        irCaseDto.setProcessStatus("1");
        irCaseDto.setAutoPassMk("N");
        irCaseDto.setAdvBranch("091");
        irCaseDto.setCreditMK("N");
        // check account
        String accountNo = irMessageCheckSerivce.getAccountNo(irCaseDto.getReceiverAccount());
        irCaseDto.setReceiverAccount(accountNo);
        //顧客資料，受通知分行
        CustomerDto customer = commonFeignClient.getCustomer(irCaseDto.getReceiverAccount()).getData();
        if (customer == null) {
            throw new Exception("DZZZ:查不到客戶");
        }
        irCaseDto.setBeAdvBranch(customer.getBranchID());
        irCaseDto.setCustomerId(customer.getCustomerId());
        // process-date,tx-time
        LocalDateTime currentDateTime = LocalDateTime.now();
        String time = String.valueOf(currentDateTime.toLocalTime());
        String processDate = String.valueOf(currentDateTime.toLocalDate());
        irCaseDto.setReceiveDate(LocalDate.parse(processDate));
        irCaseDto.setTxTime(time);
        //讀取銀行名稱地址
        BankDto bankDto = commonFeignClient.getBank(irCaseDto.getSenderSwiftCode());
        irCaseDto.setSenderInfo1(bankDto.getName());
        irCaseDto.setSenderInfo3(bankDto.getAddress());
        //讀取都市檔
        String country = commonFeignClient.getCountryName(irCaseDto.getSenderSwiftCode().substring(4, 5));
        //讀取存匯行關係
        String isCorrspondent = bankDto.getIsCorrespondent();
        //取號
        String irSeqNo = commonFeignClient.getSeqNo();
        irCaseDto.setSeqNo(irSeqNo);
        if (irCaseDto.getSeqNo() != null) {
            return irCaseDto;
        } else {
            throw new Exception("S105");
        }

    }

    // 傳入seqNo編號查詢案件
    // s121i 查詢待放行資料
    // S061I 查詢待退匯(無匯入編號)
    public IRCaseDto getByIRSeqNo(String irSeqNo) throws Exception {

        IRCaseEntity irCaseEntity = irCaseRepository.findBySeqNo(irSeqNo).orElseThrow(() -> new Exception("S001"));
        IRCaseDto irCaseDto = new IRCaseDto();

        if (irCaseDto != null) {
            // 自動將entity的屬性，對應到dto裡
            irCaseDto = irCaseMapper.irCaseEntityToDto(irCaseEntity);
//            BeanUtils.copyProperties(irCaseEntity, irCaseDto);
        }
        return irCaseDto;
    }

    // 傳入seqNo編號及處理狀態查詢電文檔案件
    public IRCaseEntity getByIRSeqNoAndProcessStatus(String seqNo, String processStatus) throws Exception {

        IRCaseEntity irCaseEntity = irCaseRepository.findBySeqNoAndProcessStatus(seqNo, processStatus).orElseThrow(() -> new Exception("S001"));
        return irCaseEntity;
    }

    // check是否為期交所案件
    public boolean checkFuturesExchange(String account) {
        if (account.equals("17140014156") || account.equals("17140014172"))
            return true;
        else {
            return false;
        }
    }

    // 期交所自動解款,寫入IRCase,IRMaster
    public IRMaster beFEAutoSettle(String irSeqNo) throws Exception {
        IRCaseDto irCaseDto = getByIRSeqNo(irSeqNo);
        irCaseDto.setProcessStatus("7");
        irCaseDto.setCreditMK("Y");
        updateByIRSeqNo(irCaseDto);

        //自動放行新增進irMaster並更新為已入帳
        IRSaveCmd irSaveCmd = irCaseMapper.irCaseDtoToIRSaveCmd(irCaseDto);
//        BeanUtils.copyProperties(irCaseDto, irSaveCmd);
        irSaveCmd = irService.setIRMaster(irSaveCmd);
        //期交所自動解款，PAID_STATUS = 2
        irSaveCmd.setPaidStats(2);
        irSaveCmd.setBeAdvBranch("091");
        irSaveCmd.setProcessBranch("091");
        IRMaster irMaster = irService.insertIRMaster(irSaveCmd);
        return irMaster;
    }

    public boolean updateByIRSeqNo(IRCaseDto irCaseDto) throws Exception {
//        IRCaseEntity irCaseEntity = new IRCaseEntity();
// 將irCaseDto，對應到entity裡
        IRCaseEntity irCaseEntity = irCaseMapper.irCaseDtoToEntity(irCaseDto);
//        BeanUtils.copyProperties(irCaseDto, irCaseEntity);
        irCaseRepository.save(irCaseEntity);
        return true;
    }

    // 查詢待處理案件
    public IRCaseDto getByIRSeqNoAndStatus(String seqNo) throws Exception {

        IRCaseDto irCaseDto = new IRCaseDto();
        IRCaseEntity irCaseEntity = this.getByIRSeqNoAndProcessStatus(seqNo, "1");
        if (irCaseEntity != null) {
            irCaseDto = irCaseMapper.irCaseEntityToDto(irCaseEntity);
//            BeanUtils.copyProperties(irCaseEntity, irCaseDto);
        }
        return irCaseDto;
    }

    // S061A 執行退匯
    public IRCaseDto exeReturnIRCase(String seqNo) throws Exception {

        IRCaseDto irCaseDto = new IRCaseDto();
        IRCaseEntity irCaseEntity = this.getByIRSeqNoAndProcessStatus(seqNo, "1");

        if (irCaseEntity != null) {
            irCaseEntity.setProcessStatus("8");
            // 更新電文檔
            irCaseRepository.save(irCaseEntity);
            irCaseDto = irCaseMapper.irCaseEntityToDto(irCaseEntity);
//            BeanUtils.copyProperties(irCaseEntity, irCaseDto);
        } else {
            // 查無此電文
            new Exception("S001");
        }
        return irCaseDto;
    }

    // S061D 執行退匯刪除
    public IRCaseDto deleteReturnIRCase(String seqNo) throws Exception {

        IRCaseDto irCaseDto = new IRCaseDto();
        IRCaseEntity irCaseEntity = this.getByIRSeqNoAndProcessStatus(seqNo, "8");

        if (irCaseEntity != null) {
            irCaseEntity.setProcessStatus("1");
            // 更新電文檔
            irCaseRepository.save(irCaseEntity);
            irCaseDto = irCaseMapper.irCaseEntityToDto(irCaseEntity);
//            BeanUtils.copyProperties(irCaseEntity, irCaseDto);
        } else {
            // 查無此電文
            new Exception("S001");
        }
        return irCaseDto;
    }

    // S121A 執行MT103放行
    public IRDto exeCaseAuthorization(String seqNo) throws Exception {

        IRDto irDto = new IRDto();
        IRMaster irMaster = null;
        IRCaseEntity irCaseEntity = this.getByIRSeqNoAndProcessStatus(seqNo, "1");

        if (irCaseEntity != null) {
            // 從電文檔搬移到主檔
            irMaster = new IRMaster();
            irMaster.setPaidStats(0);
            irMaster.setValueDate(irCaseEntity.getValueDate());
            irMaster.setIrAmount(irCaseEntity.getIrAmount());
            irMaster.setCurrency(irCaseEntity.getCurrency());
            irMaster.setBeAdvBranch("093");
            irMaster.setPrintAdvMk("Y");
            // 產生外匯編號
            irMaster.setIrNo(commonFeignClient.getFxNo("S", "IR", "093"));
            // 新增主檔
            irMasterRepository.save(irMaster);

            // update IRCaseEntity PROCESS_STATUS = 3 ：放行訖
            irCaseEntity.setProcessStatus("3");
            irCaseRepository.save(irCaseEntity);

            // 自動將entity的屬性，對應到dto裡
            BeanUtils.copyProperties(irMaster, irDto);
        } else {
            // 查無此電文
            new Exception("S001");
        }
        return irDto;
    }

}