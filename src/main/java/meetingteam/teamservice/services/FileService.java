package meetingteam.teamservice.services;

public interface FileService {
    String generatePreSignedUrl(String newFile, String oldUrl);
    void deleteFile(String fileUrl);
}
