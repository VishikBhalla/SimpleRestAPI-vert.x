package io.vertx.blog.first;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import restAPI.API;
import restAPI.ArbritraryObject;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests the CRUD methods written in the API class
 * @author Vishik Bhalla
 *
 */


@RunWith(VertxUnitRunner.class)
public class APITests {

  private Vertx vertx;

  private int port = 8000;


  @Before
  public void setUp(TestContext context) {
    vertx = Vertx.vertx();

    ServerSocket socket;
    try {
      socket = new ServerSocket(0);

      port = socket.getLocalPort();
      socket.close();

      DeploymentOptions options = new DeploymentOptions()
          .setConfig(new JsonObject().put("http.port", port)
              );

      vertx.deployVerticle(API.class.getName(), options, context.asyncAssertSuccess());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testMyApplication(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().getNow(port, "localhost", "/", response -> {
      response.handler(body -> {
        context.assertTrue(body.toString().contains("Simple REST API"));
        async.complete();
      });
    });
  }

  @Test
  public void testaddOne(TestContext testContext) {
    Async async = testContext.async();
    JsonObject testobj = new JsonObject().put("object", "100");
    ArbritraryObject testobj1 = new ArbritraryObject(testobj);
    final String json = Json.encodePrettily(testobj1.getObject());
    final String length = Integer.toString(json.length());
    vertx.createHttpClient().post(port, "localhost", "/api/objects")
    .putHeader("content-type", "application/json")
    .putHeader("content-length", length)
    .handler(response -> {
      testContext.assertEquals(response.statusCode(), 201);
      testContext.assertTrue(response.headers().get("content-type").contains("application/json"));
      response.bodyHandler(body -> {
        final String obj = Json.decodeValue(body.toString(), ArbritraryObject.class).getObject().toString();
        testContext.assertEquals(obj, "{object=100}");
        testContext.assertNotNull(body);
        async.complete();
      });
    })
    .write(json)
    .end();

  }

  @Test
  public void testgetMethods(TestContext testContext) {
    Async async = testContext.async();
    JsonObject testobj = new JsonObject().put("object", "100");
    ArbritraryObject testobj1 = new ArbritraryObject(testobj);
    final String json = Json.encodePrettily(testobj1.getObject());
    final String length = Integer.toString(json.length());

    vertx.createHttpClient().post(port, "localhost", "/api/objects")
    .putHeader("content-type", "application/json")
    .putHeader("content-length", length)
    .handler(response -> {
      testContext.assertEquals(response.statusCode(), 201);
      testContext.assertTrue(response.headers().get("content-type").contains("application/json"));
      response.bodyHandler(body -> {
        final String obj = Json.decodeValue(body.toString(), ArbritraryObject.class).getObject().toString();
        testContext.assertEquals(obj, "{object=100}");
        testContext.assertNotNull(body);
        async.complete();
      });
    })
    .write(json)
    .end();

    vertx.createHttpClient().get(port, "localhost", "/api/objects")
    .putHeader("content-type", "application/json")
    .putHeader("content-length", length)
    .handler(response -> {
      testContext.assertEquals(response.statusCode(), 200);
      testContext.assertTrue(response.headers().get("content-type").contains("application/json"));
      response.bodyHandler(body -> {
        testContext.assertNotNull(body);
        final String obj = Json.decodeValue(body.toString(), ArbritraryObject.class).getObject().toString();
        testContext.assertEquals(obj, "{object=100}");
        async.complete();
      });
    })
    .write(json)
    .end();
  }

  @Test
  public void testupdateOne(TestContext testContext) {
    Async async = testContext.async();
    JsonObject testobj = new JsonObject().put("object", "100");
    ArbritraryObject testobj1 = new ArbritraryObject(testobj);
    final String json = Json.encodePrettily(testobj1.getObject());
    final String length = Integer.toString(json.length());
    vertx.createHttpClient().put(port, "localhost", "/api/objects")
    .putHeader("content-type", "application/json")
    .putHeader("content-length", length)
    .handler(response -> {
      testContext.assertEquals(response.statusCode(), 404);
      async.complete();
    })
    .write(json)
    .end();
  }


  @Test
  public void testdeleteOne(TestContext testContext) {
    Async async = testContext.async();
    JsonObject testobj = new JsonObject().put("object", "100");
    ArbritraryObject testobj1 = new ArbritraryObject(testobj);
    final String json = Json.encodePrettily(testobj1.getObject());
    final String length = Integer.toString(json.length());
    vertx.createHttpClient().delete(port, "localhost", "/api/objects")
    .putHeader("content-type", "application/json")
    .putHeader("content-length", length)
    .handler(response -> {
      testContext.assertEquals(response.statusCode(), 404);
      async.complete();
    })
    .write(json)
    .end();

  }
}