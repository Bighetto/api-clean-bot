package api.bank.app.converter;

import org.springframework.stereotype.Component;

import api.bank.app.model.Bank;
import api.bank.app.model.BankUser;
import api.bank.domain.dataprovider.BankDataProvider;
import api.bank.domain.entity.BankEntity;
import api.bank.domain.entity.BankUserEntity;
import api.security.auth.app.model.UserLogin;
import api.utils.abstractClasses.ConvertCase;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BankUserEntityToModelConverter extends ConvertCase<BankUserEntity, BankUser> {

    private final BankDataProvider bankDataProvider;

    @Override
    public BankUser convertToModel(BankUserEntity entity) {
        BankUser bankUser = new BankUser();
        bankUser.setId(entity.getId());

        UserLogin userLogin = new UserLogin();
        userLogin.setDocument(entity.getUserLoginId());
        bankUser.setUser(userLogin);

        BankEntity bankEntity = this.bankDataProvider.findByName(entity.getBankName());
        Bank bank = new Bank();
        bank.setId(bankEntity.getId());
        bankUser.setBank(bank);

        bankUser.setLogin(entity.getLogin());
        bankUser.setPassword(entity.getPassword());
        bankUser.setNickname(entity.getNickname());

        return bankUser;
    }

    @Override
    public BankUserEntity convertToEntity(BankUser model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToEntity'");
    }

}
