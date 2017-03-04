README
# UAA Performance Test

## Prerequisite
1. Download Jmeter and add the bin directory to the path

## Run data-load-scripts
These scripts must be run in this order only.
1. Add `127.0.0.1 perfzone{count}.localhost` to /etc/hosts file. Add an entry for each zone that will be created. ex: perfzone1, perfzone2
2. Run `create_uaa_zone_clients.sh` and specify `number of identity zones` to be created as argument of this script. This script creates specified number of zones and one client in each zone.
3. Run `create_uaa_users.sh` and specify `start index of user` and `end index of user` as arguments. This script creates one user per identity zone. As per this script, `end index of user` can not be specified higher than the total number of zones created.

## Run jmeter-scripts
1. execute the following
 `jmeter -n -t {path_to_jmx_file} -l {log_file}.jtl`
2. The summary report can be viewed in Jmeter GUI using the {log_file}.jtl file