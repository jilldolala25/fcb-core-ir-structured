package tw.com.fcb.dolala.core.ir.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tw.com.fcb.dolala.core.ir.infra.enums.ChargeType;
import tw.com.fcb.dolala.core.ir.application.command.SwiftMessageSaveCmd;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author Han-Ru
 * SWIFT MT103進電處理
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@AutoConfigureMockMvc

class IRCaseControllerTest {

	@Autowired
	 MockMvc mockMvc;
	@Autowired
	ObjectMapper mapper;

// 一般自動放行案件
	@Test
	void receiveSwift() throws Exception {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		SwiftMessageSaveCmd swiftMessageSaveCmd = new SwiftMessageSaveCmd();

		swiftMessageSaveCmd.setReferenceNo("test-swift-1");
		swiftMessageSaveCmd.setChargeType(ChargeType.valueOf("SHA"));
		swiftMessageSaveCmd.setReceiveDate(LocalDate.now());
		swiftMessageSaveCmd.setValueDate(LocalDate.now());
		swiftMessageSaveCmd.setIrAmount(BigDecimal.valueOf(100));
		swiftMessageSaveCmd.setReceiverAccount("/09357654321");
		swiftMessageSaveCmd.setSenderSwiftCode("CITIUS33XXX");
		swiftMessageSaveCmd.setCurrency("USD");

		var insert = mockMvc.perform(post("/ir/ircase/receive-swift")
						.content(mapper.writeValueAsString(swiftMessageSaveCmd))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString(Charset.defaultCharset());

		System.out.println("insert: " + insert);
	}

	// 期交所案件
	@Test
	void receiveFESwift() throws Exception {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		SwiftMessageSaveCmd swiftMessageSaveCmd = new SwiftMessageSaveCmd();

		swiftMessageSaveCmd.setReferenceNo("FutureExchange");
		swiftMessageSaveCmd.setChargeType(ChargeType.valueOf("SHA"));
		swiftMessageSaveCmd.setReceiveDate(LocalDate.now());
		swiftMessageSaveCmd.setValueDate(LocalDate.now());
		swiftMessageSaveCmd.setIrAmount(BigDecimal.valueOf(100));
		swiftMessageSaveCmd.setReceiverAccount("/17140014156");
		swiftMessageSaveCmd.setSenderSwiftCode("CITIUS33XXX");
		swiftMessageSaveCmd.setCurrency("USD");

		var feInsert = mockMvc.perform(post("/ir/ircase/receive-swift")
						.content(mapper.writeValueAsString(swiftMessageSaveCmd))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString(Charset.defaultCharset());

		System.out.println("feInsert: " + feInsert);
	}
	@Test
	void receiveNoAutoSwift() throws Exception {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		SwiftMessageSaveCmd swiftMessageSaveCmd = new SwiftMessageSaveCmd();

// 期交所案件
		swiftMessageSaveCmd.setReferenceNo("NoAutoPassSwiftMessage");
		swiftMessageSaveCmd.setChargeType(ChargeType.valueOf("SHA"));
		swiftMessageSaveCmd.setReceiveDate(LocalDate.now());
		swiftMessageSaveCmd.setValueDate(LocalDate.now());
		swiftMessageSaveCmd.setIrAmount(BigDecimal.valueOf(100));
		swiftMessageSaveCmd.setReceiverAccount("/09311123456");
		swiftMessageSaveCmd.setSenderSwiftCode("CITIUS33XXX");
		swiftMessageSaveCmd.setCurrency("USD");

		var noAutoInsert = mockMvc.perform(post("/ir/ircase/receive-swift")
						.content(mapper.writeValueAsString(swiftMessageSaveCmd))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print())
				.andReturn().getResponse().getContentAsString(Charset.defaultCharset());
		System.out.println("noAutoInsert: " + noAutoInsert);
	}


}
