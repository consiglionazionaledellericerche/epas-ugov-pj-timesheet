package it.cnr.iit.epas.timesheet.ugovpj;

import static org.mockito.Mockito.when;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import it.cnr.iit.epas.timesheet.ugovpj.client.EpasClient;
import it.cnr.iit.epas.timesheet.ugovpj.repo.PersonTimeDetailRepo;
import it.cnr.iit.epas.timesheet.ugovpj.service.TimeDetailTypeService;
import jakarta.inject.Inject;

@ActiveProfiles(value = {"test", "h2"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringCommonTest {

  @MockitoBean
  TimeDetailTypeService timeDetailTypeService;

  @MockitoBean
  protected EpasClient epasClient;

  @Inject
  PersonTimeDetailRepo repo;

  @BeforeEach
  public void setUp() {
    when(timeDetailTypeService.timeDetailTypes()).thenReturn(Sets.set("N", "F", "T", "X"));
    repo.deleteAll();
  }

}