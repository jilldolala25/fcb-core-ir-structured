package tw.com.fcb.dolala.core.ir.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tw.com.fcb.dolala.core.ir.infra.entity.IRMaster;
import tw.com.fcb.dolala.core.ir.infra.repository.IRMasterRepositoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class IRMasterRepositoryTest {

	@Autowired
	IRMasterRepositoryImpl repository;

	@Test
	void testFindByIrNo() {
		IRMaster irMaster = repository.findByIrNo("S1NHA00947").orElseThrow();
		assertEquals("3456.78", irMaster.getIrAmount().toString());
	}

}
