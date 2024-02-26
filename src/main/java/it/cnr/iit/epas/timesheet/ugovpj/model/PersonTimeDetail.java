package it.cnr.iit.epas.timesheet.ugovpj.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Entity
@Table(name = "ie_pj_marcature")
public class PersonTimeDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_ie_pj_marcature")
  private Long id;

  @Column(name = "data")
  private LocalDate date;

  @Column(name = "matricola")
  private String number;

  @Column(name = "marcatura")
  private Long minutes;
  
  @Column(name = "fl_consolidata")
  private int permanent = 0;
  
  @Column(name = "tipo_marcatura")
  private String type;

}
