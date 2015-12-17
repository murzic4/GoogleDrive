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
        REMOTE_DRIVE_CONNECT_NOTIFICATION,
        REMOTE_DRIVE_LOAD_FILES_REQUEST,
        REMOTE_DRIVE_START
}
