package it.cnr.iit.epas.timesheet.ugovpj.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Modello per il mapping della Tabella Oracle di UGOV-Pj-Timesheet che contiene i tipi
 * di dettaglio delle marcature e/o assenze.
 *
 * @author Cristian Lucchesi
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
@Entity
@Table(name = "ie_pj_marcature_tipi")
public class TimeDetailType {

  @Id
  @Column(name = "ID_TIPO")
  private Long id;

  @Column(name = "CD_TIPO")
  private String code; 

  @Column(name = "DS_TIPO")
  private String description;
  
  @Column(name = "CD_PROGETTO")
  private String projectCode;
  
  @Column(name = "CD_UNITA_LAVORO")
  private String WorkUnitCode;

}