package org.marceloleite.projetoanna.utils.file;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import org.marceloleite.projetoanna.utils.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Stores some components which are utils to manipulate files.
 */

public abstract class FileUtils {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = FileUtils.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(FileUtils.class);
    }

    /**
     * Size of buffer used to copy the file content.
     */
    private static final int COPY_FILE_BUFFER_SIZE = 1024 * 1024;

    /**
     * Creates a temporary file based on application context and the file type.
     *
     * @param context  The context which the temporary file will be created.
     * @param fileType The type of the file to be created.
     * @return The temporary file created.
     */
    public static File createTemporaryFile(Context context, FileType fileType) {

        boolean fileCreated;
        File temporaryFile = null;

        File cacheDirectory = context.getCacheDir();

        if (!cacheDirectory.exists()) {
            if (!cacheDirectory.mkdirs()) {
                throw new RuntimeException("Unable to create directory \"" + cacheDirectory.getPath() + "\".");
            }
        }

        fileCreated = false;
        while (!fileCreated) {
            temporaryFile = new File(cacheDirectory.getPath() + File.separator + createFileName(fileType));

            try {
                if (temporaryFile.createNewFile()) {
                    fileCreated = true;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interruptedException) {
                        throw new RuntimeException("Interrupted while waiting to create a new temporary file.");
                    }
                }
            } catch (IOException ioException) {
                Log.d(FileUtils.class, LOG_TAG, "createTemporaryFile (66): Error while creating new temporary file \"" + temporaryFile.getAbsolutePath() + "\".");
                ioException.printStackTrace();
            }
        }

        return temporaryFile;
    }

    /**
     * Creates a new file based on its type.
     *
     * @param context  The context to create the file.
     * @param fileType The type of the file to be created.
     * @return The new file created.
     */
    public static File createFile(Context context, FileType fileType) {

        /* Checks if external storage is available. */
        if (!isExternalStorageWritable()) {
            throw new RuntimeException("External storage is not available.");
        }

        String rootDirectory;
        switch (fileType) {
            case TEMPORARY_FILE:
                throw new RuntimeException("Temporary files should be created on using \"createTemporaryFile\" method.");
            case AUDIO_MP3_FILE:
            case AUDIO_AAC_FILE:
            case AUDIO_RAW_FILE:
                rootDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
                break;
            case VIDEO_FILE:
            case MOVIE_FILE:
                rootDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
                break;
            default:
                throw new RuntimeException("Unknown file type " + fileType);
        }

        File outputDirectory = new File(rootDirectory + File.separator + context.getPackageName());

        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new RuntimeException("Could not create directory \"" + outputDirectory.getAbsolutePath() + "\".");
            }
            Log.d(FileUtils.class, LOG_TAG, "createFile (106): Directory \"" + outputDirectory.getAbsolutePath() + "\" created.");
        }

        String fileName = createFileName(fileType);

        File file = new File(outputDirectory, fileName);

        try {
            file.createNewFile();
        } catch (IOException ioException) {
            throw new RuntimeException("Could not create file \"" + file.getAbsolutePath() + "\".");
        }

        return file;
    }

    public static File copyFile(File file) {

        String copyFilePath = createCopyFilePath(file.getParent(), file.getName());

        File copyFile = new File(copyFilePath);

        copyFileContent(file, copyFile);

        return copyFile;
    }

    private static String createCopyFilePath(String fileDirectory, String fileName) {
        int fileCopyIndex = 1;
        boolean checkFileExists = false;
        String copyFileName = null;
        String copyFilePath = null;

        while (checkFileExists) {
            copyFileName = fileName + " (" + fileCopyIndex + ")";
            copyFilePath = fileDirectory + File.pathSeparator + copyFileName;
            File copyFile = new File(copyFilePath);

            if (copyFile.exists()) {
                fileCopyIndex++;
            } else {
                checkFileExists = true;
            }
        }

        return copyFilePath;
    }

    private static boolean copyFileContent(File sourceFile, File destinationFile) {

        boolean copyConcluded = false;
        int bytesRead;
        byte[] buffer = new byte[COPY_FILE_BUFFER_SIZE];

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);

            while (copyConcluded) {
                bytesRead = fileInputStream.read(buffer, 0, COPY_FILE_BUFFER_SIZE);
                fileOutputStream.write(buffer, 0, bytesRead);

                if (bytesRead <= 0) {
                    copyConcluded = true;
                }
            }

            return true;

        } catch (IOException ioException) {
            Log.e(FileUtils.class, LOG_TAG, "copyFileContent (172): Error while copying file \"" + sourceFile.getAbsolutePath() + "\" content to \"" + destinationFile.getAbsolutePath() + "\".");
            ioException.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if external storage is mounted.
     *
     * @return True if external storage is mounted. False otherwise.
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Creates a file name based on current time and its format.
     *
     * @param fileType The type of the file to create the name.
     * @return The name created for the file.
     */
    private static String createFileName(FileType fileType) {
        String formattedDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return formattedDate + "." + fileType.getFileExtension();
    }

}
