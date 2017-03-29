README
# UAA Performance Test

## Prerequisite
1. Download Jmeter and add the bin directory to the path
1. `gradle`,`mysql`, `mysqlimport`

## Run data-load-scripts

Perform the following gradle tasks to CSV load performance data into the target mysql db.
1. `cd data-load-scripts`
1. Update the DB configuration in `gradle.properties`
1. If the environment requires SSH tunneling, run `gradle startSSH -Pfile=path_to_pem_file`
1. Then run `gradle createAndLoad -P count=num_of_zones,num_of_clients_per_zone,num_of_users_per_zone` to create the CSV and import them to the target db
1. To delete performance data from the db use `gradle cleandb`
1. If SSH tunnel was established earlier, `gradle stopSSH` can be used to close it

## Run jmeter-scripts
1. Add `127.0.0.1 perfzone{count}.localhost` to /etc/hosts file. Add an entry for each zone that will be created. ex: perfzone1, perfzone2
1. execute the following
 `jmeter -n -t {path_to_jmx_file} -l {log_file}.jtl`
2. The summary report can be viewed in Jmeter GUI using the {log_file}.jtl file
