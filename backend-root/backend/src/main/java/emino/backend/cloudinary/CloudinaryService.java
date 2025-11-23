package emino.backend.cloudinary;

import java.io.IOException;
import java.util.UUID;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Plik nie może być pusty");
        }

        String publicId = folder + "/" + UUID.randomUUID().toString();

        Map<String, Object> params = ObjectUtils.asMap(
            "public_id", publicId,
            "folder", folder,
            "resource_type", "image",
            "quality", "auto",
            "fetch_format", "auto"
        );

        Map upload = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) upload.get("secure_url");
    }

    public void deleteImage(String imgUrl) throws IOException {
        if (imgUrl == null || imgUrl.isEmpty()) {
            return;
        }

        String publicId = extractIdFromUrl(imgUrl);
        if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    private String extractIdFromUrl(String imgUrl) {
        try {
            String[] parts = imgUrl.split("/upload/");
            if (parts.length > 1) {
                String publicIdWithExtenstion = parts[1].substring(parts[1].indexOf("/") + 1);
                return publicIdWithExtenstion.substring(0, publicIdWithExtenstion.lastIndexOf('.'));
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas ekstrakcji public_id z URL: " + e.getMessage());
        }
        return null;
    }

}
