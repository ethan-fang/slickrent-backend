CREATE SCHEMA IF NOT EXISTS slickrent;


CREATE TABLE slickrent.login_platform (
  id SMALLSERIAL NOT NULL PRIMARY KEY,
  platform TEXT UNIQUE NOT NULL
);

INSERT INTO slickrent.login_platform (id, platform) VALUES (0, 'NATIVE');
INSERT INTO slickrent.login_platform (id, platform) VALUES (1, 'FB');
INSERT INTO slickrent.login_platform (id, platform) VALUES (2, 'GOOGLE');

CREATE TABLE slickrent.user (
  id UUID NOT NULL PRIMARY KEY,
  username TEXT NOT NULL,
  password TEXT,
  access_token TEXT NOT NULL,
  login_platform_id INTEGER NOT NULL DEFAULT 0 REFERENCES slickrent.login_platform(id),
  CONSTRAINT user_constraint UNIQUE(username, login_platform_id)
);
CREATE INDEX user_index ON slickrent.user(id, username, access_token);




CREATE TABLE slickrent.item (
  id UUID NOT NULL PRIMARY KEY,
  item_name TEXT NOT NULL,
  item_description TEXT,
  price_usd_cent_per_min NUMERIC,
  rental_start TIMESTAMP WITH TIME ZONE,
  rental_end TIMESTAMP WITH TIME ZONE,
  image_uuids UUID[],
  created_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
  user_id UUID REFERENCES slickrent.user (id) NOT NULL
);

CREATE INDEX item_id_index ON slickrent.item(id);
CREATE INDEX item_user_id_index ON slickrent.item(user_id);



