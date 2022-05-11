package tw.com.fcb.dolala.core.ir.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.com.fcb.dolala.core.common.http.Response;
import tw.com.fcb.dolala.core.common.web.CommonFeignClient;
import tw.com.fcb.dolala.core.ir.web.api.IRControllerApi;
import tw.com.fcb.dolala.core.ir.application.command.IRSaveCmd;
import tw.com.fcb.dolala.core.ir.application.dto.IRAdvicePrintListDto;
import tw.com.fcb.dolala.core.ir.application.dto.IRDto;
import tw.com.fcb.dolala.core.ir.domain.service.IRService;
import tw.com.fcb.dolala.core.ir.infra.entity.IRMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C),2022-2022,FirstBank
 * FileName: IRController
 * Author: Han-Ru
 * Date: 2022/3/10 下午 02:08
 * Description: IRDto Controller
 */
@Slf4j
@RestController
@RequestMapping("/ir")
public class IRController implements IRControllerApi {

    @Autowired
    IRService irService;
    @Autowired
    CommonFeignClient commonFeignClient;


    @Override
    public Response<IRMaster> irMasterInsert(@Validated @RequestBody IRSaveCmd irSaveCmd) {
        Response<IRMaster> response = new Response<IRMaster>();

        try {
            // insert irMaster檢核完成，新增至主檔
            IRMaster irMaster = irService.insertIRMaster(irSaveCmd);
            response.Success();
            response.setData(irMaster);
            log.info("呼叫新增匯入匯款主檔API：新增一筆主檔資料");
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫新增匯入匯款主檔API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    @Override
    public Response<Integer> getCount(@PathVariable("branch") String branch) {
        Response<Integer> response = new Response<Integer>();
        try {
            Integer count = irService.getIrCaseCount(branch);
            response.Success();
            response.setData(count);
            log.info("呼叫查詢匯入案件數API：" + branch + "分行匯入案件數=" + count);
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫查詢匯入案件數API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    @Override
    public Response<IRDto> getIRMasterByIrNo(@PathVariable("irNo") String irNo) {
        Response<IRDto> response = new Response<IRDto>();
        try {
            IRDto irDto = irService.getByIrNo(irNo);
            response.Success();
            response.setData(irDto);
            if ("0".equals(irDto.getPaidStats().toString())) {
                log.info("呼叫查詢匯入匯款案件API：查詢匯入匯款編號" + irNo + "待處理");
            } else if ("4".equals(irDto.getPaidStats().toString())) {
                response.Error("S102", commonFeignClient.getErrorMessage("S102"));
                log.info("呼叫查詢匯入匯款案件API：查詢匯入匯款編號" + irNo + "已解款");
            } else if ("5".equals(irDto.getPaidStats().toString())) {
                response.Error("S103", commonFeignClient.getErrorMessage("S103"));
                log.info("呼叫查詢匯入匯款案件API：查詢匯入匯款編號" + irNo + "已退匯");
            } else {
                log.info("呼叫查詢匯入匯款案件API：查詢匯入匯款編號" + irNo + ",狀態 = " + irDto.getPaidStats());
            }
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫查詢匯入匯款案件API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    @Override
    public Response<IRDto> getByIrNo(@PathVariable("irNo") String irNo) {
        Response<IRDto> response = new Response<IRDto>();
        try {
            response.Success();
            response.setData(irService.findByIrNoAndPaidStats(irNo));
            log.info("呼叫查詢匯入匯款編號案件API：查詢irNo編號-AFTER" + irNo);
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫查詢匯入匯款編號案件API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }


    @Override
    public Response<?> print(@PathVariable("irNo") String irNo) {
        Response<?> response = new Response<>();
        try {
            irService.print(irNo);
            response.Success();
            log.info("呼叫變更印製通知書記號API：irNo編號" + irNo + "通知書印製成功，更新記號為Y");
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫變更印製通知書記號API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    @Override
    public Response<?> settle(@PathVariable("irNo") String irNo) {
        Response<?> response = new Response<>();
        try {
            irService.settle(irNo);
            response.Success();
            log.info("呼叫變更付款狀態API：irNo編號" + irNo + "付款狀態變更為已解款");
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫變更付款狀態API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }
    // ※※※ S131 API清單 ※※※
    // S131R 「處理種類」為(0、1、2、7或8) 之發查電文。 ==>進行通知書列印(多筆)
    @Override
    public Response<List<IRDto>> qryAdvicePrint(String branch) {
        Response<List<IRDto>> response = new Response<List<IRDto>>();
        List<IRDto> listData = new ArrayList<IRDto>();

        try {
            listData = irService.qryAdvicePrint(branch);
            response.Success();
            response.setData(listData);

            if (listData.size() != 0) {
                log.info("呼叫分行通知書列印API：" + branch + "分行列印" + listData.size() + "筆通知書");
            } else {
                log.info("呼叫分行通知書列印API：" + branch + "分行查無需列印資料");
            }
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫分行通知書列印API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    // S131I1 "「處理種類」為(3或4) 之發查電文。==>回傳「受通知筆數」、「已印製通知書筆數」欄位"
    @Override
    public Response<int[]> qryAdviceCount(String branch) {
        Response<int[]> response = new Response<int[]>();
        int[] adviceCount = new int[2];

        try {
            adviceCount = irService.qryAdviceCount(branch);
            response.Success();
            response.setData(adviceCount);

            // 受通知筆數
            if (adviceCount[0] != 0) {
                log.info("呼叫分行查詢受通知筆數API：" + branch + "分行受通知筆數:" + adviceCount[0] + "筆");
                log.info("呼叫分行查詢受通知筆數API：" + branch + "已印製通知書筆數:" + adviceCount[1] + "筆");
            } else {
                log.info("呼叫分行查詢受通知筆數API：" + branch + "分行查無資料");
            }
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫分行查詢受通知筆數API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    // S131I2 "「處理種類」為(5或6) 之發查電文。==>回傳S1311畫面"
    @Override
    public Response<List<IRAdvicePrintListDto>> qryAdviceList(String branch) {
        Response<List<IRAdvicePrintListDto>> response = new Response<List<IRAdvicePrintListDto>>();
        List<IRAdvicePrintListDto> listData = new ArrayList<IRAdvicePrintListDto>();

        try {
            listData = irService.qryAdviceList(branch);
            response.Success();
            response.setData(listData);

            if (listData.size() != 0) {
                log.info("呼叫分行查詢通知書明細API：" + branch + "分行共:" + listData.size() + "筆通知書");
            } else {
                log.info("呼叫分行查詢通知書明細API：" + branch + "分行查無資料");
            }
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫分行查詢通知書明細API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }
    @Override
    public Response<IRDto> exeRelaseIRMaster(@Validated @RequestBody IRSaveCmd irSaveCmd) {
        Response<IRDto> response = new Response<IRDto>();

        try {
            IRDto irDto = new IRDto();
            irDto = irService.exeRelaseIRMaster(irSaveCmd);
            response.Success();
            response.setData(irDto);
            log.info("呼叫新增原幣解款案件API：查詢匯入匯款編號" + response.getData().getIrNo() + "已解款");
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫新增原幣解款案件API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    // S211C 執行原幣解款資料更正 (C:更正)

    // S211D 執行原幣解款資料剔除 (D:剔除)
    @Override
    public Response<IRDto> delRelaseIRMaster(@PathVariable("irNo") String irNo) {
        Response<IRDto> response = new Response<IRDto>();
        try {
            IRDto irDto = irService.deleteRelaseIRMaster(irNo);
            response.Success();
            response.setData(irDto);
            log.info("呼叫剔除原幣解款案件API：查詢匯入匯款編號" + irNo + "已剔除解款");
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫剔除原幣解款案件API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    // S211I 依匯入編號資料查詢解款資料 (A,C,D)
    @Override
    public Response<IRDto> qryWaitForRelaseIRMaster(@PathVariable("irNo") String irNo) {
        Response<IRDto> response = new Response<IRDto>();
        try {
            IRDto irDto = irService.getByIrNo(irNo);
            response.Success();
            response.setData(irDto);
            if ("0".equals(irDto.getPaidStats().toString())) {
                log.info("呼叫查詢匯入匯款案件API：查詢匯入匯款編號" + irNo + "待處理");
            } else if ("4".equals(irDto.getPaidStats().toString())) {
                response.Error("S102", commonFeignClient.getErrorMessage("S102"));
                log.info("呼叫查詢匯入匯款案件API：查詢匯入匯款編號" + irNo + "已解款");
            } else if ("5".equals(irDto.getPaidStats().toString())) {
                response.Error("S103", commonFeignClient.getErrorMessage("S103"));
                log.info("呼叫查詢匯入匯款案件API：查詢匯入匯款編號" + irNo + "已退匯");
            }
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫查詢匯入匯款案件API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    // S211P 於交易完成後，端末判斷須列印申報書者，提示訊息，若選擇列印，則發S211P取得申報書資料列印
    @Override
    public Response<IRDto> prtStatement(@PathVariable("irNo") String irNo) {
        Response<IRDto> response = new Response<IRDto>();

        try {
            IRDto irDto = irService.getByIrNo(irNo);
            response.Success();
            response.setData(irDto);
            if ("4".equals(irDto.getPaidStats().toString())) {
                log.info("呼叫列印申報書資料API：列印匯入匯款編號" + irNo + "已解款");
            } else if ("0".equals(irDto.getPaidStats().toString())) {
                response.Error("S104", commonFeignClient.getErrorMessage("S104"));
                log.info("呼叫列印申報書資料API：列印匯入匯款編號" + irNo + "未解款");
            } else if ("5".equals(irDto.getPaidStats().toString())) {
                response.Error("S103", commonFeignClient.getErrorMessage("S103"));
                log.info("呼叫列印申報書資料API：列印匯入匯款編號" + irNo + "已退匯");
            }
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫列印申報書資料API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }
    @Override
    public Response<IRDto> qryWaitForReturnIRMaster(@PathVariable("irNo") String irNo) {
        Response<IRDto> response = new Response<IRDto>();
        try {
            IRDto irDto = irService.getByIrNo(irNo);
            response.Success();
            response.setData(irDto);
            if ("0".equals(irDto.getPaidStats().toString())) {
                log.info("呼叫分行退匯API：查詢匯入匯款編號" + irNo + "待處理");
            } else if ("4".equals(irDto.getPaidStats().toString())) {
                response.Error("S102", commonFeignClient.getErrorMessage("S102"));
                log.info("呼叫分行退匯API：查詢匯入匯款編號" + irNo + "已解款");
            } else if ("5".equals(irDto.getPaidStats().toString())) {
                response.Error("S103", commonFeignClient.getErrorMessage("S103"));
                log.info("呼叫分行退匯API：查詢匯入匯款編號" + irNo + "已退匯");
            }
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫分行退匯API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    // S611A	新增退匯交易
    @Override
    public Response<IRDto> exeReturnIRMaster(@PathVariable("irNo") String irNo) {
        Response<IRDto> response = new Response<IRDto>();
        try {
            IRDto irDto = irService.exeReturnIRMaster(irNo);
            response.Success();
            response.setData(irDto);
            log.info("呼叫匯入匯款退匯交易API：匯入匯款編號" + irNo + "已退匯");
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫匯入匯款退匯交易API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

    // S611C	更正退匯交易

    // S611D	剔除退匯交易
    @Override
    public Response<IRDto> deleteReturnIRMaster(@PathVariable("irNo") String irNo) {
        Response<IRDto> response = new Response<IRDto>();
        try {
            IRDto irDto = irService.delReturnIRMaster(irNo);
            response.Success();
            response.setData(irDto);
            log.info("呼叫剔除匯入匯款退匯交易API：匯入匯款編號" + irNo + "已剔除退匯");
        } catch (Exception e) {
            response.Error(e.getMessage(), commonFeignClient.getErrorMessage(e.getMessage()));
            log.info("呼叫剔除匯入匯款退匯交易API：" + commonFeignClient.getErrorMessage(e.getMessage()));
        }
        return response;
    }

}
