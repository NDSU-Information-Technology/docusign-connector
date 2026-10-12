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

import com.google.gson.annotations.SerializedName;

/**
 * https://developers.docusign.com/docs/admin-api/reference/usermanagement/users/getusers/
 *
 */
public class UserInfo {

  /** user unique id */
  private String id;
  
  /** first last */
  @SerializedName("user_name")
  private String userName;
  
  /** first */
  @SerializedName("first_name")
  private String firstName;
  
  /** last */
  @SerializedName("last_name")
  private String lastName;
  
  /** status: activation_required, activation_sent, active, closed, disabled */
  // TODO turn this into an enum
  @SerializedName("membership_status")
  private String membershipStatus;
  
  /** email address */
  private String email;
  
  /** created on */
  @SerializedName("membership_created_on")
  private String membershipCreatedOn;
  
  /** created on */
  @SerializedName("created_on")
  private String createdOn;
  
  /** membership id */
  @SerializedName("membership_id")
  private String membershipId;
  
  /** site id, probably 1 */
  @SerializedName("site_id")
  private int siteId;
  
  /** memberships */
  private List<Membership> memberships;
  
  /** user status, requried on some paths instead of membership status */
  @SerializedName("user_status")
  private String userStatus;

  /**
   * Get id
   * @return id
   */
  public String getId() {
    return id;
  }

  /** 
   * Set id
   * @param id id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Get display name
   * @return display name
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Set display name
   * @param userName display name
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /** 
   * First name
   * @return first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Set first name
   * @param firstName first name
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Last name
   * @return last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Set last name
   * @param lastName last name
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Membership status
   * @return status
   */
  public String getMembershipStatus() {
    return membershipStatus;
  }

  /**
   * Set membership status
   * @param membershipStatus status
   */
  public void setMembershipStatus(String membershipStatus) {
    this.membershipStatus = membershipStatus;
  }

  /**
   * Email
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

  /**
   * Created on
   * @return created on
   */
  public String getCreatedOn() {
    if (createdOn != null) {
      return createdOn;
    }
    return membershipCreatedOn;
  }

  /**
   * Set created on
   * @param createdOn created on
   */
  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * Membership id
   * @return membership id
   */
  public String getMembershipId() {
    return membershipId;
  }

  /**
   * Set membership id
   * @param membershipId membership id
   */
  public void setMembershipId(String membershipId) {
    this.membershipId = membershipId;
  }

  /**
   * Site id
   * @return site id, probably 1
   */
  public int getSiteId() {
    return siteId;
  }

  /**
   * Set site id
   * @param siteId site id
   */
  public void setSiteId(int siteId) {
    this.siteId = siteId;
  }

  /**
   * Get memberships
   * @return list of membership
   */
  public List<Membership> getMemberships() {
    return memberships;
  }

  /**
   * Set memberships
   * @param memberships list of memberships
   */
  public void setMemberships(List<Membership> memberships) {
    this.memberships = memberships;
  }

  /**
   * Get user status.
   * Use this on some get paths instead of membershipStatus
   * @return status
   */
  public String getUserStatus() {
    return userStatus;
  }

  /**
   * User status
   * @param userStatus user status
   */
  public void setUserStatus(String userStatus) {
    this.userStatus = userStatus;
  }
  
  
}
