package org.sirix.access;

import org.sirix.api.Database;
import org.sirix.api.ResourceManager;
import org.sirix.node.SirixDeweyID;
import org.sirix.node.delegates.NodeDelegate;
import org.sirix.node.delegates.StructNodeDelegate;
import org.sirix.node.interfaces.Node;
import org.sirix.node.json.JsonDocumentRootNode;
import org.sirix.node.xml.XmlDocumentRootNode;
import org.sirix.settings.Fixed;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DatabaseType {
  XML("xml") {
    @SuppressWarnings("unchecked")
    @Override
    public <R extends ResourceManager<?, ?>> Database<R> createDatabase(
            DatabaseConfiguration dbConfig, User user) {
      return (Database<R>) Databases.MANAGER.xmlDatabaseFactory().createDatabase(dbConfig, user);
    }

    @Override
    public Node getDocumentNode(SirixDeweyID id) {
      final NodeDelegate nodeDel = new NodeDelegate(Fixed.DOCUMENT_NODE_KEY.getStandardProperty(),
          Fixed.NULL_NODE_KEY.getStandardProperty(), null, null, 0, id);
      final StructNodeDelegate structDel = new StructNodeDelegate(nodeDel, Fixed.NULL_NODE_KEY.getStandardProperty(),
          Fixed.NULL_NODE_KEY.getStandardProperty(), Fixed.NULL_NODE_KEY.getStandardProperty(), 0, 0);

      return new XmlDocumentRootNode(nodeDel, structDel);
    }
  },

  JSON("json") {
    @SuppressWarnings("unchecked")
    @Override
    public <R extends ResourceManager<?, ?>> Database<R> createDatabase(
            DatabaseConfiguration dbConfig, User user) {
      return (Database<R>) Databases.MANAGER.jsonDatabaseFactory().createDatabase(dbConfig, user);
    }

    @Override
    public Node getDocumentNode(SirixDeweyID id) {
      final NodeDelegate nodeDel = new NodeDelegate(Fixed.DOCUMENT_NODE_KEY.getStandardProperty(),
          Fixed.NULL_NODE_KEY.getStandardProperty(), null, null, 0, id);
      final StructNodeDelegate structDel = new StructNodeDelegate(nodeDel, Fixed.NULL_NODE_KEY.getStandardProperty(),
          Fixed.NULL_NODE_KEY.getStandardProperty(), Fixed.NULL_NODE_KEY.getStandardProperty(), Fixed.NULL_NODE_KEY.getStandardProperty(), 0, 0);

      return new JsonDocumentRootNode(nodeDel, structDel);
    }
  };

  private String stringType;

  DatabaseType(String stringType) {
    this.stringType = stringType;
  }

  public String getStringType() {
    return stringType;
  }

  private static final Map<String, DatabaseType> stringToEnum =
      Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

  public static Optional<DatabaseType> fromString(String symbol) {
    return Optional.ofNullable(stringToEnum.get(symbol));
  }

  public abstract <R extends ResourceManager<?, ?>> Database<R> createDatabase(
          DatabaseConfiguration dbConfig, User user);

  public abstract Node getDocumentNode(SirixDeweyID id);
}
