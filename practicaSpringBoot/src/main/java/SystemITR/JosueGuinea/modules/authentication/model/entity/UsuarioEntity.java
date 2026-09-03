package SystemITR.JosueGuinea.modules.authentication.model.entity;

import SystemITR.JosueGuinea.modules.empleados.entity.EmpleadosEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "TBUSUARIOS")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USUARIO_ID")
    private Long usuarioId;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String passwordhash;

    @Column(name = "ESTADO", nullable = false, length = 20)
    private String estado;

    @Column(name = "FECHA_CREACION")
    private LocalDate fechaCreacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROL_ID", nullable = false)
    private RolEntity rol;

    @OneToOne
    @JoinColumn(name = "EMPLEADO_ID", unique = true)
    private EmpleadosEntity empleado;
}
