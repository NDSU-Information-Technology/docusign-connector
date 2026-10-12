package edu.ndsu.eci.midpoint.docusign_connector.api;

import com.google.gson.annotations.SerializedName;

/** 
 * OAuth token
 */
public class Token {

  /** access token */
  @SerializedName("access_token")
  private String accessToken;
  
  /** expires, which will be one hour */
  @SerializedName("expires_in")
  private int expiresIn;

  /**
   * Get access token
   * @return access token
   */
  public String getAccessToken() {
    return accessToken;
  }

  /**
   * Set access token
   * @param accessToken access token
   */
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  /**
   * Get expires in
   * @return expires in
   */
  public int getExpiresIn() {
    return expiresIn;
  }

  /**
   * Set expires in
   * @param expiresIn expires in
   */
  public void setExpiresIn(int expiresIn) {
    this.expiresIn = expiresIn;
  }
  
  
}
