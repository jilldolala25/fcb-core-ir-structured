package tw.com.fcb.dolala.core.ir.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.com.fcb.dolala.core.common.http.Response;
import tw.com.fcb.dolala.core.common.web.CommonFeignClient;
import tw.com.fcb.dolala.core.ir.web.api.IRCaseControllerApi;
import tw.com.fcb.dolala.core.ir.application.command.SwiftMessageSaveCmd;
import tw.com.fcb.dolala.core.ir.application.dto.IRCaseDto;
import tw.com.fcb.dolala.core.ir.application.mapper.mapper.IRCaseDtoMapper;
import tw.com.fcb.dolala.core.ir.application.dto.IRDto;
import tw.com.fcb.dolala.core.ir.domain.service.AutoPassCheckService;
import tw.com.fcb.dolala.core.ir.domain.service.IRCaseService;

import javax.validation.constraints.NotNull;

/**
 * Copyright (C),2022-2022,FirstBank
 * FileName: IRCaseController
 * Author: Han-Ru
 * Date: 2022/3/10 下午 02:51
 * Description: IRSwiftController
 */
@Slf4j
@RestController
@RequestMapping("/ir")
@OpenAPIDefinition(info = @Info(title = "DoLALA多啦啦's  匯入  API", version = "v1.0.0"))
public class IRCaseController implements IRCaseControllerApi {

    @Autowired
    IRCaseService irCaseService;
    @Autowired
    CommonFeignClient commonFeignClient;
    @Autowired
    AutoPassCheckService autoPassCheckService;
    @Autowired
    IRCaseDtoMapper dtoMapper;

    @Override
    public Response<String> receiveSwift(@Validated @RequestBody SwiftMessageSaveCmd message) {
        Response<String> response = new Response();
        String insertIRCaseResult;
        try {
            IRCaseDto irCaseDto = dtoMapper.cmdToIRCaseDto(message);
            //insert，將電文資料新增至IRCase檔案
            insertIRCaseResult = irCaseService.irCaseInsert(irCaseDto);
            response.Success();
            response.setData(insertIRCaseResult);
            log.info("呼叫接收 SWIFT 電文 API：接收及儲存一筆 SWIFT 電文");
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫接收 SWIFT 電文 API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    @Override
    public Response<String> checkAutoPassMK(@NotNull @PathVariable("irSeqNo") String irSeqNo) {
        Response<String> response = new Response<String>();
        try {
            IRCaseDto irCaseDto = irCaseService.getByIRSeqNo(irSeqNo);
            // check 是否可自動放行
            // update IRCaseDto AutoPassMk
            irCaseDto.setAutoPassMk(autoPassCheckService.checkAutoPass(irCaseDto));
            irCaseService.updateByIRSeqNo(irCaseDto);
//			if (irCaseDto.getAutoPassMk().equals("Y")){
//				irService.autoPassInsertIRMaster(irCaseDto);
//			}
            response.Success();
            response.setData("success");
            log.info("呼叫檢核電文是否可自動放行API：SeqNo編號" + irSeqNo + "是否已自動放行:" + irCaseDto.getAutoPassMk());
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫檢核電文是否可自動放行API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    @Override
    public Response<IRCaseDto> getBySeqNo(@NotNull @PathVariable("irSeqNo") String irSeqNo) {
        Response<IRCaseDto> response = new Response<IRCaseDto>();
        try {
            IRCaseDto irCaseDto = irCaseService.getByIRSeqNo(irSeqNo);
            response.Success();
            response.setData(irCaseDto);
            log.info("呼叫查詢匯款電文資料API：查詢SeqNo編號" + irSeqNo);
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫查詢匯款電文資料API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }
    @Override
    public Response<IRCaseDto> qryWaitForAuthorization(@PathVariable("seqNo") String seqNo) {
        Response<IRCaseDto> response = new Response<IRCaseDto>();
        try {
            IRCaseDto irCaseDto = irCaseService.getByIRSeqNo(seqNo);
            response.Success();
            response.setData(irCaseDto);
            if ("1".equals(irCaseDto.getProcessStatus())) {
                log.info("呼叫作業部查詢待放行案件API：查詢SeqNo編號" + seqNo + "待處理");
            } else if ("3".equals(irCaseDto.getProcessStatus())) {
                response.Error("S002", commonFeignClient.getErrorMessage("S002"));
                log.info("呼叫作業部查詢待放行案件API：查詢SeqNo編號" + seqNo + "主管已放行");
            } else if ("8".equals(irCaseDto.getProcessStatus())) {
                response.Error("S003", commonFeignClient.getErrorMessage("S003"));
                log.info("呼叫作業部查詢待放行案件API：查詢SeqNo編號" + seqNo + "已退匯");
            }
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫作業部查詢待放行案件API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    // S121A 執行MT103放行
    @Override
    public Response<IRDto> exeCaseAuthorization(@PathVariable("seqNo") String seqNo) {
        Response<IRDto> response = new Response<IRDto>();
        try {
            IRDto irDto = irCaseService.exeCaseAuthorization(seqNo);
            response.Success();
            response.setData(irDto);
            log.info("呼叫作業部匯入匯款案件放行API：SeqNo編號" + seqNo + "已放行成功");
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫作業部匯入匯款案件放行API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }
    @Override
    public Response<IRCaseDto> qryIRCase(@PathVariable("seqNo") String seqNo) {
        Response<IRCaseDto> response = new Response<IRCaseDto>();
        try {
            IRCaseDto irCaseDto = irCaseService.getByIRSeqNo(seqNo);
            response.Success();
            response.setData(irCaseDto);
            if ("1".equals(irCaseDto.getProcessStatus())) {
                log.info("呼叫作業部查詢匯款電文資料API：查詢SeqNo編號" + seqNo + "待處理");
            } else if ("3".equals(irCaseDto.getProcessStatus())) {
                response.Error("S002", commonFeignClient.getErrorMessage("S002"));
                log.info("呼叫作業部查詢匯款電文資料API：查詢SeqNo編號" + seqNo + "主管已放行");
            } else if ("8".equals(irCaseDto.getProcessStatus())) {
                response.Error("S003", commonFeignClient.getErrorMessage("S003"));
                log.info("呼叫作業部查詢匯款電文資料API：查詢SeqNo編號" + seqNo + "已退匯");
            }
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫作業部查詢匯款電文資料API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }
    @Override
    public Response<IRCaseDto> exeReturnIRCase(@PathVariable("seqNo") String seqNo) {
        Response<IRCaseDto> response = new Response<IRCaseDto>();
        try {
            IRCaseDto irCaseDto = irCaseService.exeReturnIRCase(seqNo);
            response.Success();
            response.setData(irCaseDto);
            log.info("呼叫作業部退匯API：SeqNo編號" + seqNo + "已執行退匯作業");
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫作業部退匯API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    // S061C	退匯(無匯入編號) (C)

    // S061D	退匯(無匯入編號) (D)
    @Override
    public Response<IRCaseDto> deleteReturnIRCase(@PathVariable("seqNo") String seqNo) {
        Response<IRCaseDto> response = new Response<IRCaseDto>();
        try {
            IRCaseDto irCaseDto = irCaseService.deleteReturnIRCase(seqNo);
            response.Success();
            response.setData(irCaseDto);
            log.info("呼叫作業部退匯API：SeqNo編號" + seqNo + "已執行退匯刪除作業");
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫作業部退匯API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    // S061P	退匯(無匯入編號) (P)
    // S061I	退匯(無匯入編號) (A/C/D/P) 前資料查詢
    @Override
    public Response<IRCaseDto> qryWaitForReturnIRCase(@PathVariable("seqNo") String seqNo) {
        Response<IRCaseDto> response = new Response<IRCaseDto>();
        try {
            IRCaseDto irCaseDto = irCaseService.getByIRSeqNo(seqNo);
            response.Success();
            response.setData(irCaseDto);
            if ("1".equals(irCaseDto.getProcessStatus())) {
                log.info("呼叫作業部退匯API：查詢SeqNo編號" + seqNo + "待處理");
            } else if ("3".equals(irCaseDto.getProcessStatus())) {
                response.Error("S002", commonFeignClient.getErrorMessage("S002"));
                log.info("呼叫作業部退匯API：查詢SeqNo編號" + seqNo + "主管已放行");
            } else if ("8".equals(irCaseDto.getProcessStatus())) {
                response.Error("S003", commonFeignClient.getErrorMessage("S003"));
                log.info("呼叫作業部退匯API：查詢SeqNo編號" + seqNo + "已退匯");
            }
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫作業部退匯API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

}
