CREATE TABLE IF NOT EXISTS artists
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR NOT NULL UNIQUE,
    external_id BIGINT UNIQUE
);

CREATE TABLE IF NOT EXISTS songs
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR NOT NULL,
    "year"      INTEGER NOT NULL,
    artist_id   BIGINT  NOT NULL REFERENCES artists (id) ON DELETE CASCADE,
    shortname   VARCHAR NOT NULL,
    bpm         INTEGER,
    duration    INTEGER NOT NULL CHECK (duration > 0),
    genre       VARCHAR NOT NULL,
    spotify_id  VARCHAR,
    album       VARCHAR,
    external_id BIGINT UNIQUE,
    UNIQUE (name, artist_id)
);