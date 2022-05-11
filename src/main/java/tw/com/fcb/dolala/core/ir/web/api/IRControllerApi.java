package tw.com.fcb.dolala.core.ir.web.api;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tw.com.fcb.dolala.core.common.http.Response;
import tw.com.fcb.dolala.core.ir.application.command.IRSaveCmd;
import tw.com.fcb.dolala.core.ir.application.dto.IRAdvicePrintListDto;
import tw.com.fcb.dolala.core.ir.application.dto.IRDto;
import tw.com.fcb.dolala.core.ir.infra.entity.IRMaster;

import java.util.List;

/**
 * Copyright (C),2022,FirstBank
 * FileName: IRControllerApi
 * Author: Han-Ru
 * Date: 2022/5/11 上午 11:37
 * Description:
 */
public interface IRControllerApi {
    @PostMapping("/irmaster/insert")
    @Operation(description = "匯入匯款主檔資料寫入", summary = "新增匯入匯款主檔")
    public Response<IRMaster> irMasterInsert(@Validated @RequestBody IRSaveCmd irSaveCmd) ;

    @GetMapping("/irmaster/{branch}/count")
    @Operation(description = "傳入受通知單位查詢案件數", summary = "查詢匯入案件數")
    public Response<Integer> getCount(@PathVariable("branch") String branch);

    @GetMapping("/irmaster/{irNo}/")
    @Operation(description = "查詢匯入匯款主檔資料", summary = "查詢匯入匯款主檔資料")
    public Response<IRDto> getIRMasterByIrNo(@PathVariable("irNo") String irNo);

    @GetMapping("/irmaster/{irNo}/enquiry")
    @Operation(description = "傳入匯入匯款編號查詢案件明細", summary = "查詢匯入案件明細")
    public Response<IRDto> getByIrNo(@PathVariable("irNo") String irNo);

    @PutMapping("/irmaster/{irNo}/advice-print")
    @Operation(description = "變更印製通知書記號", summary = "印製通知書記號")
    public Response<?> print(@PathVariable("irNo") String irNo);

    @PutMapping("/irmaster/{irNo}/settle")
    @Operation(description = "變更付款狀態", summary = "付款狀態")
    public Response<?> settle(@PathVariable("irNo") String irNo);

    @PutMapping("/advice-print/{branch}/enquiry")
    @Operation(description = "進行通知書列印", summary = "通知書列印")
    public Response<List<IRDto>> qryAdvicePrint(String branch);

    @GetMapping("/advice-print/{branch}/count")
    @Operation(description = "受通知筆數", summary = "受通知筆數")
    public Response<int[]> qryAdviceCount(String branch);

    @GetMapping("/advice-print/{branch}/enquiry-list")
    @Operation(description = "分行通知書列表", summary = "通知書列表")
    public Response<List<IRAdvicePrintListDto>> qryAdviceList(String branch);

    @PostMapping("/originalccy-release/execute")
    @Operation(description = "新增原幣解款案件資料", summary = "新增原幣解款案件資料")
    public Response<IRDto> exeRelaseIRMaster(@Validated @RequestBody IRSaveCmd irSaveCmd);

    @PutMapping("/originalccy-release/{irNo}/delete")
    @Operation(description = "剔除原幣解款案件資料", summary = "剔除原幣解款案件資料")
    public Response<IRDto> delRelaseIRMaster(@PathVariable("irNo") String irNo);

    @GetMapping("/originalccy-release/{irNo}/enquiry")
    @Operation(description = "查詢匯入匯款案件資料", summary = "查詢匯入匯款案件資料")
    public Response<IRDto> qryWaitForRelaseIRMaster(@PathVariable("irNo") String irNo);

    @GetMapping("/originalccy-release/{irNo}/print-statement")
    @Operation(description = "列印申報書資料", summary = "列印申報書資料")
    public Response<IRDto> prtStatement(@PathVariable("irNo") String irNo);

    @GetMapping("/return-irmaster/{irNo}/enquiry")
    @Operation(description = "查詢匯入退匯資料", summary = "查詢匯入退匯資料")
    public Response<IRDto> qryWaitForReturnIRMaster(@PathVariable("irNo") String irNo);

    @PutMapping("/return-irmaster/{irNo}/execute")
    @Operation(description = "新增匯入匯款退匯交易", summary = "新增匯入匯款退匯交易")
    public Response<IRDto> exeReturnIRMaster(@PathVariable("irNo") String irNo);

    @PutMapping("/return-irmaster/{irNo}/delete")
    @Operation(description = "剔除匯入匯款退匯交易", summary = "剔除匯入匯款退匯交易")
    public Response<IRDto> deleteReturnIRMaster(@PathVariable("irNo") String irNo);


}
