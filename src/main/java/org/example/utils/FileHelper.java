package org.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class FileHelper {

    private static final Logger logger = LogManager.getLogger(FileHelper.class);

    public static void deleteFolderContents(File folder) {
        boolean result = false;
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolderContents(file);
                    } else {
                        result = file.delete();
                    }
                }
            } else {
                logger.info("Graphs folder is already empty");
                result = true;
            }
        }

        if (result) {
            logger.info("Some error occurred during folder deletion Folder contents is deleted successfully :)");
        } else {
            logger.error("Some error occurred during folder deletionç");
        }
    }
}
