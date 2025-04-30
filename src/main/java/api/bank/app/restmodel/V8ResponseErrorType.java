package api.bank.app.restmodel;

public enum V8ResponseErrorType {

    VALUES_MUST_HAVE_AT_LEAST_ONE_VALUE("values() must be called with at least one value"),
    INSUFFICIENT_BALANCE("Saldo insuficiente"),
    CANNOT_READ_PROPERTY_MAP("Cannot read properties of undefined (reading 'map')"),
    FAILED_TO_FETCH_AVAILABLE_BALANCE("Falha ao buscar o saldo disponível!"),
    SERVICE_UNAVAILABLE("Serviço indisponível no momento, tente novamente mais tarde"),
    RATE_LIMIT_EXCEEDED("Excedido o limite de requisições (máximo de 1 por segundo)."),
    TOO_MANY_REQUESTS("Limite de requisições excedido, tente novamente mais tarde"),
    CLIENT_NOT_AUTHORIZED("Cliente não autorizou"),
    EMPTY_RESPONSE("Empty response"),
    COSTS_EXCEED_FINANCED_AMOUNT("Valor dos custos superior ao valor financiado"),
    FIDUCIARY_INSTITUTION("Instituição Fiduciária"),
    WORKER_NOT_BIRTHYDAY("Trabalhador não possui adesão ao saque aniversário vigente na data corrente.");
    
    private final String messageFragment;

    V8ResponseErrorType(String messageFragment) {
        this.messageFragment = messageFragment;
    }

    public String getMessageFragment() {
        return messageFragment;
    }

    public static V8ResponseErrorType fromMessage(String errorMessage) {
        for (V8ResponseErrorType type : values()) {
            if (errorMessage.contains(type.getMessageFragment())) {
                return type;
            }
        }
        throw new RuntimeException();
    }
}