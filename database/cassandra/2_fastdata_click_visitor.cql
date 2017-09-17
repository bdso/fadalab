CREATE KEYSPACE IF NOT EXISTS fastdata
with REPLICATION = {
'class' : 'SimpleStrategy',
'replication_factor' : 1
}
AND DURABLE_WRITES = true;

CREATE TABLE IF NOT EXISTS fastdata.click (
time text PRIMARY KEY,
keys text,
counts int
);