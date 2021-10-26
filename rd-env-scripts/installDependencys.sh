#! /bin/bash

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

apt-get update;

#Add Corretto Repository 
wget -O- https://apt.corretto.aws/corretto.key | sudo apt-key add - sudo add-apt-repository 'deb https://apt.corretto.aws stable main'
echo "install Maven"
apt-get install -y java-1.8.0-amazon-corretto-jdk maven

#TomEE Plus install
echo "install TomEE"
useradd -s /sbin/nologin tomcat
wget https://www.apache.org/dyn/closer.cgi/tomee/tomee-8.0.6/apache-tomee-8.0.6-plus.tar.gz
mkdir /opt/tomee
mv apache-tomee-plus-8.0.6/ /opt/tomee/
chown -R tomcat:tomcat /opt/tomee/
ln -s /opt/tomee/apache-tomee-plus-8.0.6/ /opt/tomee/latest
cp /tomee.service /usr/lib/systemd/system/tomee.service
echo "Enabling/starting TomEE service"
systemctl daemon-reload
systemctl enable tomee.service
systemctl start tomee.service
echo "Add TomEE to PATH"
echo 'export PATH=$PATH:$HOME/.local/bin:$HOME/bin'
echo 'export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$LIB_PATH'
echo 'export CATALINA_HOME=/opt/tomee/apache-tomee-plus-8.0.6'

source ~/.bash_profile
