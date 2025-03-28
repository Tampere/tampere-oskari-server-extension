-- save which property is used as id to oskari_maplayer.attributes -> attachment_prop
create table IF NOT EXISTS tampere_layer_attachment (
    id serial,
    layer_id integer not null,
    feature_id text not null,
    locale text,
    file_extension text,
    CONSTRAINT tampere_attachment_layer_files_pkey PRIMARY KEY (id),
    CONSTRAINT tampere_attachment_layer_files_fkey FOREIGN KEY (layer_id)
        REFERENCES oskari_maplayer (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE
);


CREATE INDEX tampere_layer_attachment_id_idx
ON tampere_layer_attachment
USING btree(id);

CREATE INDEX tampere_layer_attachment_layer_idx
ON tampere_layer_attachment
USING btree(layer_id);