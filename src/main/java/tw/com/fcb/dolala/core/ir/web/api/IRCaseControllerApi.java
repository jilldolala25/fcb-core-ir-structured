package tw.com.fcb.dolala.core.ir.web.api;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tw.com.fcb.dolala.core.common.http.Response;
import tw.com.fcb.dolala.core.ir.application.command.SwiftMessageSaveCmd;
import tw.com.fcb.dolala.core.ir.application.dto.IRCaseDto;
import tw.com.fcb.dolala.core.ir.application.dto.IRDto;

import javax.validation.constraints.NotNull;

/**
 * Copyright (C),2022,FirstBank
 * FileName: IRCaseControllerApi
 * Author: Han-Ru
 * Date: 2022/5/11 上午 11:47
 * Description:
 */
public interface IRCaseControllerApi {
    @PostMapping("/ircase/receive-swift")
    @Operation(description = "接收 SWIFT 電文並存到 SwiftMessage", summary = "接收及儲存 SWIFT 電文")
    public Response<String> receiveSwift(@Validated @RequestBody SwiftMessageSaveCmd message);

    @PutMapping("/ircase/{irSeqNo}/autopass")
    @Operation(description = "檢核電文是否可自動放行", summary = "更新AUTO_PASS欄位")
    public Response<String> checkAutoPassMK(@NotNull @PathVariable("irSeqNo") String irSeqNo);

    @GetMapping("/ircase/{irSeqNo}/enquiry")
    @Operation(description = "取得seqNo電文資料", summary = "取得seqNo電文資料")
    public Response<IRCaseDto> getBySeqNo(@NotNull @PathVariable("irSeqNo") String irSeqNo);

    @GetMapping("/ircase-authorization/{seqNo}/enquiry")
    @Operation(description = "查詢待放行資料", summary = "查詢待放行")
    public Response<IRCaseDto> qryWaitForAuthorization(@PathVariable("seqNo") String seqNo);

    @PutMapping("/ircase-authorization/{seqNo}/execute")
    @Operation(description = "執行MT103放行", summary = "MT103放行")
    public Response<IRDto> exeCaseAuthorization(@PathVariable("seqNo") String seqNo);

    @GetMapping("/ircase-entry/{seqNo}/enquiry")
    @Operation(description = "查詢匯款電文資料", summary = "查詢匯款電文資料")
    public Response<IRCaseDto> qryIRCase(@PathVariable("seqNo") String seqNo);

    @PutMapping("/return-ircase/{seqNo}/execute")
    @Operation(description = "退匯作業(無匯入編號)", summary = "退匯作業(無匯入編號)")
    public Response<IRCaseDto> exeReturnIRCase(@PathVariable("seqNo") String seqNo);

    @PutMapping("/return-ircase/{seqNo}/delete")
    @Operation(description = "退匯刪除作業(無匯入編號)", summary = "退匯刪除作業(無匯入編號)")
    public Response<IRCaseDto> deleteReturnIRCase(@PathVariable("seqNo") String seqNo);

    @GetMapping("/return-ircase/{seqNo}/enquiry")
    @Operation(description = "查詢待退匯案件(無匯入編號)", summary = "查詢待退匯案件(無匯入編號)")
    public Response<IRCaseDto> qryWaitForReturnIRCase(@PathVariable("seqNo") String seqNo);
}
