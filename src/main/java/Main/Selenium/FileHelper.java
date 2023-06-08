package Main.Selenium;

import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public class FileHelper {
    public FileHelper() {
    }

    public FileUpload nameToFileUpload(String fileName) {
        File tempFile = new File(fileName);
        File file = new File(tempFile.getAbsolutePath()+".xls");
        return FileUpload.fromData(file);

    }
}
