CREATE TABLE slickrent.user_profile (
  id UUID NOT NULL PRIMARY KEY,
  username TEXT NOT NULL,
  email TEXT NOT NULL,
  photo_uuid UUID NOT NULL,
  full_name TEXT NOT NULL,
  phone_number TEXT NOT NULL,
  address_line1 TEXT NOT NULL,
  address_line2 TEXT,
  city TEXT NOT NULL,
  state TEXT NOT NULL,
  zip_code TEXT NOT NULL,
  country_code TEXT NOT NULL,
  created_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_time TIMESTAMP WITH TIME ZONE DEFAULT now(),
  user_id UUID REFERENCES slickrent.user (id) NOT NULL
);

CREATE INDEX profile_user_id_index ON slickrent.user_profile(user_id);
