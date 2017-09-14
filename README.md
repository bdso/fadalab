# Cd folder
cd /Users/leo/Zimportant/src/bin_src/spark-2.1.1-bin-hadoop2.7

# Start Spark Master

sbin/start-master.sh

# Start Spark Slaves

sbin/start-slave.sh spark://leos-MacBook.local:7077

# Create BDS_Network
docker network create --driver=bridge --subnet=172.50.11.0/24 --ip-range=172.50.11.0/24 --gateway=172.50.11.254 bds_network

# Docs to support Redis
https://goo.gl/sdbgHM

protected-mode yes -> no
CONFIG SET protected-mode no


# Docs to support Cassandra.
https://goo.gl/HkwKoW