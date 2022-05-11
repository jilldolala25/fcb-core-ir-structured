package tw.com.fcb.dolala.core.ir.application.mapper.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import tw.com.fcb.dolala.core.ir.infra.entity.IRMaster;
import tw.com.fcb.dolala.core.ir.application.command.IRSaveCmd;
import tw.com.fcb.dolala.core.ir.application.dto.IRAdvicePrintListDto;
import tw.com.fcb.dolala.core.ir.application.dto.IRCaseDto;
import tw.com.fcb.dolala.core.ir.application.dto.IRDto;

@Mapper
public interface IRMasterMapper {

    IRMaster irDtoToIRMaster(IRDto irDto);


    IRDto irMasterToIRDto(IRMaster irMaster);



    IRMaster irSaveCmdToIRMaster(IRSaveCmd irSaveCmd);

    IRSaveCmd irCaseDtoToIRSaveCmd(IRCaseDto irCaseDto);

    IRAdvicePrintListDto irMasterToIrAdvicePrintDto(IRMaster irMaster);


    IRMaster updateIRMasterFromirSaveCmd(IRSaveCmd irSaveCmd,@MappingTarget IRMaster irMaster);


}
