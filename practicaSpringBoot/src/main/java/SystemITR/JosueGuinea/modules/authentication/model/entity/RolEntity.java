package SystemITR.JosueGuinea.modules.authentication.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "TBROLES")
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROL_ID")
    private Long rolId;

    @Column(name = "NOMBRE_ROL", nullable = false, unique = true, length = 50)
    private String nombreRol;

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;
}
