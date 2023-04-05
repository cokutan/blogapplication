package scalefocus.blogapp.blogappwebflux.controllers;

import io.github.techgnious.IVCompressor;
import io.github.techgnious.dto.ImageFormats;
import io.github.techgnious.dto.ResizeResolution;
import io.github.techgnious.dto.VideoFormats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import scalefocus.blogapp.blogappwebflux.domain.AttachmentFile;
import scalefocus.blogapp.blogappwebflux.exceptions.CannotDoTheCompressionException;
import scalefocus.blogapp.blogappwebflux.repositories.AttachementFileRepository;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v3/blogs/file")
@Slf4j
@RequiredArgsConstructor
public class UploadController {

  private final AttachementFileRepository databaseFileService;

  @Operation(
      summary =
          "Upload a bunch of images (jpg, jpeg, png) and/or videos (mp4, mkv, flv, mov, avi, wmv)",
      tags = {"image", "video"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Succesfully uploaded files",
            content = @Content),
        @ApiResponse(responseCode = "500", content = @Content)
      })
  @PostMapping(name = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<Void> upload(
      @Parameter(
              content =
                  @Content(
                      mediaType = "multipart/form-data",
                      schema =
                          @Schema(
                              description = "file to upload",
                              type = "string",
                              format = "binary")),
              description = "image or video files")
          @RequestPart("files")
          Flux<FilePart> filePartFlux,
      @Parameter(description = "related blog id") @RequestParam Long blogId,
      @Parameter(required = false, description = "in which resolution the file will be converted")
          @RequestParam(required = false)
          ResizeResolution resizeResolution) {
    return filePartFlux
        .flatMap(
            fp ->
                DataBufferUtils.join(fp.content())
                    .map(
                        dataBuffer -> {
                          byte[] bytes;
                          try {
                            InputStream inputStream = dataBuffer.asInputStream();
                            bytes = inputStream.readAllBytes();
                            inputStream.close();
                          } catch (IOException e) {
                            throw new RuntimeException(e);
                          }
                          DataBufferUtils.release(dataBuffer);
                          Tika tika = new Tika();
                          String mimeType = tika.detect(bytes);
                          String filename = fp.filename();
                          return new AttachmentFile(bytes, blogId, filename, mimeType);
                        }))
        .doOnNext(
            m -> {
              if (resizeResolution != null) {
                try {
                  if (m.isImage())
                    m.setFile(
                        new IVCompressor()
                            .resizeImage(
                                m.getFile(),
                                ImageFormats.valueOf(m.getFormat()),
                                resizeResolution));
                  else
                    m.setFile(
                        new IVCompressor()
                            .reduceVideoSize(
                                m.getFile(),
                                VideoFormats.valueOf(m.getFormat()),
                                resizeResolution));
                } catch (Exception e) {
                  throw new CannotDoTheCompressionException(m.getFilename(), resizeResolution);
                }
              }
            })
        .flatMap(databaseFileService::save)
        .log()
        .then();
  }

  @Operation(
      summary = "delete the file with given id",
      tags = {"image", "video"},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Succesfully deleted files",
            content = @Content)
      })
  @DeleteMapping("/delete")
  public Mono<Void> delete(
      @Parameter(description = "id of the file to be deleted") @RequestParam
          Long attachmentFileId) {
    return databaseFileService.deleteAttachmentFileById(attachmentFileId);
  }
}
