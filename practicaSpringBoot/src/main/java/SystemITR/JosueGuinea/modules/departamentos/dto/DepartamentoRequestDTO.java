package SystemITR.JosueGuinea.modules.departamentos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class DepartamentoRequestDTO {

    private Long id;
    @NotBlank (message = "ERR01: El nombre del departamento es obligatorio")
    @Size(max = 50, min = 5, message = "ERR02: El nombre del departamento puede contener un máximo de 50 caracteres.")
    private String nombreDepto;
    @Size(max = 5, message = "ERR03: Error al crear la abreviación, 5 caracteres como máximo")
    private String abreviatura;
    private String ubicacion;
}
