CREATE KEYSPACE IF NOT exists fastdata
WITH REPLICATION = {
'class' :'SimpleStrategy',
'replication_factor':1
}
AND DURABLE_WRITES = true;

//DROP TABLE fastdata.ccu;
//TRUNCATE fastdata.ccu;

CREATE TABLE IF NOT exists fastdata.ccu (
time text PRIMARY KEY,
keys text,
counts int
);

SELECT * FROM fastdata.ccu;