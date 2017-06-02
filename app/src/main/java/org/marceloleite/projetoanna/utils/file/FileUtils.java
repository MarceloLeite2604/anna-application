package org.marceloleite.projetoanna.utils.file;

import android.content.Context;
import android.os.Environment;

import org.marceloleite.projetoanna.utils.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Marcelo Leite on 03/05/2017.
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

    private static final NullPointerException CONTEXT_NOT_DEFINED_EXCEPTION = new NullPointerException("Context for file creation is not specified. Use \"setContext\" method to define it.");

    private static final int COPY_FILE_BUFFER_SIZE = 1024 * 1024;

    private static Context context = null;

    public static void setContext(Context context) {
        FileUtils.context = context;
    }

    public static File createTemporaryFile(FileType fileType) {

        if (context == null) {
            throw CONTEXT_NOT_DEFINED_EXCEPTION;
        }

        File cacheDirectory = context.getCacheDir();

        if (!cacheDirectory.exists()) {
            if (!cacheDirectory.mkdirs()) {
                Log.d(FileUtils.class, LOG_TAG, "createTemporaryFile (53): Failed to create directory \"" + cacheDirectory.getPath() + "\".");
                return null;
            }
        }

        String formattedDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());


        File temporaryFile = new File(cacheDirectory.getPath() + File.separator + formattedDate + fileType.getFileExtension());

        try {
            temporaryFile.createNewFile();
        } catch (IOException ioException) {
            Log.d(FileUtils.class, LOG_TAG, "createTemporaryFile (66): Error while creating new temporary file \"" + temporaryFile.getAbsolutePath() + "\".");
            ioException.printStackTrace();
        }

        return temporaryFile;
    }

    public static File createFile(FileType fileType) throws IOException {

        if (context == null) {
            throw CONTEXT_NOT_DEFINED_EXCEPTION;
        }

        if (!isExternalStorageWritable()) {
            throw new IOException("External storage is not available.");
        }

        String rootDirectory = null;
        switch (fileType) {
            case TEMPORARY_FILE:
                throw new IOException("Temprary files should be created on using \"createTemporaryFile\" method.");
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
                throw new IllegalArgumentException("Unknown file type " + fileType);
        }

        File outputDirectory = new File(rootDirectory + File.separator + context.getPackageName());

        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new IOException("Could not create directory \"" + outputDirectory.getAbsolutePath() + "\".");
            }
            Log.d(FileUtils.class, LOG_TAG, "createFile (106): Directory \"" + outputDirectory.getAbsolutePath() + "\" created.");
        }

        String fileName = createFileName(fileType);

        File file = new File(outputDirectory, fileName);

        file.createNewFile();

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


    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private static String createFileName(FileType fileType) {
        String formattedDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = formattedDate + "." + fileType.getFileExtension();
        return fileName;
    }

}
