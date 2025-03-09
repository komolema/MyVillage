Vagrant.configure("2") do |config|
  config.vm.box = "peru/windows-server-2019-standard-x64-eval"
  config.vm.communicator = "winrm"
  config.vm.guest = :windows
  config.vm.network "private_network", type: "dhcp"

  config.vm.provider "virtualbox" do |vb|
    vb.memory = "4096"
    vb.cpus = 2
  end

  # Install Jenkins agent, Java, and Gradle
  config.vm.provision "shell", path: "setup-agent.ps1"
end