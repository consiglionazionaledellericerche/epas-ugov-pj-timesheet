package it.cnr.iit.epas.timesheet.ugovpj;

import static org.mockito.Mockito.when;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import it.cnr.iit.epas.timesheet.ugovpj.service.TimeDetailTypeService;

@ActiveProfiles( value = {"test","h2"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringCommonTest {

  @MockBean
  TimeDetailTypeService timeDetailTypeService;

  @BeforeEach
  public void setUp() {
    when(timeDetailTypeService.timeDetailTypes()).thenReturn(Sets.set("N", "F", "T"));
  }

}