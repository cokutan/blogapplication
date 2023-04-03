package scalefocus.blogapp.blogappwebflux.domain;

import java.util.Optional;

public enum ImageFormats {

    JPG("jpg"), JPEG("jpeg"), PNG("png");

    /**
     * Extension type of the Image
     */
    private String type;

    ImageFormats(String type) {
        this.type = type;
    }

    /**
     * @return the extension type of the image
     */
    public String getType() {
        return type;
    }

    public static Optional<ImageFormats> as(String mimeType) {
        if (mimeType.endsWith(JPEG.getType())) return Optional.of(JPEG);
        if (mimeType.endsWith(JPG.getType())) return Optional.of(JPG);
        if (mimeType.endsWith(PNG.getType())) return Optional.of(PNG);
        return Optional.empty();
    }
}
