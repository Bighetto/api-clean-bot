package api.bank.domain.dto;


import api.bank.app.exception.HubBankResponseErrorTypeException;

public enum HubBankResponseErrorType {

    COSTS_HIGHER_THAN_FINANCED_AMOUNT("superior ao valor financiado"),
    WORKER_NOT_IN_BIRTHDAY_WITHDRAWAL("não possui adesão ao saque aniversário"),
    WORKER_HAS_NO_FGTS_ACCOUNT("não possui contas de FGTS"),
    OPERATION_TEMPORARILY_BLOCKED("Operação não permitida antes de"),
    BANKS_NOT_AUTHORIZED("precisa liberar os dois bancos"),
    INVALID_CPF("CPF inválido"),
    FGTS_ACCOUNT_RECENTLY_CHANGED("Mudanças cadastrais ou lançamentos a débito"),
    NO_AVAILABLE_BALANCE("não possui saldo disponível"),

    SIMULATION_IN_PROGRESS("Simulação em andamento"),
    REQUEST_TIMEOUT("Read timed out"),
    QUERY_LIMIT_EXCEEDED("Limite de consultas execedido"),
    FIDUCIARY_OPERATION_IN_PROGRESS("Operação Fiduciária em andamento"),
    TOO_MANY_REQUESTS("Excedido o limite de requisições"),

    INSUFFICIENT_BALANCE("é menor do que o permitido"),
    FIDUCIARY_INSTITUTION("Instituição Fiduciária");
    
    private final String messageFragment;

    HubBankResponseErrorType(String messageFragment) {
        this.messageFragment = messageFragment;
    }

    public String getMessageFragment() {
        return messageFragment;
    }

    public static HubBankResponseErrorType fromMessage(String errorMessage) {
        for (HubBankResponseErrorType type : values()) {
            if (errorMessage.contains(type.getMessageFragment())) {
                return type;
            }
        }
        throw new HubBankResponseErrorTypeException("Error formating error message to error type");
    }
}
