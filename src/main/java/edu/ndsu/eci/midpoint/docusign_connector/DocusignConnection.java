/*
 * Copyright (c) 2026 North Dakota State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.ndsu.eci.midpoint.docusign_connector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;

import edu.ndsu.eci.midpoint.docusign_connector.api.GetUsersResponse;
import edu.ndsu.eci.midpoint.docusign_connector.api.Membership;
import edu.ndsu.eci.midpoint.docusign_connector.api.Token;
import edu.ndsu.eci.midpoint.docusign_connector.api.UserInfo;

/**
 * Service for interacting with DocuSign admin API
 * https://developers.docusign.com/docs/admin-api/reference/
 */
public class DocusignConnection {

  /** replace token every 45 minutes per their documentation */
  private static final long TOKEN_REPLACEMENT_IN_MILLISECONDS = 45 * 60 * 1000;
  
  /** default admin scopes */
  public static final String ADMIN_SCOPES = "user_read user_write";
  
  /** thread safe client */
  private CloseableHttpClient httpclient = HttpClients.createDefault();

  /** gson */
  private Gson gson = new Gson();
  
  /** when the current token expires */
  private long expiresIn;
  /** current token */
  private String token = null;

  /** configuration */
  private DocusignConfiguration config;

  /**
   * Constructor
   * @param config configuration
   */
  public DocusignConnection(DocusignConfiguration config) {
    this.config = config;
    if (config.getScopes() == null) {
      config.setScopes(ADMIN_SCOPES);
    }
  }
  
  /**
   * Get list of users in the system
   * @param activeOnly only get active users
   * @return list of users
   */
  public List<UserInfo> getAllUsers(boolean activeOnly) {
    GetUsersResponse userResp = getFirstPage(activeOnly);
    
    if (userResp == null) {
      // somehow this can happen, likely due to redirect
      throw new ConnectorIOException();
    }
    
    List<UserInfo> allUsers = new ArrayList<>(userResp.getUsers());
    
    while (!StringUtils.isBlank(userResp.getPaging().getNext())) {
            
      userResp = getNextPage(userResp.getPaging().getNext());
      allUsers.addAll(userResp.getUsers());
    }
    
    return allUsers;
  }
  
  /**
   * Get the first page of all users
   * @param activeOnly true to get only active users, false otherwise
   * @return first page
   */
  public GetUsersResponse getFirstPage(boolean activeOnly) {
    checkToken();
    URIBuilder builder;
    try {
      builder = new URIBuilder(config.getBaseUrl() + "/Management/v2/organizations/" + config.getOrgId() + "/users");
    } catch (URISyntaxException e) {
      // no idea how you could get a URI syntax error, but it is a config problem
      throw new ConfigurationException(e);
    }
    builder.addParameter("account_id", config.getAccountId());
    // TODO config take??
    builder.addParameter("take", "250");
    
    if (activeOnly) {
      builder.addParameter("membership_status", "active");
    }
    
    HttpGet get;
    try {
      get = new HttpGet(builder.build());
    } catch (URISyntaxException e) {
      // no idea how you could get a URI syntax error, but it is a config problem
      throw new ConfigurationException(e);
    }
    get.addHeader("Accept", "application/json");
    get.addHeader("Content-Type", "application/json");
    get.addHeader("Authorization", "Bearer " + token);

    CloseableHttpResponse resp = null;
    String body;
    try {
      resp = httpclient.execute(get);
      body = EntityUtils.toString(resp.getEntity());
    } catch (IOException | ParseException e) {
     throw new ConnectorIOException(e);
    } finally {
      closeQuietly(resp);
    }
    
    GetUsersResponse userResp = gson.fromJson(body, GetUsersResponse.class);
    return userResp;
  }
  
  /**
   * Don't care about IOExceptions when trying to close the response. 
   * If it fires, there isn't anything that can be done anyway.
   * @param response response to close
   */
  private void closeQuietly(CloseableHttpResponse response) {
    if (response == null) {
      return;
    }
    try {
      response.close();
    } catch (IOException e) {
      // just ignore
    }
  }
  
  /**
   * Get a next page of users
   * @param nextPage URL of next page
   * @return response 
   */
  public GetUsersResponse getNextPage(String nextPage) {
    checkToken();
    HttpGet get = new HttpGet(nextPage);
    get.addHeader("Accept", "application/json");
    get.addHeader("Content-Type", "application/json");
    get.addHeader("Authorization", "Bearer " + token);

    CloseableHttpResponse resp = null;
    String body;
    try {
      resp = httpclient.execute(get);
      body = EntityUtils.toString(resp.getEntity());
    } catch (IOException | ParseException e) {
      throw new ConnectorIOException(e);
    } finally {
      closeQuietly(resp);  
    }
    
    return gson.fromJson(body, GetUsersResponse.class);
  }
  
  /**
   * Get user by id
   * https://developers.docusign.com/docs/admin-api/reference/usermanagement/multiproductusermanagement/getuserdsprofile/
   * @param id id
   * @return user object, null if not found
   */
  public UserInfo getActiveUserProfileById(String id) {
    checkToken();
    URIBuilder builder;
    HttpGet get;
    try {
      builder = new URIBuilder(config.getBaseUrl() + "/Management/v2.1/organizations/" + config.getOrgId() + "/users/" + id +"/dsprofile");
      get = new HttpGet(builder.build());
    } catch (URISyntaxException e) {
      // this should never trigger, but if it does, this result makes sense
      throw new ConfigurationException(e);
    }

    get.addHeader("Accept", "application/json");
    get.addHeader("Content-Type", "application/json");
    get.addHeader("Authorization", "Bearer " + token);

    CloseableHttpResponse resp = null;
    String body;
    try {
      resp = httpclient.execute(get);
      body = EntityUtils.toString(resp.getEntity());
    } catch (IOException | ParseException e) {
      throw new ConnectorIOException(e);
    } finally {
      closeQuietly(resp);
    }

    GetUsersResponse users = gson.fromJson(body, GetUsersResponse.class);

    if (users.getUsers() == null) {
      return null;
    }
        
    for (UserInfo user : users.getUsers()) {
      if (StringUtils.equals(user.getUserStatus(), "active")) {
        setMailFromMembership(user);
        return user;
      }
    }
    return null;
  }
  
  /**
   * Set the user email address from the membership list
   * users active one if present. Needed because user object
   * doesn't always directly have email set.
   * @param user user
   */
  private void setMailFromMembership(UserInfo user) {
    if (user.getMemberships().isEmpty()) {
      return;
    }
    
    user.setEmail(user.getMemberships().get(0).getEmail());
    
    for (Membership membership : user.getMemberships()) {
      if (membership.getStatus().equals("active")) {
        user.setEmail(membership.getEmail());
      }
    }
    
  }

  /**
   * Get user profile. Needed to get their site_id for username changes.
   * Could return multiple values. This will return the one value that comes back,
   * or will pick the first unclosed value (or first if no unclosed).
   * @param email email to lookup
   * @return user profile
   */
  public UserInfo getUserProfile(String email) {
    checkToken();
    URIBuilder builder;
    HttpGet get;
    try {
      builder = new URIBuilder(config.getBaseUrl() + "/Management/v2/organizations/" + config.getOrgId() + "/users/profile");
      builder.addParameter("email", email);

      get = new HttpGet(builder.build());
    } catch (URISyntaxException e) {
      // this should never trigger, but if it does, this result makes sense
      throw new ConfigurationException(e);
    }
    
    get.addHeader("Accept", "application/json");
    get.addHeader("Content-Type", "application/json");
    get.addHeader("Authorization", "Bearer " + token);

    CloseableHttpResponse resp = null;
    String body;
    try {
      resp = httpclient.execute(get);
      body = EntityUtils.toString(resp.getEntity());
    } catch (IOException | ParseException e) {
      throw new ConnectorIOException(e);
    } finally {
      closeQuietly(resp);
    }

    GetUsersResponse users = gson.fromJson(body, GetUsersResponse.class);
    
    if (users.getUsers() == null) {
      return null;
    }
    
    if (users.getUsers().size() == 1) {
      UserInfo user = users.getUsers().get(0);
      user.setEmail(email);
      return user;
    }
    
    for (UserInfo user : users.getUsers()) {
      if (StringUtils.equals(user.getMembershipStatus(), "active")) {
        user.setEmail(email);
        return user;
      }
    }
    
    UserInfo user = users.getUsers().get(0);
    user.setEmail(email);
    return user;
    
  }
  
  
  /**
   * Update the email address for a user
   * @param id users's id
   * @param newAddress new address to change to
   * @param siteId site id
   * @return true if it worked, false otherwise
   */
  public boolean updateEmailAddress(String id, String newAddress, int siteId) {
    checkToken();
    HttpPost post = new HttpPost(config.getBaseUrl() + "/Management/v2/organizations/" + config.getOrgId() + "/users/email_addresses");
    post.addHeader("Accept", "application/json");
    post.addHeader("Content-Type", "application/json");
    post.addHeader("Authorization", "Bearer " + token);
    
    String body = String.format("""
        {
  "users": [ {
  "id": "%s",
    "email": "%s",
    "site_id": %s
  }
  
  ]

}
        """,id, newAddress, siteId);
    post.setEntity(new StringEntity(body));
    CloseableHttpResponse resp = null;
    String respBody;
    try {
      resp = httpclient.execute(post);
      respBody = EntityUtils.toString(resp.getEntity());
    } catch (IOException | ParseException e) {
      throw new ConnectorIOException(e);
    } finally  {
      closeQuietly(resp);
    }
    
    if (resp.getCode() != 200) {
      throw new ConnectorException(respBody);
    }
    return true;
    
  }
  
  /**
   * Close a user
   * @param userGuid user to close
   * @return true for success, false otherwise
   */
  public boolean closeUser(String userGuid) {
    checkToken();
    HttpDelete delete = new HttpDelete(String.format("%s/Management/v2/organizations/%s/users/%s/accounts", config.getBaseUrl(), config.getOrgId(), userGuid));
    delete.addHeader("Accept", "application/json");
    delete.addHeader("Content-Type", "application/json");
    delete.addHeader("Authorization", "Bearer " + token);
    String body = String.format("""
        {
          "accounts": [{
            "id": "%s"}]
        }
        """, config.getAccountId());
    delete.setEntity(new StringEntity(body));
    CloseableHttpResponse resp = null;
    String respBody;
    try {
      resp = httpclient.execute(delete);
      respBody = EntityUtils.toString(resp.getEntity());
    } catch (IOException | ParseException e) {
      throw new ConnectorIOException(e);
    } finally {
      closeQuietly(resp);
    }
    
    if (resp.getCode() != 200) {
      throw new ConnectorException(respBody);
    }
    return true;
  }

  /**
   * Check to see if the current token is good, update if necessary 
   */
  public synchronized void checkToken() {
    if(this.token == null || (System.currentTimeMillis() + TOKEN_REPLACEMENT_IN_MILLISECONDS) > this.expiresIn) {
      updateToken();
    }
  }

  /**
   * Get a new token
   */
  private void updateToken() {
    RSAPrivateKey privateKey = config.getPrivateKey();
    Algorithm algorithm = Algorithm.RSA256(null, privateKey);
    long now = System.currentTimeMillis();
    JWTCreator.Builder builder =
        JWT.create()
            .withIssuer(config.getIkey())
            .withAudience(config.getAuthServer())
            .withIssuedAt(new Date(now))
            .withClaim("scope", config.getScopes())
            .withExpiresAt(new Date(now + expiresIn * 1000));
    
    String signedJWT = builder.sign(algorithm);
    
    String target = "https://" + config.getAuthServer() + "/oauth/token";
    
    CloseableHttpClient httpclient = HttpClients.createDefault();
    
    HttpPost post = new HttpPost(target);
    
    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("assertion",signedJWT));
    params.add(new BasicNameValuePair("grant_type","urn:ietf:params:oauth:grant-type:jwt-bearer"));
    
    post.setEntity(new UrlEncodedFormEntity(params));
    
    CloseableHttpResponse response = null;
    String body;
    try {
      response = httpclient.execute(post);
      body = EntityUtils.toString(response.getEntity());
    } catch (IOException | ParseException e) {
      throw new ConnectorIOException(e);
    } finally {
      closeQuietly(response);
    }
   
    Token respToken = gson.fromJson(body, Token.class);

    if (respToken.getAccessToken() == null) {
      throw new ConnectionFailedException(body);
    }
    
    token = respToken.getAccessToken();
    expiresIn = System.currentTimeMillis() + (respToken.getExpiresIn() * 1000);
  }
}
