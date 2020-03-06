package restAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class represents an arbitrary JsonObject 
 * with a specific UID
 * @author Vishik Bhalla
 *
 */
public class ArbritraryObject {
  public static Map<String, ArbritraryObject> objmap = new HashMap<>();

  private UUID uid;
  private Object jsonObject;

  public ArbritraryObject(Object jsonObject) {
    this.uid = UUID.randomUUID();
    this.jsonObject = jsonObject;
  }

  public ArbritraryObject() {
  }

  public Object getObject() {
    return jsonObject;
  }

  public void setObject(Object obj) {
    this.jsonObject = obj;
  }

  public UUID getUid() {
    return uid;
  }

  public void setUid(UUID uid) {
    this.uid = uid;
  }
}