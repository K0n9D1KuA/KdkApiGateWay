package exception;

import enums.ResponseCode;

public class LimitException extends BaseException{

    private static final long serialVersionUID = -5534700534739261761L;


    public LimitException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public LimitException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
    }
}
