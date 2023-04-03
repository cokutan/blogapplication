package scalefocus.blogapp.blogappwebflux.domain;

import java.util.Optional;

public enum VideoFormats {
    MP4("mp4"),
    MKV("mkv"),
    FLV("flv"),
    MOV("mov"),
    AVI("avi"),
    WMV("wmv");

    private String type;

    private VideoFormats(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static Optional<VideoFormats> as(String mimeType) {
        if (mimeType.endsWith(MP4.getType())) return Optional.of(MP4);
        if (mimeType.endsWith(MKV.getType())) return Optional.of(MKV);
        if (mimeType.endsWith(FLV.getType())) return Optional.of(FLV);
        if (mimeType.endsWith(MOV.getType())) return Optional.of(MOV);
        if (mimeType.endsWith(AVI.getType())) return Optional.of(AVI);
        if (mimeType.endsWith(WMV.getType())) return Optional.of(WMV);
        return Optional.empty();
    }
}