package it.cnr.iit.epas.timesheet.ugovpj.repo;

import it.cnr.iit.epas.timesheet.ugovpj.model.TimeDetailType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository per l'accesso e la gestione dei dati dei tipi di informazioni sui
 * tempi scambiate con il sistema di timesheet.
 *
 * @author Cristian Lucchesi
 */
public interface TimeDetailTypeRepo extends JpaRepository<TimeDetailType,Long> {
  //Empty interface
}