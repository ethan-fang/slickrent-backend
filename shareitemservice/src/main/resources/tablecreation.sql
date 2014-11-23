
create table shareitemschema.item (
  id UUID NOT NULL PRIMARY KEY,
  item_name TEXT,
  item_description TEXT,
  rental_start TIMESTAMP WITH TIME ZONE,
  rental_end TIMESTAMP WITH TIME ZONE,
  image_uuids UUID[]
)


