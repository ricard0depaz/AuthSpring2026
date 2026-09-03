package SystemITR.JosueGuinea.modules.authentication.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private long id;
    private String username;
    private String rol;
    private String mensaje;
}
