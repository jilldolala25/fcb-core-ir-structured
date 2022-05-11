package tw.com.fcb.dolala.core.ir.infra.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tw.com.fcb.dolala.core.ir.domain.repository.IRMasterRepository;
import tw.com.fcb.dolala.core.ir.infra.entity.IRMaster;

import java.util.List;
import java.util.Optional;

/**
 * Copyright (C),2022,FirstBank
 * FileName: IRMasterRepositoryImpl
 * Author: Han-Ru
 * Date: 2022/5/11 下午 05:57
 * Description:
 */
@Repository
public class IRMasterRepositoryImpl implements IRMasterRepository {

    @Autowired
    IRMasterRepositoryJpa irMasterRepositoryJpa;


    @Override
    public String save(IRMaster irMaster) {
        return irMasterRepositoryJpa.save(irMaster).getCaseSeqNo();
    }

    @Override
    public Optional<IRMaster> findByIrNo(String irNo) {
        return irMasterRepositoryJpa.findByIrNo(irNo);
    }

    @Override
    public Optional<IRMaster> findByIrNoAndPaidStats(String irNo, Integer paidStats) {
        return irMasterRepositoryJpa.findByIrNoAndPaidStats(irNo,paidStats);
    }

    @Override
    public List<IRMaster> findByBeAdvBranch(String beAdvBranch) {
        return irMasterRepositoryJpa.findByBeAdvBranch(beAdvBranch);
    }

    @Override
    public List<IRMaster> findByBeAdvBranchAndPaidStats(String beAdvBranch, Integer paidStats) {
        return irMasterRepositoryJpa.findByBeAdvBranchAndPaidStats(beAdvBranch,paidStats);
    }

    @Override
    public List<IRMaster> findByBeAdvBranchAndPrintAdvMk(String beAdvBranch, String printAdvMk) {
        return irMasterRepositoryJpa.findByBeAdvBranchAndPrintAdvMk(beAdvBranch,printAdvMk);
    }

    @Override
    public List<IRMaster> findByBeAdvBranchAndPaidStatsAndPrintAdvMk(String beAdvBranch, Integer paidStats, String printAdvMk) {
        return irMasterRepositoryJpa.findByBeAdvBranchAndPaidStatsAndPrintAdvMk(beAdvBranch,paidStats,printAdvMk);
    }
}
