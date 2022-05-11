package tw.com.fcb.dolala.core.ir.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.com.fcb.dolala.core.ir.domain.repository.IRMasterRepository;
import tw.com.fcb.dolala.core.ir.infra.entity.IRMaster;

import java.util.List;
import java.util.Optional;

/**
 * Copyright (C),2022,FirstBank
 * FileName: IRMasterRepositoryJpa
 * Author: Han-Ru
 * Date: 2022/5/11 下午 01:30
 * Description:
 */
@Repository
public interface IRMasterRepositoryJpa extends JpaRepository<IRMaster, Long> {

    Optional<IRMaster> findByIrNo(@Param("irNo") String irNo);

    Optional<IRMaster> findByIrNoAndPaidStats(@Param("irNo") String irNo, @Param("paidStats") Integer paidStats);

    //@Query(name="findByBeAdvBranch",nativeQuery = true,value = "select * from IR_APPLY_MASTER where BE_ADV_BRANCH=:beAdvBranch AND PRINT_ADV_MK = 'N'")

    List<IRMaster> findByBeAdvBranch(@Param("beAdvBranch") String beAdvBranch);

    List<IRMaster> findByBeAdvBranchAndPaidStats(@Param("beAdvBranch") String beAdvBranch, @Param("paidStats") Integer paidStats);

    List<IRMaster> findByBeAdvBranchAndPrintAdvMk(@Param("beAdvBranch") String beAdvBranch, @Param("printAdvMk") String printAdvMk);

    List<IRMaster> findByBeAdvBranchAndPaidStatsAndPrintAdvMk(@Param("beAdvBranch") String beAdvBranch,
                                                              @Param("paidStats") Integer paidStats, @Param("printAdvMk") String printAdvMk);
}
