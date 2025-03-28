CREATE TABLE IF NOT EXISTS tampere_filedl_log_file (
    at timestamp NOT NULL DEFAULT NOW(),
    user_id integer not null,
    layer_id integer not null,
    file_id integer not null
);

CREATE TABLE IF NOT EXISTS tampere_filedl_log_external (
    at timestamp NOT NULL DEFAULT NOW(),
    user_id integer not null,
    layer_id integer not null,
    feature_id text not null,
    name text not null
);
