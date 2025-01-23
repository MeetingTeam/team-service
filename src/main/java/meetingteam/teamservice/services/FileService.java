package meetingteam.teamservice.services;

public interface FileService {
    String generatePreSignedUrl(String folder,String newFile, String oldUrl);
    void deleteFile(String fileUrl);
}
