package edu.ndsu.eci.midpoint.docusign_connector;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class DocusignConfiguration extends AbstractConfiguration {

  private static final Log LOG = Log.getLog(DocusignConfiguration.class);

  private String accountId;
  private String authServer;
  private String baseUrl;
  private String ikey;
  private String orgId;
  private String privateKeyFilePath;
  /** private key */
  private RSAPrivateKey privateKey;
  private String scopes;
  
  /** validate user address, also gets site id */
  private String validateUserAddress;

  public String getAccountId() {
    return accountId;
  }

  @ConfigurationProperty(displayMessageKey = "docusign.config.api.accountId")
  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getIkey() {
    return ikey;
  }

  @ConfigurationProperty(displayMessageKey = "docusign.config.api.iKey")
  public void setIkey(String ikey) {
    this.ikey = ikey;
  }

  public String getOrgId() {
    return orgId;
  }

  @ConfigurationProperty(displayMessageKey = "docusign.config.api.orgId")
  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public String getPrivateKeyFilePath() {
    return privateKeyFilePath;
  }

  @ConfigurationProperty(displayMessageKey = "docusign.config.api.privateKeyFilePath")
  public void setPrivateKeyFilePath(String privateKeyFilePath) {
    this.privateKeyFilePath = privateKeyFilePath;
  }
  
  /**
   * User to use for validation
   * @return user email address
   */
  public String getValidateUserAddress() {
    return validateUserAddress;
  }

  /**
   * User email address to use for validation
   * @param validateUserAddress address
   */
  @ConfigurationProperty(displayMessageKey = "docusign.config.api.validateEmail")
  public void setValidateUserAddress(String validateUserAddress) {
    this.validateUserAddress = validateUserAddress;
  }
  
  public String getScopes() {
    return scopes;
  }
  
  @ConfigurationProperty(displayMessageKey = "docusign.config.api.scopes")
  public void setScopes(String scopes) {
    this.scopes = scopes;
  }

  @Override
  public void validate() {
    
    if (accountId == null) {
      LOG.error("Account ID can't be blank.");
      throw new ConfigurationException("Account ID can't be blank.");
    }
    
    if (authServer == null) {
      LOG.error("authServer can't be blank.");
      throw new ConfigurationException("authServer can't be blank.");
    }
    
    if (baseUrl == null) {
      LOG.error("baseUrl can't be blank.");
      throw new ConfigurationException("baseUrl can't be blank.");
    }
    
    if (ikey == null) {
      LOG.error("IKey can't be blank.");
      throw new ConfigurationException("IKey can't be blank.");
    }
    
    if (orgId == null) {
      LOG.error("Org ID can't be blank.");
      throw new ConfigurationException("Org ID can't be blank.");
    }
    
    if (getValidateUserAddress() == null) {
      LOG.error("Validate User Email Address can't be blank.");
      throw new ConfigurationException("Validate User Email Address can't be blank.");
    }
    
    if (privateKeyFilePath == null) {
      LOG.error("Private Key Path can't be blank.");
      throw new ConfigurationException("Private Key Path can't be blank.");
    }

    if (getPrivateKey() == null) {
      LOG.error("Private key has to be valid.");
      throw new ConfigurationException("Private key has to be valid.");
    }
    
  }
  
  /**
   * Get auth server
   * @return auth server
   */
  public String getAuthServer() {
    return authServer;
  }
  
  @ConfigurationProperty(displayMessageKey = "docusign.config.api.authServer")
  public void setAuthServer(String authServer) {
    this.authServer = authServer;
  }
  
  /**
   * Get base url
   * @return base url
   */
  public String getBaseUrl() {
    return baseUrl;
  }
  
  @ConfigurationProperty(displayMessageKey = "docusign.config.api.baseUrl")
  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  /**
   * Get private key
   * @return read in private key
   */
  public synchronized RSAPrivateKey getPrivateKey() {
    if (privateKey == null) {
      try {
        byte[] privateKeyBytes = FileUtils.readFileToByteArray(new File(privateKeyFilePath));
        PemReader reader = new PemReader(new StringReader(new String(privateKeyBytes)));
        try {
          PemObject pemObject = reader.readPemObject();
          byte[] bytes = pemObject.getContent();
          RSAPrivateKey privateKey = null;
          try {
            Security.addProvider(new BouncyCastleProvider());
            KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            privateKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
          } catch (NoSuchAlgorithmException e) {
            LOG.error("Could not reconstruct the private key, the given algorithm could not be found.");
            throw new ConfigurationException("Could not reconstruct the private key, the given algorithm could not be found.", e);
          } catch (InvalidKeySpecException e) {
            LOG.error("Could not reconstruct the private key.");
            throw new ConfigurationException("Could not reconstruct the private key.", e);
          } catch (NoSuchProviderException e) {
            LOG.error("Could not reconstruct the private key, invalid provider.");
            throw new ConfigurationException("Could not reconstruct the private key, invalid provider.", e);
          }

          return privateKey;
        } finally {
          reader.close();
        }
        
      } catch (IOException e) {
        throw new ConfigurationException(e);
      }
    }
    return privateKey;
  }
}
