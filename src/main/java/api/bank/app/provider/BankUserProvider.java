package api.bank.app.provider;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import api.bank.app.converter.BankUserModelToEntityConverter;
import api.bank.app.model.BankUser;
import api.bank.app.repository.BankUserRepository;
import api.bank.domain.dataprovider.BankDataProvider;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.entity.BankUserEntity;
import api.security.auth.app.model.UserLogin;
import api.security.auth.domain.dataprovider.AuthDataProvider;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BankUserProvider implements BankUserDataProvider {

    private final BankUserRepository bankUserRepository;
    private final AuthDataProvider authDataProvider;
    private final BankDataProvider bankDataProvider;
    private final BankUserModelToEntityConverter bankUserModelToEntityConverter;

    @Override
    public void uploadBankUser(BankUser bankUser) {
        this.bankUserRepository.save(bankUser);
    }

    @Override
    public List<BankUserEntity> findUsersBankByUser(UserLogin document) {
        List<BankUser> modelsList = this.bankUserRepository.findByUser(document);

        if (modelsList.isEmpty()) {
            throw new RuntimeException();
        }

        return modelsList.stream()
            .map(model -> bankUserModelToEntityConverter.convertToEntity(model))
            .collect(Collectors.toList());
    }

    @Override
    public Boolean existsByLoginAndBankId(String login, String bankId) {
        return this.bankUserRepository.existsByLoginAndBankId(login, bankId);
    }

    @Override
    public Integer deleteBankUserById(String id) {
        return this.bankUserRepository.deleteByIdReturningCount(id);
    }
}
