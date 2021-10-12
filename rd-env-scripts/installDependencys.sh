#! /bin/bash

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
