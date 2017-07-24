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
import java.util.Random;

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
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * Size of the buffer used to copy a file (in bytes).
     */
    private static final int COPY_FILE_BUFFER_SIZE = 1024 * 1024;

    /**
     * Creates a temporary file based on application context and the file type.
     *
     * @param context  The context which the temporary file will be created.
     * @param fileType The type of temporary file to be created.
     * @return The temporary file created.
     */
    @SuppressWarnings("unused")
    public static File createTemporaryFile(@NonNull Context context, FileType fileType) {

        File cacheDirectory = context.getCacheDir();

        if (!cacheDirectory.exists()) {
            if (!cacheDirectory.mkdirs()) {
                throw new RuntimeException("Could not create temporary file's directory \"" + cacheDirectory.getAbsolutePath() + "\".");
            }
        }

        String formattedDate = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

        int randomInt = new Random().nextInt(65535);


        File temporaryFile = new File(cacheDirectory.getPath() + File.separator + formattedDate + "_" + randomInt + "." + fileType.getFileExtension());

        try {
            boolean fileDoesNotExist = temporaryFile.createNewFile();
            if (!fileDoesNotExist) {
                Log.w(LOG_TAG, "createTemporaryFile (64): Temporary file \"" + temporaryFile.getAbsolutePath() + "\" already exists.");
            }
        } catch (IOException ioException) {
            throw new RuntimeException("Could not create temporary file \"" + temporaryFile.getAbsolutePath() + "\".");
        }

        return temporaryFile;
    }

    /**
     * Creates a new file.
     *
     * @param context  The context which the file will be created.
     * @param fileType The type of the file to be created.
     * @return The file created.
     */
    public static File createFile(@NonNull Context context, @NonNull FileType fileType) {

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
            Log.i(LOG_TAG, "createFile (106): Directory \"" + outputDirectory.getAbsolutePath() + "\" created.");
        }

        String fileName = createFileName(fileType);

        File file = new File(outputDirectory, fileName);

        try {
            boolean fileDoesNotExist = file.createNewFile();
            if (!fileDoesNotExist) {
                Log.w(LOG_TAG, "createFile (120): File \"" + file.getAbsolutePath() + "\" already exists.");
            }
        } catch (IOException ioException) {
            throw new RuntimeException("Could not create file \"" + file.getAbsolutePath() + "\".", ioException);
        }

        return file;
    }

    /**
     * Copies a file on the same directory.
     *
     * @param file File to be copied.
     * @return The copy file.
     */
    @SuppressWarnings("unused")
    public static File copyFile(File file) {

        String copyFilePath = createCopyFilePath(file);

        File copyFile = new File(copyFilePath);

        copyFileContent(file, copyFile);

        return copyFile;
    }

    /**
     * Copies a file.
     *
     * @param sourceFile      The source file to be copied.
     * @param destinationFile The destination file where the source will be copied.
     */
    public static void copyFile(File sourceFile, File destinationFile) {
        copyFileContent(sourceFile, destinationFile);
    }

    /**
     * Creates the path of the copy file.
     *
     * @param originalFile The original file to be copied.
     * @return The path of the copy file.
     */
    private static String createCopyFilePath(File originalFile) {
        int fileCopyIndex = 1;
        boolean checkFileExists = true;
        String copyFileName;
        String copyFilePath = null;
        String fileName = originalFile.getName();
        String filePath = originalFile.getPath();

        while (checkFileExists) {
            copyFileName = fileName + " (" + fileCopyIndex + ")";
            copyFilePath = filePath + File.pathSeparator + copyFileName;
            File copyFile = new File(copyFilePath);

            if (copyFile.exists()) {
                fileCopyIndex++;
            } else {
                checkFileExists = false;
            }
        }

        return copyFilePath;
    }

    /**
     * Copies the content of a file.
     *
     * @param sourceFile      The file which its content will be copied.
     * @param destinationFile The file which the content will be written.
     */
    private static void copyFileContent(File sourceFile, File destinationFile) {

        boolean copyConcluded = false;
        int bytesRead;
        byte[] buffer = new byte[COPY_FILE_BUFFER_SIZE];

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);

            while (!copyConcluded) {
                bytesRead = fileInputStream.read(buffer, 0, COPY_FILE_BUFFER_SIZE);

                if (bytesRead > 0) {
                    fileOutputStream.write(buffer, 0, bytesRead);

                    if (bytesRead < COPY_FILE_BUFFER_SIZE) {
                        copyConcluded = true;
                    }
                } else {
                    copyConcluded = true;
                }
            }

        } catch (IOException ioException) {
            Log.e(LOG_TAG, "copyFileContent (172): Error while copying file \"" + sourceFile.getAbsolutePath() + "\" content to \"" + destinationFile.getAbsolutePath() + "\".");
            throw new RuntimeException("Error while copying file \"" + sourceFile.getAbsolutePath() + "\" to \"" + destinationFile.getAbsolutePath() + "\".", ioException);
        }
    }

    /**
     * Checks if external storage is writable.
     *
     * @return True if external storage is writable. False otherwise.
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Creates a new file name.
     *
     * @param fileType The type of the file to be created.
     * @return The name of the new file.
     */
    private static String createFileName(FileType fileType) {
        String formattedDate = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return formattedDate + "." + fileType.getFileExtension();
    }
}
