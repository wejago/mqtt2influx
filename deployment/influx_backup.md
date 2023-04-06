# Backup influxdb
In this document we describe how to perform backup and restore of influxdb v2 from docker container.

1. Backup the data from a docker container:
`docker exec -it <container_id> influx backup <backup_location> -t <secret_token>`

2. Copy the data from the docker container to the server:
`docker cp <container_id>:<backup_location> <folder_server_location>`

3. (optional) Copy the files on local machine:
`from the local machine run: scp <user>@server_ip_address:/backup_location.`

4. Copy the data to docker container:
`docker cp <folder_server_location> <container_id>:<backup_location>`

5. Delete existing bucket and restore data:
```
    influx bucket delete --name my-init-bucket
    influx restore ./<container_backup_location>
```

### Useful links:
- https://docs.influxdata.com/influxdb/v2.6/backup-restore/restore
- https://jet.dev/blog/secure-influxdb-setup-with-docker
