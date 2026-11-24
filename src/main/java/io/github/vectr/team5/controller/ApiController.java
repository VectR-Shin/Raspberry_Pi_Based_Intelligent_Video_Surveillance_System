package io.github.vectr.team5.controller;

import io.github.vectr.team5.dto.*;
import io.github.vectr.team5.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final LogQueryService logQueryService;
    private final ImageQueryService imageQueryService;
    private final DeviceQueryService deviceQueryService;
    private final DeviceCommandService deviceCommandService;
    private final LogCommandService logCommandService;

    @GetMapping("/logs")
    public PageResponseDTO<LogResponseDTO> getLogs(
            @AuthenticationPrincipal OidcUser principal,
            @Valid @ModelAttribute LogDataRequestDTO request
    ) {
        return logQueryService.getLogs(principal.getSubject(), request);
    }

    @PostMapping(value = "/logs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createLog(
            @AuthenticationPrincipal OidcUser principal,
            @RequestHeader("X-DEVICE-ID") String deviceIdentifier,
            @Valid @RequestPart("log") LogRequestDTO request,
            @RequestPart("image") MultipartFile image
    ) {
        logCommandService.createLog(principal.getSubject(), deviceIdentifier, request, image);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> getImage(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long imageId
    ) {

        String keycloakSub = oidcUser.getSubject();

        ImageFileResponse image =
                imageQueryService.getImage(imageId, keycloakSub);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(image.contentType());
        headers.setContentDisposition(
                ContentDisposition.inline().build()
        );
        headers.setCacheControl("no-store");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(image.data());
    }

    @GetMapping("/devices")
    public PageResponseDTO<DeviceResponseDTO> getDevices(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestParam(required = false) String alias,
            @RequestParam(defaultValue = "0") int page
    ) {
        return deviceQueryService.getDevices(oidcUser.getSubject(), alias, page);
    }

    @PostMapping("/devices")
    public ResponseEntity<DeviceResponseDTO> registerDevice(
            @AuthenticationPrincipal OidcUser oidcUser,
            @Valid @RequestBody DeviceRegistrationRequestDTO request
    ) {
        DeviceResponseDTO response = deviceCommandService.registerDevice(
                oidcUser.getSubject(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/devices/{deviceId}/alias")
    public DeviceResponseDTO updateAlias(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long deviceId,
            @Valid @RequestBody DeviceAliasUpdateRequestDTO request
    ) {
        log.info("닉네임 수정 완료");
        return deviceCommandService.updateAlias(
                oidcUser.getSubject(),
                deviceId,
                request
        );
    }

    @DeleteMapping("/devices/{deviceId}")
    public ResponseEntity<Void> unregisterDevice(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long deviceId
    ) {
        log.info("기기 삭제 완료");
        deviceCommandService.unregisterDevice(
                oidcUser.getSubject(),
                deviceId
        );

        return ResponseEntity.noContent().build();
    }
}
