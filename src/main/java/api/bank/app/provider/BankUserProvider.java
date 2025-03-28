package api.bank.app.provider;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import api.bank.app.converter.BankUserModelToEntityConverter;
import api.bank.app.model.BankUser;
import api.bank.app.repository.BankUserRepository;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.entity.BankUserEntity;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BankUserProvider implements BankUserDataProvider {

    @Autowired
    BankUserRepository bankUserRepository;

    @Autowired
    BankUserModelToEntityConverter bankUserModelToEntityConverter;


    @Override
    public List<BankUserEntity> findUsersBankByUserDocument(String document) {
        List<BankUser> modelsList = this.bankUserRepository.findByUserDocument(document);

        if (modelsList.isEmpty()) {
            throw new RuntimeException();
        }

        return modelsList.stream()
            .map(model -> bankUserModelToEntityConverter.convertToEntity(model))
            .collect(Collectors.toList());
    }

}
