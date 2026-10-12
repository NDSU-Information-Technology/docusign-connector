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
