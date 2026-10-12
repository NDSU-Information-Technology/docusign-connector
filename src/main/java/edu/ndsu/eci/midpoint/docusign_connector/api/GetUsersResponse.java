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

import java.util.List;

/**
 * https://developers.docusign.com/docs/admin-api/reference/usermanagement/users/getusers/
 *
 */
public class GetUsersResponse {

  /** list of users */
  private List<UserInfo> users;
  
  /** paging info */
  private PagingProperties paging;

  /**
   * List of users
   * @return users
   */
  public List<UserInfo> getUsers() {
    return users;
  }

  /**
   * Set users
   * @param users users
   */
  public void setUsers(List<UserInfo> users) {
    this.users = users;
  }

  /**
   * Get page properties
   * @return paging
   */
  public PagingProperties getPaging() {
    return paging;
  }

  /**
   * Set page properties
   * @param paging paging
   */
  public void setPaging(PagingProperties paging) {
    this.paging = paging;
  }
  
  
}
