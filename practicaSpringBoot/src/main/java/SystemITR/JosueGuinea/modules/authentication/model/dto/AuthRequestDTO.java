package SystemITR.JosueGuinea.modules.authentication.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
