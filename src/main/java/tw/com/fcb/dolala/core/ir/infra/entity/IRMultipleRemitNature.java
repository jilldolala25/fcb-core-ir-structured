package tw.com.fcb.dolala.core.ir.infra.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author sinjen
 * 匯入解款匯款性質分割資料檔
 */
@Entity
@Table(name = "IR_MULTIPLE_REMIT_NATURE")
@Data
public class IRMultipleRemitNature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MULTIPLE_REMIT_NATURE_SEQ_NO")
    String multipleRemitNatureSeqNo;    //流水編號

    @Column(name = "KEY_SEQ")
    String keySeq;    //指定單位字軌(NH,AW...)

    @Column(name = "TX_CODE")
    String txCode;    //交易代號(421,451...)

    @Column(name = "IR_NO")
    String irNo;    //匯入匯款編號

    @Column(name = "PROCESS_MOD")
    String process_Mod;    //處理方式(A,C,D)

    @Column(name = "PROCESS_DATE")
    LocalDate processDate;    //處理日期

    @Column(name = "FX_DEP_MARK")
    String fxDepMark;    //存入外匯存款性質

    @Column(name = "FX_DEP_ORG_MARK")
    String fxDepOrgMark;    //存入外匯存款-原匯款性質

    @Column(name = "FX_DEP_AMT")
    String fxDepAmt;    //存入外匯存款金額

    @Column(name = "REMIT_D_MARK")
    String remitDMark;    //轉匯國內他行性質

    @Column(name = "REMIT_D_ORG_MARK")
    String remitDOrgMark;    //轉匯國內他行-原匯款性質

    @Column(name = "REMIT_D_AMT")
    String remitDAmt;    //轉匯國內他行金額

    @Column(name = "OTHER_MARK")
    String otherMark;    //其他性質

    @Column(name = "OTHER_ORG_MARK")
    String otherOrgMark;    //其他-原匯款性質

    @Column(name = "OTHER_AMT")
    String otherAmt;    //其他金額

    @Column(name = "FWD_SETTLED_MARK")
    String fwdSettledMark;    //遠期結匯性質

    @Column(name = "FWD_SETTLED_ORG_MARK")
    String fwdSettledOrgMark;    //遠期結匯-原匯款性質

    @Column(name = "FWD_SETTLED_AMT")
    String fwdSettledAmt;    //遠期結匯金額

    @Column(name = "SPOT_SETTLED_MARK")
    String spotSettledMark;    //即期結匯性質

    @Column(name = "SPOT_SETTLED_ORG_MARK")
    String spotSettledOrgMark;    //即期結匯-原匯款性質

    @Column(name = "SPOT_SETTLED_AMT")
    String spotSettledAmt;    //即期結匯金額

    @Column(name = "FX_LOAN_MARK")
    String fxLoanMark;    //扣還外幣貸款性質

    @Column(name = "FX_LOAN_ORG_MARK")
    String fxLoanOrgMark;    //扣還外幣貸款-原匯款性質

    @Column(name = "FX_LOAN_AMT")
    String fxLoanAmt;    //扣還外幣貸款金額

    @Column(name = "CREATE_DATE")
    LocalDate createDate;    //資料建立日期

    @Column(name = "AMEND_DATE")
    LocalDate amendDate;    //資料異動日期

    @Column(name = "FCB_AGENT_ID")
    String fcbAgentID;    //經辦代碼

    @Column(name = "STATUS")
    String status;    //資料狀態
}
