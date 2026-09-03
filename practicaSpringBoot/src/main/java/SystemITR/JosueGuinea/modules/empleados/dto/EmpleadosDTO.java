package SystemITR.JosueGuinea.modules.empleados.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmpleadosDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate fecha_ingreso;
    private double salario;
    private Long idDepartamento;

    //Campo adicional
    private String nombreDepartamento;
}
