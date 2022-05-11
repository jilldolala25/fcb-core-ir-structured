package tw.com.fcb.dolala.core.ir.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "匯入匯款案件主檔S131I2電文	發查結果")
@Data
public class IRAdvicePrintListDto {
    @Schema(description = "匯入匯款主檔系統編號")
    String caseSeqNo;

    @Schema(description = "匯入匯款編號")
    String irNo;

    @Schema(description = "印製通知書記號")
    String printAdvMk;

    @Schema(description = "幣別")
    String curency;

    @Schema(description = "匯入匯款金額")
    BigDecimal irAmt;

    @Schema(description = "受款人一")
    String receiverInfo1;

    @Schema(description = "有效日")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate valueDate;

    @Schema(description = "通知日")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate advDate;

}
