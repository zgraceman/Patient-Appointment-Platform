package org.perscholas.mbs;

import org.junit.jupiter.api.Test;
import org.perscholas.mbs.dao.PatientRepoI;
import org.perscholas.mbs.models.Patient;
import org.perscholas.mbs.service.PatientService;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MbsNewApplicationTests {

	private PatientService patientService;


	@Test
	public void verifyPatient() throws Exception{

		Patient testUser = new Patient("User", "User@gmail.com", "password");
		Patient expectedPatient = patientService.getAllPatients().get(0);

		assertThat(testUser).isEqualTo(expectedPatient);
	}

}
