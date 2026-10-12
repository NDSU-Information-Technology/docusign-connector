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
