CREATE KEYSPACE IF NOT exists fastdata
WITH REPLICATION = {
'class' :'SimpleStrategy',
'replication_factor':1
}
AND DURABLE_WRITES = true

;

CREATE TABLE IF NOT exists fastdata.clicks(
id  uuid,
click_id  uuid,
product_id  uuid,
timestamp  timestamp,
fp_id  uuid,
link_id  uuid,
dns_ip  inet,
public_ip  inet,
private_ip  inet,
referrer  text,
user_agent  text,
isp  text,
country  text,
city  text,
language  text,
os  text,
browser  text,
time_zone  text,
screen_size  text,
fonts  text,
http_params  map < text, text >,
campaign_id  uuid,
channel_id  uuid,
zone_id  uuid,
utm_source  text,
utm_campaign  text,
utm_medium  text,
utm_term  text,
network  text,
future_params  map < text, text >,
PRIMARY KEY (id)
);