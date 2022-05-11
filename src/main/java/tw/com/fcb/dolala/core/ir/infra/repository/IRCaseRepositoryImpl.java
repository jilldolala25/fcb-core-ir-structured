package tw.com.fcb.dolala.core.ir.infra.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tw.com.fcb.dolala.core.ir.domain.repository.IRCaseRepository;
import tw.com.fcb.dolala.core.ir.infra.entity.IRCaseEntity;

import java.util.Optional;

/**
 * Copyright (C),2022,FirstBank
 * FileName: IRCaseRepositoryImpl
 * Author: Han-Ru
 * Date: 2022/5/11 下午 05:47
 * Description:
 */
@Repository
public class IRCaseRepositoryImpl implements IRCaseRepository {
    @Autowired
    IRCaseRepositoryJpa irCaseRepositoryJpa;


    public  long save(IRCaseEntity irCaseEntity){
       return irCaseRepositoryJpa.save(irCaseEntity).getId();
    }

    @Override
    public Optional<IRCaseEntity> findBySeqNo(String irSeqNo) {
        return irCaseRepositoryJpa.findBySeqNo(irSeqNo);
    }

    @Override
    public Optional<IRCaseEntity> findBySeqNoAndProcessStatus(String seqNo, String processStatus) {
        return irCaseRepositoryJpa.findBySeqNoAndProcessStatus(seqNo,processStatus);
    }
}
