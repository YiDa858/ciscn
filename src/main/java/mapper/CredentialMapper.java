package mapper;

import pojo.Credential;

import java.util.List;

public interface CredentialMapper {
    List<Credential> getCredentialsByName(String username);
}
