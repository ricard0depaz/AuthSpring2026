package SystemITR.JosueGuinea.modules.departamentos.entity;

import SystemITR.JosueGuinea.modules.empleados.entity.EmpleadosEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @ToString
@Table(name = "TBDEPARTAMENTOS")
public class DepartamentosEntity {

    @Id //-> id será la llave primaria a nivel de código.
    @GeneratedValue(strategy = GenerationType.IDENTITY) //-> Esta anotación es importante ya que describe como el valor de la PK se genera.
    @Column(name = "DEPARTAMENTO_ID")
    private Long id;
    @Column(name = "NOMBRE_DEPT")
    private String nombreDepto;
    @Column(name = "ABREVIATURA")
    private String abreviatura;
    @Column(name = "UBICACION")
    private String ubicacion;

    @OneToMany(mappedBy = "departamento")
    private List<EmpleadosEntity> empleados = new ArrayList<>();
}
