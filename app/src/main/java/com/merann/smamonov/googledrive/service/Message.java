package com.merann.smamonov.googledrive.service;

/**
 * Created by samam_000 on 05.12.2015.
 */
public enum  Message {
        /* configuration messages */
        GET_CONFIGURATION_REQUEST,
        GET_CONFIGURATION_RESPONSE,
        UPDATE_CONFIGURATION_REQUEST,
        UPDATE_CONFIGURATION_RESPONSE,

        /* drive commands */
        REMOTE_DRIVE_CONFIGURATION_UPDATE_NOTIFICATION,
        REMOTE_DRIVE_AUTHENTICATION_PERFORM_REQUEST,
        REMOTE_DRIVE_AUTHENTICATION_PERFORM_RESPONSE,
        REMOTE_DRIVE_CONNECT_REQUEST,
        REMOTE_DRIVE_CONNECT_RESPONSE,
        REMOTE_DRIVE_CONNECT_NOTIFICATION,
        REMOTE_DRIVE_DISCONNECT_NOTIFICATION,
        REMOTE_DRIVE_UPLOAD_FILE_REQUEST,
        REMOTE_DRIVE_UPLOAD_FILE_RESPONSE,
        REMOTE_DRIVE_LOAD_FILES_REQUEST,
        REMOTE_DRIVE_NEW_FILE_NOTIFY,
        REMOTE_DRIVE_DO_SYNC,
        REMOTE_DRIVE_START,

        /* undefined command */
        UNDEFINED_COMMAND
}
