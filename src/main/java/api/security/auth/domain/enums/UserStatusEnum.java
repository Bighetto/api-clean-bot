package api.security.auth.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatusEnum {
    
    ATIVO("ativo"),
    INATIVO("inativo");

    private final String value;

    UserStatusEnum(String value) {
        this.value = value;
    }

    @JsonCreator
    public static UserStatusEnum fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("UserStatusEnum cannot be null");
        }
        for (UserStatusEnum status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid UserStatusEnum value: " + value);
    }

    @JsonValue
    public String toJson() {
        return value;
    }
}
