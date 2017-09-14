CREATE KEYSPACE IF NOT EXISTS fastdata
with REPLICATION = {
'class' : 'SimpleStrategy',
'replication_factor' : 1
}
AND DURABLE_WRITES = true;

//DROP TABLE fastdata.ccu;
TRUNCATE fastdata.ccu;

CREATE TABLE IF NOT EXISTS fastdata.ccu (
time text PRIMARY KEY,
keys text,
counts int
);

SELECT * FROM fastdata.ccu;