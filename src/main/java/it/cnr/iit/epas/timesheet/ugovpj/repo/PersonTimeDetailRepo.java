package it.cnr.iit.epas.timesheet.ugovpj.repo;

import it.cnr.iit.epas.timesheet.ugovpj.model.PersonTimeDetail;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository per l'accesso e la gestione dei dati del PersonTimeDetail.
 *
 * @author Cristian Lucchesi
 */
public interface PersonTimeDetailRepo extends JpaRepository<PersonTimeDetail,Long> {
  //Empty interface
}