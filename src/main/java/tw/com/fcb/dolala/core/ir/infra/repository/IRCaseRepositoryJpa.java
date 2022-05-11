package tw.com.fcb.dolala.core.ir.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.fcb.dolala.core.ir.domain.repository.IRCaseRepository;
import tw.com.fcb.dolala.core.ir.infra.entity.IRCaseEntity;

import java.util.Optional;

/**
 * Copyright (C),2022,FirstBank
 * FileName: IRCaseRepository
 * Author: Han-Ru
 * Date: 2022/5/11 下午 01:15
 * Description:
 */
@Repository
public interface IRCaseRepositoryJpa extends JpaRepository<IRCaseEntity, Long>  {
    Optional<IRCaseEntity> findBySeqNo(String irSeqNo);

    Optional<IRCaseEntity> findBySeqNoAndProcessStatus(String seqNo, String processStatus);

}
