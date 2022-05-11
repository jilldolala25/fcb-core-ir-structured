package tw.com.fcb.dolala.core.ir.application.mapper.mapper;

import org.mapstruct.Mapper;
import tw.com.fcb.dolala.core.ir.infra.entity.IRCaseEntity;
import tw.com.fcb.dolala.core.ir.application.command.IRSaveCmd;
import tw.com.fcb.dolala.core.ir.application.dto.IRCaseDto;

@Mapper
public interface IRCaseMapper {

    IRCaseEntity irCaseDtoToEntity(IRCaseDto irCaseDto);

    IRCaseDto irCaseEntityToDto(IRCaseEntity irCaseEntity);

    IRSaveCmd irCaseDtoToIRSaveCmd(IRCaseDto irCaseDto);

}

