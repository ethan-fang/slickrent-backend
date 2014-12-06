create table shareitemschema.user (
  id UUID NOT NULL PRIMARY KEY,
  username TEXT NOT NULL,
  password TEXT NOT NULL,
  access_token TEXT
)
CREATE INDEX user_index ON shareitemschema.user(id, access_token);


create table shareitemschema.item (
  id UUID NOT NULL PRIMARY KEY,
  item_name TEXT,
  item_description TEXT,
  rental_start TIMESTAMP WITH TIME ZONE,
  rental_end TIMESTAMP WITH TIME ZONE,
  image_uuids UUID[],
  user_id UUID REFERENCES shareitemschema.user (id)
)
CREATE INDEX item_id_index ON shareitemschema.item(id);

