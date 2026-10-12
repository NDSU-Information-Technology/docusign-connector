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
 * Paging properties wrapper
 */
public class PagingProperties {

  /** size */
  @SerializedName("result_set_size")
  private int resultSize;
  
  /** start */
  @SerializedName("result_set_start_position")
  private int resultStart;
  
  /** end */
  @SerializedName("result_set_end_position")
  private int resultEnd;
  
  /** total */
  @SerializedName("total_set_size")
  private int totalSize;
  
  /** next page */
  private String next;
  
  /** previous page */
  private String previous;

  /**
   * Result size
   * @return size
   */
  public int getResultSize() {
    return resultSize;
  }

  /** 
   * Set result size
   * @param resultSize size
   */
  public void setResultSize(int resultSize) {
    this.resultSize = resultSize;
  }

  /**
   * Result start
   * @return start
   */
  public int getResultStart() {
    return resultStart;
  }

  /**
   * Set result start
   * @param resultStart start
   */
  public void setResultStart(int resultStart) {
    this.resultStart = resultStart;
  }

  /**
   * Result end
   * @return end
   */
  public int getResultEnd() {
    return resultEnd;
  }

  /**
   * Set result end
   * @param resultEnd end
   */
  public void setResultEnd(int resultEnd) {
    this.resultEnd = resultEnd;
  }

  /**
   * Total size
   * @return total
   */
  public int getTotalSize() {
    return totalSize;
  }

  /**
   * Set total size
   * @param totalSize total size
   */
  public void setTotalSize(int totalSize) {
    this.totalSize = totalSize;
  }

  /**
   * Next page
   * @return next page
   */
  public String getNext() {
    return next;
  }

  /**
   * Set next page
   * @param next next page
   */
  public void setNext(String next) {
    this.next = next;
  }

  /**
   * Previous page
   * @return previous page
   */
  public String getPrevious() {
    return previous;
  }

  /**
   * Set previous page
   * @param previous previous page
   */
  public void setPrevious(String previous) {
    this.previous = previous;
  }
  
  
}
