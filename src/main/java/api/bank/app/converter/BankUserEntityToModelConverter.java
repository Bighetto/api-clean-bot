package api.bank.app.converter;

import org.springframework.stereotype.Component;

import api.bank.app.model.Bank;
import api.bank.app.model.BankUser;
import api.bank.domain.entity.BankUserEntity;
import api.security.auth.app.model.UserLogin;
import api.utils.abstractClasses.ConvertCase;

@Component
public class BankUserEntityToModelConverter extends ConvertCase<BankUserEntity, BankUser> {

    @Override
    public BankUser convertToModel(BankUserEntity entity) {
        BankUser bankUser = new BankUser();
        bankUser.setId(entity.getId());

        UserLogin userLogin = new UserLogin();
        userLogin.setDocument(entity.getUserLoginId());
        bankUser.setUser(userLogin);

        Bank bank = new Bank();
        bank.setId(entity.getBankId());
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
