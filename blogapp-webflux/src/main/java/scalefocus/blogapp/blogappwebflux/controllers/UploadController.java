package scalefocus.blogapp.blogappwebflux.controllers;

import io.github.techgnious.IVCompressor;
import io.github.techgnious.dto.ImageFormats;
import io.github.techgnious.dto.ResizeResolution;
import io.github.techgnious.dto.VideoFormats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import scalefocus.blogapp.blogappwebflux.domain.AttachmentFile;
import scalefocus.blogapp.blogappwebflux.repositories.AttachementFileRepository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v3/blogs/file")
@Slf4j
@RequiredArgsConstructor
public class UploadController {

    private final AttachementFileRepository databaseFileService;

    @PostMapping("/upload")
    public Mono<Void> upload(@RequestPart("files") Flux<FilePart> filePartFlux, @RequestParam(required = true) Long blogId, @RequestParam(required = false) ResizeResolution resizeResolution) {

        return filePartFlux
                .doOnNext(fp -> System.out.println(fp.filename()))
                .flatMap(fp -> DataBufferUtils.join(fp.content()).map(dataBuffer -> {
                    var bytes = dataBuffer.asByteBuffer().array();
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    Tika tika = new Tika();
                    String mimeType = tika.detect(bytes);
                    String filename = fp.filename();
                    return new AttachmentFile(bytes, blogId, filename, mimeType);
                }))
                .doOnNext(m -> {
                    if (resizeResolution != null) {
                        try {

                            if (m.isImage())
                                m.setFile(new IVCompressor().resizeImage(m.getFile(), ImageFormats.valueOf(m.getFormat()), resizeResolution));
                            else
                                m.setFile(new IVCompressor().reduceVideoSize(m.getFile(), VideoFormats.valueOf(m.getFormat()), resizeResolution));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).flatMap(databaseFileService::save)
                .log()
                .then();
    }

    private static String getFilePartFileType(String fileName, byte[] fewBytes) throws Exception {
        String mimeType;
        Tika tika = new Tika();
        CompletableFuture<String> mimeTypeFuture = CompletableFuture.supplyAsync(() -> tika.detect(fewBytes, fileName), Executors.newCachedThreadPool());
        mimeType = mimeTypeFuture.get();
        return mimeType;
    }


}