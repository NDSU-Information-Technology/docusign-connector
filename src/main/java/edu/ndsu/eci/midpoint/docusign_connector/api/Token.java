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
