# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

# ENV['VAGRANT_DEFAULT_PROVIDER'] ||= 'docker'
# ENV['VAGRANT_NO_PARALLEL'] ||= 'true'

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  config.vm.box = "phusion/ubuntu-14.04-amd64"
  config.vm.box_url = "https://oss-binaries.phusionpassenger.com/vagrant/boxes/latest/ubuntu-14.04-amd64-vbox.box"

  config.vm.provision "shell", path: "env/provision.sh"

  # expose ldap port
  config.vm.network :forwarded_port, host: 1389, guest: 1389
  # expose cas port
  config.vm.network :forwarded_port, host: 8443, guest: 8443

# part below will work with vagrant >= 1.7

#  config.vm.define 'ldap' do |ldap|
#    ldap.vm.provider 'docker' do |d|
#      d.build_dir = 'env/docker/ldap'
#      d.build_args = ['-t', 'scmmu/ldap']
#      d.name = 'ldap'
#      d.ports = ['1389:389']
#    end

#    ldap.vm.network :forwarded_port, host: 1389, guest: 1389
#  end

#  config.vm.define 'cas' do |cas|
#    cas.vm.provider 'docker' do |d|
#      d.build_dir = 'env/docker/cas'
#      d.build_args = ['-t','scmmu/cas']
#      d.name = 'cas'
#      d.ports = ['8443:8443']
#      d.link 'ldap:ldap'
#    end

#    cas.vm.network :forwarded_port, host: 8443, guest: 8443
#  end

end
