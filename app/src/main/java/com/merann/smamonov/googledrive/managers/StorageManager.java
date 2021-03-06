package com.merann.smamonov.googledrive.managers;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.merann.smamonov.googledrive.model.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by samam_000 on 16.12.2015.
 */
public class StorageManager {

    public interface StorageManagerListener {

        void onFilesChanged();

        void onFileUpload(File file, boolean isSuccess);

        void onConnectionFailed(ConnectionResult connectionResult);
    }

    private final static String LOG_TAG = "StorageManager";

    private HashMap<String, Image> mImages;
    private HashMap<String, File> mFilesToBeUploaded;

    private Context mContext;

    private StorageManagerListener mStorageManagerListener;
    private RemoteStorageManager mRemoteStorageManager;

    private DiskCacheHelper mDiskCacheHelper;
    private DataBaseHelper mDataBaseHelper;
    private Boolean isSyncNeeded;

    public StorageManager(Context context,
                          final StorageManagerListener storageManagerListener) {

        Log.d(LOG_TAG, "StorageManager");

        mContext = context;
        mStorageManagerListener = storageManagerListener;
        mDiskCacheHelper = new DiskCacheHelper(mContext);
        mDataBaseHelper = new DataBaseHelper(mContext);
        mImages = new HashMap();
        mFilesToBeUploaded = new HashMap();
        isSyncNeeded = false;

        mRemoteStorageManager = new RemoteStorageManager(context, new RemoteStorageManager.RemoteStorageManagerListener() {
            @Override
            public void onFileUpload(File file, boolean isSuccess) {
                Log.d(LOG_TAG, "onFileUpload");
                mStorageManagerListener.onFileUpload(file, isSuccess);
            }

            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {

                Iterator fileToBeLoadIterator = mFilesToBeUploaded.entrySet().iterator();
                while (fileToBeLoadIterator.hasNext()) {
                    File file = ((HashMap.Entry<String, File>) fileToBeLoadIterator.next()).getValue();
                    mStorageManagerListener.onFileUpload(file, false);
                    fileToBeLoadIterator.remove();
                }

                Log.d(LOG_TAG, "onConnectionFailed");
                mStorageManagerListener.onConnectionFailed(connectionResult);
            }

            @Override
            public void onConnectionEstablished() {
                Log.d(LOG_TAG, "onConnectionEstablished: isSyncNeeded=" + isSyncNeeded);

                Iterator fileToBeLoadIterator = mFilesToBeUploaded.entrySet().iterator();
                while (fileToBeLoadIterator.hasNext()) {
                    File file = ((HashMap.Entry<String, File>) fileToBeLoadIterator.next()).getValue();
                    mRemoteStorageManager.uploadFileAsync(file);
                    fileToBeLoadIterator.remove();
                }

                for (File file : mFilesToBeUploaded.values()) {
                    mRemoteStorageManager.uploadFileAsync(file);
                }

                if (isSyncNeeded) {
                    isSyncNeeded = false;
                    mRemoteStorageManager.getFileListAsync();
                }
            }

            @Override
            public void onConnectionSuspended() {
                Log.d(LOG_TAG, "onConnectionSuspended");
            }

            @Override
            public void onNewFileDetected(RemoteStorageManager.RemoteDriveFile file) {
                Log.d(LOG_TAG, "onNewFileDetected");
                addRemoteImage(file);
            }
        });

        loadLocalImages();
    }

    public void loadLocalImages() {

        Log.d(LOG_TAG, "loadLocalImages");

        List<Image> localImages = mDataBaseHelper.getImagesFromDb();

        for (Image image : localImages) {
            mImages.put(image.getFileName(), image);
            mStorageManagerListener.onFilesChanged();
        }

        LocalStorageManager localStorageManager = new LocalStorageManager(mContext,
                LocalStorageManager.IMAGE_FOLDER.IMAGE_FOLDER_ICON_CACHE,
                new LocalStorageManager.BitmapLoadedListener() {
                    @Override
                    public void onBitmapLoaded(String fileName) {
                        Log.d(LOG_TAG, "onBitmapLoaded");
                    }
                });

        for (Image image : mImages.values()) {
            File iconCache = localStorageManager.getFileByFileName(image.getFileName());
            image.setBitmap(ImageService.loadIcon(iconCache));
            mStorageManagerListener.onFilesChanged();
        }
    }

    public void addRemoteImage(RemoteStorageManager.RemoteDriveFile file) {
        Log.d(LOG_TAG, "addRemoteImage");

        if (mImages.containsKey(file.getFileName())) {
            if (file.getBitmap() != null
                    && mDiskCacheHelper.getCachedIcon(file.getFileName()) == null) {
                // save icon to disk cache
                mDiskCacheHelper.saveIconToFile(file.getFileName(),
                        file.getBitmap());
            }
        } else {
            // add remote image to database and load save icon in on disk cache
            mDataBaseHelper.addImage(file);
            if (file.getBitmap() != null) {
                mDiskCacheHelper.saveIconToFile(file.getFileName(),
                        file.getBitmap());
            } else {
                //download remote file bitmap
                mRemoteStorageManager.downloadFileAsync(file);
            }

            // update files map
            mImages.put(file.getFileName(), file);
        }

        // notify about new file/icon
        mStorageManagerListener.onFilesChanged();
    }

    public List<Image> getImages() {
        Log.d(LOG_TAG, "getImages");
        return new ArrayList(mImages.values());
    }

    public void doSync() {
        Log.d(LOG_TAG, "doSync");
        isSyncNeeded = true;
        mRemoteStorageManager.connect();
    }

    public void uploadFile(File file) {
        Log.d(LOG_TAG, "uploadFile: " + file.getPath());

        isSyncNeeded = true;

        if (mRemoteStorageManager.isConnected()) {
            mRemoteStorageManager.uploadFileAsync(file);
        } else {
            mFilesToBeUploaded.put(file.getName(), file);
            mRemoteStorageManager.connect();
        }
    }

    public void handleRemoteDriveProblemSolved() {
        Log.d(LOG_TAG, "handleRemoteDriveProblemSolved");
        doSync();
        //mRemoteStorageManager.connect();
    }


}
