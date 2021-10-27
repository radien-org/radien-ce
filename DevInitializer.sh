#!/bin/sh

# Copyright (c) 2021-present radien GmbH. All rights reserved.
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
# limitations under the License.Ku

#to run this shell script you need jq (is a lightweight and flexible command-line JSON processor)
# you can find jq at https://github.com/stedolan/jq
# its available in package manager like brew (https://brew.sh/)
if ! command -v jq &> /dev/null
then
    echo "to run this shell script you need jq (is a lightweight and flexible command-line JSON processor)"
    echo "you can find jq at https://github.com/stedolan/jq"
    echo "its available in package manager like brew (https://brew.sh/)"
    echo "so if you have brew installed you can install jq with 'brew install jq'"
    exit
fi

echo '/-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\'
echo '|/---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\|'
echo '|| $$$$$$$\   $$$$$$\  $$$$$$$\  $$$$$$\ $$$$$$$$\ $$\   $$\       $$$$$$$\  $$$$$$$$\ $$\    $$\       $$$$$$\ $$\   $$\ $$$$$$\$$$$$$$$\$$$$$$\  $$$$$$\  $$\      $$$$$$\$$$$$$$$\ $$$$$$$$\ $$$$$$$\   ||'
echo '|| $$  __$$\ $$  __$$\ $$  __$$\ \_$$  _|$$  _____|$$$\  $$ |      $$  __$$\ $$  _____|$$ |   $$ |      \_$$  _|$$$\  $$ |\_$$  _\__$$  __\_$$  _|$$  __$$\ $$ |     \_$$  _\____$$  |$$  _____|$$  __$$\  ||'
echo '|| $$ |  $$ |$$ /  $$ |$$ |  $$ |  $$ |  $$ |      $$$$\ $$ |      $$ |  $$ |$$ |      $$ |   $$ |        $$ |  $$$$\ $$ |  $$ |    $$ |    $$ |  $$ /  $$ |$$ |       $$ |     $$  / $$ |      $$ |  $$ | ||'
echo '|| $$$$$$$  |$$$$$$$$ |$$ |  $$ |  $$ |  $$$$$\    $$ $$\$$ |      $$ |  $$ |$$$$$\    \$$\  $$  |        $$ |  $$ $$\$$ |  $$ |    $$ |    $$ |  $$$$$$$$ |$$ |       $$ |    $$  /  $$$$$\    $$$$$$$  | ||'
echo '|| $$  __$$ |$$  __$$ |$$ |  $$ |  $$ |  $$  __|   $$ \$$$$ |      $$ |  $$ |$$  __|    \$$\$$  /         $$ |  $$ \$$$$ |  $$ |    $$ |    $$ |  $$  __$$ |$$ |       $$ |   $$  /   $$  __|   $$  __$$ | ||'
echo '|| $$ |  $$ |$$ |  $$ |$$ |  $$ |  $$ |  $$ |      $$ |\$$$ |      $$ |  $$ |$$ |        \$$$  /          $$ |  $$ |\$$$ |  $$ |    $$ |    $$ |  $$ |  $$ |$$ |       $$ |  $$  /    $$ |      $$ |  $$ | ||'
echo '|| $$ |  $$ |$$ |  $$ |$$$$$$$  |$$$$$$\ $$$$$$$$\ $$ | \$$ |      $$$$$$$  |$$$$$$$$\    \$  /         $$$$$$\ $$ | \$$ |$$$$$$\   $$ |  $$$$$$\ $$ |  $$ |$$$$$$$$\$$$$$$\$$$$$$$$\ $$$$$$$$\ $$ |  $$ | ||'
echo '|| \__|  \__|\__|  \__|\_______/ \______|\________|\__|  \__|      \_______/ \________|    \_/          \______|\__|  \__|\______|  \__|  \______|\__|  \__|\________\______\________|\________|\__|  \__| ||'
echo '|\---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------/|'
echo '| Author: Bruno Gama - Rethink                                                                                                                                                                              |'
echo '| Author: Nuno Santana - Rethink                                                                                                                                                                            |'
echo '\-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------/'

echo "--> Going to initialize all the minimum requirements for development purposes."
sh ./rd-ms-tenantmanagement/TenantDevInitializer.sh
sh ./rd-ms-permissionmanagement/ActionDevInitializer.sh
sh ./rd-ms-permissionmanagement/ResourceDevInitializer.sh
sh ./rd-ms-permissionmanagement/PermissionDevInitializer.sh
sh ./rd-ms-rolemanagement/RoleDevInitializer.sh
sh ./rd-ms-rolemanagement/TenantRoleAssociationDevInitializer.sh
echo "--> All done! Ready to start development!"