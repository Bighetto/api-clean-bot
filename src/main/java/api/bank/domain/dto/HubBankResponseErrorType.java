package api.bank.domain.dto;


import api.bank.app.exception.V8ResponseErrorTypeException;

public enum HubBankResponseErrorType {

    FIDUCIARY_INSTITUTION("Instituição Fiduciária"),
    WORKER_NOT_BIRTHYDAY("Trabalhador não possui adesão ao saque aniversário vigente na data corrente."),
    INSUFFICIENT_BALANCE("é menor do que o permitido"),
    CLIENT_NOT_AUTHORIZED("O cliente precisa liberar os dois bancos"),
    TOO_MANY_REQUESTS("Excedido o limite de requisições");
    
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
        throw new V8ResponseErrorTypeException("Error formating error message to error type");
    }
}
