package api.security.auth.domain.dataprovider;

import api.security.auth.domain.entity.TokenEntity;

public interface TokenDataProvider {

    TokenEntity generateToken(String userEmail);

}
