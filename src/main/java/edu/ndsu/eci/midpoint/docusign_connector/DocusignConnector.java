package edu.ndsu.eci.midpoint.docusign_connector;

import java.util.List;
import java.util.Set;

import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.AttributesAccessor;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.PoolableConnector;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.TestOp;
import org.identityconnectors.framework.spi.operations.UpdateOp;

import edu.ndsu.eci.midpoint.docusign_connector.api.UserInfo;

/**
 * Connector for Docusign.
 */
@ConnectorClass(displayNameKey = "docusign.connector.display", configurationClass = DocusignConfiguration.class)
public class DocusignConnector implements PoolableConnector, TestOp, SchemaOp, SearchOp<Filter>, DeleteOp, UpdateOp {

  private static final String FIRST_NAME = "first_name";
  private static final String LAST_NAME = "last_name";
  private static final String STATUS = "membership_status";
  private static final String DATE_CREATED = "membership_created_on";
  // really display name
  private static final String USER_NAME = "user_name";
  private DocusignConfiguration configuration;
  private DocusignConnection connection;

  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public void init(Configuration cfg) {
    this.configuration = (DocusignConfiguration) cfg;
    connection = new DocusignConnection(configuration);
    connection.checkToken();
  }

  @Override
  public void dispose() {
    // noop

  }

  @Override
  public Uid update(ObjectClass objectClass, Uid uid, Set<Attribute> replaceAttributes, OperationOptions options) {
    AttributesAccessor accessor = new AttributesAccessor(replaceAttributes);
    UserInfo info = connection.getActiveUserProfileById(uid.getUidValue());
    
    if (info == null) {
      throw new ConnectorException("Got null active user back to change");
    }
    
    connection.updateEmailAddress(uid.getUidValue(), accessor.getName().getNameValue(), info.getSiteId());
    return uid;
  }

  @Override
  public void delete(ObjectClass objectClass, Uid uid, OperationOptions options) {
    connection.closeUser(uid.getUidValue());
  }

  @Override
  public FilterTranslator<Filter> createFilterTranslator(ObjectClass objectClass, OperationOptions options) {
    return new FilterTranslator<Filter>() {
      public List<Filter> translate(Filter filter) {
        return CollectionUtil.newList(filter);
      }
    };
  }

  @Override
  public void executeQuery(ObjectClass objectClass, Filter query, ResultsHandler handler, OperationOptions options) {

    if (query instanceof EqualsFilter && ((EqualsFilter) query).getAttribute() instanceof Uid) {
      UserInfo user = connection.getActiveUserProfileById(((Uid)((EqualsFilter)query).getAttribute()).getUidValue());

      if (user == null) {
        return;
      }
      handler.handle(convertUserToConnectorObject(user));
      return;
    }

    List<UserInfo> users = connection.getAllUsers(true);
    
    for (UserInfo user : users) {
      handler.handle(convertUserToConnectorObject(user));
    }

  }

  @Override
  public Schema schema() {
    ObjectClassInfoBuilder objectClassBuilder = new ObjectClassInfoBuilder();
    objectClassBuilder.setType("docusignAccount");

    // effectively remote system id
    AttributeInfoBuilder uidAib = new AttributeInfoBuilder(Uid.NAME);
    // this is what all of the other methods use to interact with the remote system
    uidAib.setNativeName("id");
    uidAib.setType(String.class);
    uidAib.setRequired(true);
    uidAib.setCreateable(false);
    uidAib.setUpdateable(false);
    objectClassBuilder.addAttributeInfo(uidAib.build());

    // our local value to do correlations on
    AttributeInfoBuilder nameAib = new AttributeInfoBuilder(Name.NAME);
    nameAib.setNativeName("email");
    nameAib.setType(String.class);
    nameAib.setRequired(true);
    objectClassBuilder.addAttributeInfo(nameAib.build());

    objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.define(USER_NAME).setUpdateable(false).build());
    objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.define(FIRST_NAME).setUpdateable(false).build());
    objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.define(LAST_NAME).setUpdateable(false).build());
    objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.define(STATUS).setUpdateable(false).build());
    objectClassBuilder.addAttributeInfo(AttributeInfoBuilder.define(DATE_CREATED).setUpdateable(false).build());
    
    SchemaBuilder schemaBuilder = new SchemaBuilder(DocusignConnector.class);
    schemaBuilder.defineObjectClass(objectClassBuilder.build());
    return schemaBuilder.build();
  }

  @Override
  public void test() {
    UserInfo validate = connection.getUserProfile(configuration.getValidateUserAddress());
    
    if (validate == null) {
      throw new ConnectionFailedException("Got null self back");
    }
  }

  @Override
  public void checkAlive() {
    connection.checkToken();
  }

  private ConnectorObject convertUserToConnectorObject(UserInfo user) {
    return new ConnectorObjectBuilder()
        .setUid(user.getId())
        .setName(user.getEmail())
        .addAttribute(FIRST_NAME, user.getFirstName())
        .addAttribute(LAST_NAME, user.getLastName())
        .addAttribute(USER_NAME, user.getUserName())
        .addAttribute(STATUS, user.getMembershipStatus())
        .addAttribute(DATE_CREATED, user.getCreatedOn())
        .build();
  }


}
