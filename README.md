<!--
Copyright (c) 2026 North Dakota State University

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
# Docusign connector for ConnID and midPoint

## Overview

Connector for Docusign using ConnId. This is provided as-is.

Copyright 2026 North Dakota State University and released under the Apache License, Version 2.0.

Used by NDSU to manage Docusign. For support see a midPoint support organization like Unicon. Pull requests welcome.

Deployed on midPoint 4.10. 

## Assumptions

Docusign service is offered with provision on demand as the way to have accounts created.

This is setup to operate in the way required by NDSU. If that doesn't work for your institution, pull requests to add functionality are welcome. 

No rate limiting from Docusign, but you are recommended to use shadow caching to speed operations up.

## Limitations

This connector will not create users, but it can discover active users. Docusign is frequently deployed as provision on demand, so for a person to create their Docusign account, they can just login.

Purpose of this connector is to update email addresses as they change, and to close accounts when people separate. It does not manage the user's actual name, as that is all changeable by the user in Docusign, and there is no way to lock those values down. It will not unclose users.

Delete action will close the user's account. This retains all of their info and history (depending on your retention settings). If they login again with provision on demand, they will get a new account.

# Setup

Needs the user_read user_write scopes granted as a connected app at the organization level.

Needs to be setup in your production and demo environments separately.

## Configuration

<table>
  <tr>
    <th>Key</th>
    <th>Value</th>
  </tr>
  <tr>
    <td>accountId</td>
    <td>API Account Id, needs to be OID shaped, not the number. Can be found under <strong>Apps and Keys</strong> under Integrations.</td>
  </tr>
  <tr>
    <td>authServer</td>
    <td>Likely <strong>account.docusign.com</strong> for production and <strong>account-d.docusign.com</strong> for demo.</td>
  </tr>
  <tr>
    <td>baseUrl</td>
    <td>Base URL for API calls. Likely <strong>https://api.docusign.net</strong> for production and <strong>https://api-d.docusign.net</strong> for demo.</td>
  </tr>
  <tr>
    <td>ikey</td>
    <td>ikey for API access</td>
  </tr>
  <tr>
    <td>orgId</td>
    <td>Org ID, you can get this from the URL for your Organization admin page, or from Account Profile</td>
  </tr>
  <tr>
    <td>privateKeyFilePath</td>
    <td>Path on the file system to the private key for API access</td>
  </tr>
  <tr>
    <td>scopes</td>
    <td>Scopes to include in OIDC request, likely <strong></strong></td>
  </tr>
  <tr>
    <td>validateUserAddress</td>
    <td>A user address in the system to retrieve for validation purposes.</td>
  </tr>
</table>

## Schema
<table>
  <tr>
    <th>Attribute</th>
    <th>Type</th>
    <th>Comment</th>
  </tr>
  <tr>
    <td>first_name</td>
    <td>String</td>
    <td>First name. Read only.</td>
  </tr>
  <tr>
    <td>last_name</td>
    <td>String</td>
    <td>Last name. Read only.</td>
  </tr>
  <tr>
    <td>membership_status</td>
    <td>String</td>
    <td>Status of membership. Read only.</td>
  </tr>
  <tr>
    <td>id</td>
    <td>String</td>
    <td>__UID__</td>
  </tr>
  <tr>
    <td>email</td>
    <td>String</td>
    <td>Email, __NAME__</td>
  </tr>
  <tr>
    <td>membership_created_on</td>
    <td>String</td>
    <td>When created. Read only.</td>
  </tr>
  <tr>
    <td>user_name</td>
    <td>String</td>
    <td>Display name in platform. Read only.</td>
  </tr>  
</table>

