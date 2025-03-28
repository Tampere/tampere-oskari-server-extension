# File download/upload

Layer configuration on oskari_maplayer.attributes:

    {
        "attachmentKey":"lehtinro",
        "attachmentPath":"/data/extfilesForLayer/"
    }

- attachmentKey is reference to map layer feature property name. The value of the property is used to map attachment to a feature. Defaults to id.
- attachmentPath is an optional config for externally linked files and the value should point to a folder on the server.

If the layer has attachmentPath configured the folder should have subfolders based on the feature "id"/attachmentKey value
 that has files named like [label].[extension] where label is shown on the UI. So having files like GeoJSON.json and shp.zip
  will be shown on the UI as "GeoJSON" and "shp" as links to download the files.

Note! WFS-layers only. External files are not listed if there are files for the layer that are managed in the database
 (== if files have been uploaded using the UI).
 