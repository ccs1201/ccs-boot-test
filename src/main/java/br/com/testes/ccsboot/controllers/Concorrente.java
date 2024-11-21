package br.com.testes.ccsboot.controllers;

import br.com.ccs.boot.annotations.Endpoint;
import br.com.ccs.boot.annotations.EndpointResponseCode;
import br.com.ccs.boot.server.http.enums.HttpStatusCode;

@Endpoint("/concorrente")
public class Concorrente {

    @Endpoint.POST
    @EndpointResponseCode(HttpStatusCode.OK)
    public Integer post(Integer body) {
        return body;
    }
}
