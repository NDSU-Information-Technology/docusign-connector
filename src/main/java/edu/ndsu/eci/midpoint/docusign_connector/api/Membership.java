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

/**
 * Membership object under a user
 * contains email when user doesn't.
 */
public class Membership {

  /** status */
  private String status;
  
  /** email */
  private String email;

  /**
   * Get status
   * @return status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Set status
   * @param status status
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Get email
   * @return email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Set email
   * @param email email
   */
  public void setEmail(String email) {
    this.email = email;
  }
  
  
}
