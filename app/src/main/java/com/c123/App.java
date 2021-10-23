package com.c123;

import com.c123.model.User;
import com.google.gson.Gson;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.VertxBuilder;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    public static void main(String ...args) {

        Vertx vertx = new VertxBuilder().init().vertx();
        Gson gson = new Gson();
        Logger log = LoggerFactory.getLogger(App.class);

        JksOptions keyStoreOptions = new JksOptions()
                .setPassword("abc123")
                .setPath("ssl/KeyStore.jks");

        JksOptions trustStoreOptions = new JksOptions()
                .setPassword("abc123")
                .setPath("ssl/truststore.jks");

        HttpServerOptions httpOptions = new HttpServerOptions()
                .setSsl(true)
                .setUseAlpn(true)
                .setLogActivity(true)
                .addEnabledSecureTransportProtocol("TLSv1.3")
                .removeEnabledSecureTransportProtocol("TLSv1.2")
                .setKeyStoreOptions(keyStoreOptions)
                .setTrustStoreOptions(trustStoreOptions);

        HttpServer server = vertx.createHttpServer(httpOptions);

        Router router = Router.router(vertx);

        router.route().handler(context ->
                    context.response().setStatusCode(200)
                            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .end("{\"msg\":\"I'm healthy\"}")
                )
                .failureHandler(ctx -> System.out.println("FAILURE"))
                .method(HttpMethod.GET)
                .path("/users")
                .produces("application/json");

        router.route().handler(BodyHandler.create());

        router.route().handler(context -> {
                    JsonObject json = context.getBodyAsJson();
                    User user = gson.fromJson(json.encode(), User.class);
                    log.debug(user.toString());
                    context.response()
                            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .end(json.encode());
                })
                .failureHandler(ctx -> System.out.println("FAILURE"))
                .method(HttpMethod.POST)
                .path("/users/login")
                .consumes("application/json")
                .produces("application/json");

        router.route().handler(context -> {
                    JsonObject json = context.getBodyAsJson();
                    User user = gson.fromJson(json.encode(), User.class);
                    log.debug(user.toString());
                    context.response()
                            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .end(json.encode());
                })
                .failureHandler(ctx -> System.out.println("FAILURE"))
                .method(HttpMethod.POST)
                .path("/users/register")
                .consumes("application/json")
                .produces("application/json");

        server.requestHandler(router)
                .listen(9000, "127.0.0.1");

    }
}
