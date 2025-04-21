package api.bank.domain.service;

import org.springframework.stereotype.Component;

import api.bank.app.converter.BankUserEntityToModelConverter;
import api.bank.app.model.BankUser;
import api.bank.domain.dataprovider.BankUserDataProvider;
import api.bank.domain.entity.BankUserEntity;
import api.bank.domain.usecase.UpdateBankUserNicknameUseCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UpdateBankUserNicknameService implements UpdateBankUserNicknameUseCase {
    
    private final BankUserDataProvider bankUserDataProvider;
    private final BankUserEntityToModelConverter bankUserEntityToModelConverter;
    
    @Override
    public void execute(String bankUserId, String newNickname) {
        BankUserEntity bankUserEntity = this.bankUserDataProvider.findBankUserById(bankUserId);

        if (!newNickname.equals(bankUserEntity.getNickname())) {
            bankUserEntity.setNickname(newNickname);
            BankUser bankUser = this.bankUserEntityToModelConverter.convertToModel(bankUserEntity);
            this.bankUserDataProvider.uploadBankUser(bankUser);
        }
    }

}
