package SystemITR.JosueGuinea.modules.empleados.entity;

import SystemITR.JosueGuinea.modules.departamentos.entity.DepartamentosEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter @Setter @ToString
@Table(name = "TBEMPLEADOS")
public class EmpleadosEntity {

    @Id //<-- Anotación que determina que funciona como llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLEADO_ID")
    private Long id;
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "APELLIDO")
    private String apellido;
    @Column(name = "EMAIL", unique = true)
    private String email;
    @Column(name = "FECHA_INGRESO")
    private LocalDate fechaIngreso;
    @Column(name = "SALARIO")
    private double salario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTAMENTO_ID")
    private DepartamentosEntity departamento; //<-- Llave foránea


}
