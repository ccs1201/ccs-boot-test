package br.com.testes.ccsboot.services.exceptions;

import br.com.ccs.boot.server.http.enums.HttpStatusCode;
import br.com.ccs.boot.support.exceptions.CcsBootCustomException;

public class ServiceException extends CcsBootCustomException {

    public ServiceException(String s, HttpStatusCode httpStatusCode) {
        super(s, httpStatusCode);
    }

    public ServiceException(String s) {
        super(s);
    }
}
