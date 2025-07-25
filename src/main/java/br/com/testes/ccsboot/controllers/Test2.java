package br.com.testes.ccsboot.controllers;

import br.com.ccs.boot.annotations.Endpoint;
import br.com.ccs.boot.annotations.EndpointResponseCode;
import br.com.ccs.boot.server.http.enums.HttpMethod;
import br.com.ccs.boot.server.http.enums.HttpStatusCode;
import br.com.testes.ccsboot.models.input.InputTest;
import br.com.testes.ccsboot.models.output.ResponseTest2;
import jakarta.inject.Inject;
import org.slf4j.Logger;

@Endpoint("test/2")
public class Test2 {

    private final Logger log;

    @Inject
    public Test2(Logger log) {
        this.log = log;
    }

    @Endpoint.POST
    @EndpointResponseCode(HttpStatusCode.CREATED)
    public ResponseTest2 test(InputTest request) {
        log.info("teste controller 2");
        log.info("Request: {}", request);

        return new ResponseTest2(request.dataHora(),
                request.mensagem(),
                HttpStatusCode.OK,
                HttpMethod.POST,
                request.aBoolean(), //!= null && request.aBoolean(),
                this.getClass());
    }
}
