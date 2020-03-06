package restAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * This class contains the API's for the
 * POST, GET, PUT, and DELETE methods,
 * error handling and finding host name
 * @author Vishik Bhalla
 *
 */

public class API extends AbstractVerticle {

  final int PORT = 8000;

  private InetAddress inetAddress = null;

  @Override
  // Starts the program
  public void start(Future<Void> fut) {
    Router router = Router.router(vertx);
    router.route("/").handler(routingContext -> {
      HttpServerResponse response = routingContext.response();
      response
      .putHeader("content-type", "text/html")
      .end("Simple REST API");
    });

    // Serve static resources from the /assets directory
    router.route().handler(BodyHandler.create()); 
    router.post("/api/objects").handler(this::addOne);
    router.get("/api/objects/:id").handler(this::getOne);
    router.get("/api/objects/*").handler(this::getAll);
    router.put("/api/objects/:id").handler(this::updateOne);
    router.delete("/api/objects/:id").handler(this::deleteOne);

    vertx
    .createHttpServer()
    .requestHandler(router::accept)
    .listen(
        config().getInteger("http.port", PORT),
        result -> {
          if (result.succeeded()) {
            fut.complete();
          } else {
            fut.fail(result.cause());
          }
        }
        );
  }

  // Posts a given JsonObject
  private void addOne(RoutingContext routingContext) {
    try{
      JsonObject s = routingContext.getBodyAsJson();
      ArbritraryObject obj = new ArbritraryObject(s);
      ArbritraryObject.objmap.put(obj.getUid().toString(), obj);
      routingContext.response()
      .setStatusCode(201)
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(Json.encodePrettily(obj));
    } catch(Exception e) {
      error(routingContext, "POST", "/api/objects", "Not a JsonObject");
    }
  }

  // Gets the ArbritraryObject with given uid
  private void getOne(RoutingContext routingContext) { 
    String id = routingContext.request().getParam("id");
    if(!ArbritraryObject.objmap.containsKey(id) || id == null) {
      error(routingContext, "GET", "/api/objects/" + id, 
          "JsonObject with given uid does not exist");
    }
    else {
      routingContext.response()
      .setStatusCode(201)
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(Json.encodePrettily(ArbritraryObject.objmap.get(id)));
    }
  }

  // Gets all ArbritraryObjects as urls
  private void getAll(RoutingContext routingContext) {
    JsonArray arr = new JsonArray();
    ArbritraryObject.objmap.forEach((k, v) -> arr.add
        (new JsonObject().put("url", actualHostName() + ":" + PORT + "/api/objects/" + k)));
    routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
    .end(arr.encodePrettily());
  }

  // Updates the ArbritraryObject at given uid
  private void updateOne(RoutingContext routingContext) {
    try {
      String id = routingContext.request().getParam("id");
      if(!ArbritraryObject.objmap.containsKey(id) || id == null) {
        error(routingContext, "PUT", "/api/objects/" + id, "Id does not exist");

      }
      else {
        JsonObject s = routingContext.getBodyAsJson();
        ArbritraryObject obj = ArbritraryObject.objmap.get(id);
        obj.setObject(s);
        ArbritraryObject.objmap.replace(id, obj);
        routingContext.response()
        .setStatusCode(201)
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(obj));
      }
    } catch (Exception e) {
      error(routingContext, "PUT", "/api/objects/", "Not a JsonObject");
    }
  }

  // Deletes an ArbitraryObject with given uid
  private void deleteOne(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    if (!ArbritraryObject.objmap.containsKey(id) || id == null) {
      error(routingContext, "DELETE", "/api/objects/" + id, "Id does not exist");
    } else {
      ArbritraryObject.objmap.remove(id);
    }
    routingContext.response().setStatusCode(204).end();
  }

  // Defines the error that occurred
  private void error(RoutingContext routingContext, String verb, String url, String message) {   
    JsonObject obj = new JsonObject();
    obj.put("verb", verb);
    obj.put("url", actualHostName() + ":" + PORT + url);
    obj.put("message", message);
    routingContext.response()
    .setStatusCode(400)
    .putHeader("content-type", "application/json; charset=utf-8")
    .end(Json.encodePrettily(obj));
  }

  // Gets the host name
  private String getHostName() throws IOException{
    try {
      Process process = Runtime.getRuntime().exec("ec2-metadata -p");

      StringBuilder output = new StringBuilder();

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line);
      }

      String[] arr = output.toString().split(" ");

      int exitVal = process.waitFor();
      if (exitVal == 0) {
        return arr[1];
      } else {
        return "localhost";
      }
    } catch (InterruptedException e) {
    }

    return "localhost";
  }

  // Returns the host name and returns localhost if there is none
  private String actualHostName() {
    String localhost = "localhost";
    try {
      localhost = getHostName();
    }
    catch (IOException e1) {
      try {
        inetAddress = InetAddress.getLocalHost();
        localhost = inetAddress.getHostName();
      }
      catch (UnknownHostException e) {
      }
    }
    return localhost;
  }
}
