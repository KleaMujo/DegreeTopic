package DataObjects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String usernameDTO;
    private String passwordDTO;
    private String emailDTO;
    private String roleDTO;
}
