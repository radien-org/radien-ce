#!/bin/sh
#
# Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
# <p>
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# <p>
# http://www.apache.org/licenses/LICENSE-2.0
# <p>
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

launchctl setenv KEYCLOAK_ADMIN "adminsantana"
launchctl setenv KEYCLOAK_PASSWORD "NM7uR6ybEx3eu3J"
launchctl setenv KEYCLOAK_CLIENT_ID "admin-cli"
launchctl setenv KEYCLOAK_IDP_URL "https://idp-int.radien.io"
launchctl setenv KEYCLOAK_TOKEN_PATH "/auth/realms/master/protocol/openid-connect/token"
launchctl setenv KEYCLOAK_USER_PATH "/auth/admin/realms/radien/users"

launchctl setenv REALMS_TOKEN_PATH "/auth/realms/radien/protocol/openid-connect/token"
launchctl setenv SCRIPT_CLIENT_ID "client_id"
launchctl setenv SCRIPT_CLIENT_ID_VALUE "radien"
launchctl setenv SCRIPT_CLIENT_SECRET "client_secret"
launchctl setenv SCRIPT_CLIENT_SECRET_VALUE "d8f67579-1a33-47be-ad6f-aef38269ed12"
launchctl setenv SCRIPT_REDIRECT_URL "redirect_uri"
launchctl setenv SCRIPT_REDIRECT_URL_VALUE "https://localhost:8443/web/login"
launchctl setenv SCRIPT_GRANT_TYPE "grant_type"
launchctl setenv SCRIPT_GRANT_TYPE_VALUE "password"
launchctl setenv SCRIPT_USERNAME "username"
launchctl setenv SCRIPT_USERNAME_VALUE "n.santana-username"
launchctl setenv SCRIPT_PASSWORD "password"
launchctl setenv SCRIPT_PASSWORD_VALUE "batata"

launchctl setenv AUTHENTICATION_CLIENT_ID "userManagement"
launchctl setenv AUTHENTICATION_CLIENT_SECRET "48cbf1a9-f383-49bc-8b84-25bd3130c716"
launchctl setenv AUTHENTICATION_CLIENT_SCOPE "microprofile-jwt"
launchctl setenv APPLICATION_AUTHENTICATION_CLIENT_SECRET "9a4b90bf-d42f-42e5-8c5d-2de54f895b4e"
launchctl setenv value "lookup value"
