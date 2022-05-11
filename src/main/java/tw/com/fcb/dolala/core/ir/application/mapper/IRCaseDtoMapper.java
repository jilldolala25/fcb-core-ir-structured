package tw.com.fcb.dolala.core.ir.application.mapper.mapper;

import org.mapstruct.Mapper;
import tw.com.fcb.dolala.core.ir.application.command.SwiftMessageSaveCmd;
import tw.com.fcb.dolala.core.ir.application.dto.IRCaseDto;

/**
 * Copyright (C),2022,FirstBank
 * FileName: IRCaseDtoMapper
 * Author: Han-Ru
 * Date: 2022/4/17 下午 02:07
 * Description: IRCaseToDtoMapper
 */
@Mapper
public interface IRCaseDtoMapper {


    IRCaseDto cmdToIRCaseDto(SwiftMessageSaveCmd saveCmd);

}
