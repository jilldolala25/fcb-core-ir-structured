package tw.com.fcb.dolala.core.ir.domain.repository;

import tw.com.fcb.dolala.core.ir.infra.entity.IRCaseEntity;

import java.util.Optional;

/**
 * Copyright (C),2022,FirstBank
 * FileName: IRCaseRepositoryImpl
 * Author: Han-Ru
 * Date: 2022/5/11 下午 01:08
 * Description:
 */
public interface IRCaseRepository {

    long save(IRCaseEntity irCaseEntity);

    Optional<IRCaseEntity> findBySeqNo(String irSeqNo);

    Optional<IRCaseEntity> findBySeqNoAndProcessStatus(String seqNo, String processStatus);

}
