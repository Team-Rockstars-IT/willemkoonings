-- Create sequences for batch processing
CREATE SEQUENCE IF NOT EXISTS artist_id_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS song_id_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS playlist_id_seq START WITH 1 INCREMENT BY 50;


CREATE TABLE IF NOT EXISTS artists (
    id BIGINT PRIMARY KEY DEFAULT nextval('artist_id_seq'),
    name VARCHAR NOT NULL UNIQUE,
    external_id BIGINT UNIQUE
);

CREATE TABLE IF NOT EXISTS songs (
    id BIGINT PRIMARY KEY DEFAULT nextval('song_id_seq'),
    name VARCHAR NOT NULL,
    year INTEGER NOT NULL,
    artist_id BIGINT NOT NULL REFERENCES artists(id) ON DELETE CASCADE,
    shortname VARCHAR NOT NULL,
    bpm INTEGER,
    duration INTEGER NOT NULL CHECK (duration > 0),
    genre VARCHAR NOT NULL,
    spotify_id VARCHAR,
    album VARCHAR,
    external_id BIGINT UNIQUE,
    UNIQUE(name, artist_id)
);

CREATE TABLE IF NOT EXISTS playlists (
    id BIGINT NOT NULL DEFAULT nextval('playlist_id_seq'),
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    CONSTRAINT pk_playlists PRIMARY KEY (id),
    CONSTRAINT chk_playlist_name_length CHECK (length(name) >= 2),
    CONSTRAINT chk_playlist_username_length CHECK (length(username) >= 2)
);

CREATE TABLE playlist_songs (
    playlist_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    PRIMARY KEY (playlist_id, song_id),
    FOREIGN KEY (playlist_id) REFERENCES playlists(id),
    FOREIGN KEY (song_id) REFERENCES songs(id)
);
